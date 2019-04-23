package de.domjos.schooltools.adapter.syncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

import static android.provider.CalendarContract.Calendars.CAL_ACCESS_OWNER;
import static android.provider.CalendarContract.Calendars.CALENDAR_DISPLAY_NAME;
import static android.provider.CalendarContract.Calendars.CALENDAR_COLOR;
import static android.provider.CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL;
import static android.provider.CalendarContract.Calendars.OWNER_ACCOUNT;
import static android.provider.SyncStateContract.Columns.ACCOUNT_NAME;
import static android.provider.SyncStateContract.Columns.ACCOUNT_TYPE;
import static android.provider.CalendarContract.Calendars.VISIBLE;
import static android.provider.CalendarContract.Calendars.NAME;

import static android.provider.CalendarContract.Events._ID;
import static android.provider.CalendarContract.Events.TITLE;
import static android.provider.CalendarContract.Events.DESCRIPTION;
import static android.provider.CalendarContract.Events.DTSTART;
import static android.provider.CalendarContract.Reminders;


public class CalendarSyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver contentResolver;
    private long calendar_id;

    public CalendarSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.contentResolver = this.getContext().getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            String name = MainActivity.globals.getUserSettings().getSyncCalendarName();
            Map<Long, String> calendars = this.listCalendars();
            if(!calendars.values().contains(name)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ACCOUNT_NAME, account.name);
                contentValues.put(ACCOUNT_TYPE, account.type);
                contentValues.put(NAME, name);
                contentValues.put(CALENDAR_DISPLAY_NAME, name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    contentValues.put(CALENDAR_COLOR, getContext().getColor(R.color.colorPrimary));
                } else {
                    contentValues.put(CALENDAR_COLOR, getContext().getResources().getColor(R.color.colorPrimary));
                }
                contentValues.put(CALENDAR_ACCESS_LEVEL, CAL_ACCESS_OWNER);
                contentValues.put(OWNER_ACCOUNT, account.name);
                contentValues.put(VISIBLE, "true");
                provider.insert(asSyncAdapter(CalendarContract.Calendars.CONTENT_URI, account.name, account.type), contentValues);
                calendars = this.listCalendars();
            }

            for(Map.Entry<Long, String> entry : calendars.entrySet()) {
                if(entry.getValue().equals(name)) {
                    this.calendar_id = entry.getKey();
                    break;
                }
            }

            List<TimerEvent> calendarTimerEvents = this.getEventsFromCalendar(provider, account);
            List<TimerEvent> savedTimerEvents = MainActivity.globals.getSqLite().getTimerEvents("");
            for(TimerEvent calendarTimerEvent : calendarTimerEvents) {
                boolean isAvailable = false;
                for(TimerEvent savedTimerEvent : savedTimerEvents) {
                    if(calendarTimerEvent.getTitle().trim().toLowerCase().equals(savedTimerEvent.getTitle().trim().toLowerCase())) {
                        if(calendarTimerEvent.getEventDate().compareTo(savedTimerEvent.getEventDate())==0) {
                            isAvailable = true;
                        }
                    }
                }

                if(!isAvailable) {
                    MainActivity.globals.getSqLite().insertOrUpdateTimerEvent(calendarTimerEvent);
                }
            }
            savedTimerEvents = MainActivity.globals.getSqLite().getTimerEvents("");
            for(TimerEvent savedTimerEvent : savedTimerEvents) {
                boolean isAvailable = false;
                for(TimerEvent calendarTimerEvent : calendarTimerEvents) {
                    if(calendarTimerEvent.getTitle().trim().toLowerCase().equals(savedTimerEvent.getTitle().trim().toLowerCase())) {
                        if(calendarTimerEvent.getEventDate().compareTo(savedTimerEvent.getEventDate())==0) {
                            isAvailable = true;
                        }
                    }
                }

                if(!isAvailable) {
                    this.saveTimeEventToCalendar(savedTimerEvent, provider, account);
                }
            }
        } catch (Exception ex) {
            Helper.printException(this.getContext(), ex);
        }
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }


    private Map<Long, String> listCalendars() {
        Map<Long, String> calendars = new LinkedHashMap<>();

        String[] selection = new String[] { "_id", "calendar_displayName" };
        Uri uri = Uri.parse("content://com.android.calendar/calendars");
        Cursor cursor = this.contentResolver.query(uri, selection, null, null, null);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                calendars.put(cursor.getLong(0), cursor.getString(1));
            }
            cursor.close();
        }

        return calendars;
    }

    private List<TimerEvent> getEventsFromCalendar(ContentProviderClient contentProvider, Account account) throws Exception {
        List<TimerEvent> timerEvents = new LinkedList<>();
        String[] projection = new String[]{_ID, TITLE, DESCRIPTION, DTSTART};
        Cursor cursor = contentProvider.query(asSyncAdapter(CalendarContract.Events.CONTENT_URI, account.name, account.type), projection, null, null, null);
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                timerEvent.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                timerEvent.setEventDate(new Date(cursor.getLong(cursor.getColumnIndex(DTSTART))));

                String[] alertProjection = new String[]{};
                String selection = Reminders.EVENT_ID + "=?";
                String[] arguments = new String[]{cursor.getString(cursor.getColumnIndex(_ID))};
                Cursor alertCursor = contentProvider.query(asSyncAdapter(CalendarContract.Reminders.CONTENT_URI, account.name, account.type), alertProjection, selection, arguments, null);
                if(alertCursor!=null) {
                    while (alertCursor.moveToNext()) {
                        int method = alertCursor.getInt(alertCursor.getColumnIndex(Reminders.METHOD));
                        long minutes = alertCursor.getLong(alertCursor.getColumnIndex(Reminders.MINUTES));
                        if(method==1) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(timerEvent.getEventDate());
                            cal.add(Calendar.MINUTE, (int) (-1 * minutes));
                            timerEvent.setMemoryDate(cal.getTime());
                            break;
                        }
                    }
                    alertCursor.close();
                }
                timerEvents.add(timerEvent);
            }
            cursor.close();
        }
        return timerEvents;
    }

    private void saveTimeEventToCalendar(TimerEvent timerEvent, ContentProviderClient providerClient, Account account) throws Exception {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, timerEvent.getTitle());
        contentValues.put(DESCRIPTION, timerEvent.getDescription());
        contentValues.put(DTSTART, timerEvent.getEventDate().getTime());
        contentValues.put(CalendarContract.Events.ALL_DAY, "true");
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
        providerClient.insert(asSyncAdapter(CalendarContract.Events.CONTENT_URI, account.name, account.type), contentValues);

        if(timerEvent.getMemoryDate()!=null) {
            long id = -1;
            Cursor cursor = providerClient.query(asSyncAdapter(CalendarContract.Events.CONTENT_URI, account.name, account.type), new String[]{_ID, TITLE}, null, null, null);
            if(cursor!=null) {
                while (cursor.moveToNext()) {
                    if(cursor.getString(1).equals(timerEvent.getTitle())) {
                        id = cursor.getLong(0);
                        break;
                    }
                }
                cursor.close();
            }

            contentValues = new ContentValues();
            contentValues.put(Reminders.EVENT_ID, id);
            contentValues.put(Reminders.METHOD, 1);
            contentValues.put(Reminders.MINUTES, (timerEvent.getEventDate().getTime() - timerEvent.getMemoryDate().getTime()) / (1000 * 60));
            providerClient.insert(asSyncAdapter(Reminders.CONTENT_URI, account.name, account.type), contentValues);
        }
    }
}
