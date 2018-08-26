package de.domjos.schooltools.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.timetable.Day;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.core.model.timetable.TimeTable;

public class TimeTableNotificationService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TimeTableNotificationService() {
        super("TimeTableNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if(notificationManager!=null) {
            List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("");
            for(TimeTable timeTable : timeTables) {
                if(timeTable.isShowNotifications()) {
                    Day[] days = timeTable.getDays();
                    if(days!=null) {
                        for(Day day : days) {
                            if(day!=null) {
                                Calendar calendar = GregorianCalendar.getInstance(Locale.GERMAN);
                                calendar.setTime(new Date());
                                int dayInWeek =  calendar.get(Calendar.DAY_OF_WEEK)-1;
                                if(dayInWeek==0) {
                                    dayInWeek = 6;
                                }
                                if(dayInWeek == day.getPositionInWeek()) {

                                    if(day.getTeacherHour()!=null) {
                                        for(Hour hour : day.getTeacherHour().keySet()) {
                                            int startHour = Integer.parseInt(hour.getStart().split(":")[0]);
                                            int startMinute = Integer.parseInt(hour.getStart().split(":")[1]);
                                            int endHour = Integer.parseInt(hour.getEnd().split(":")[0]);
                                            int endMinute = Integer.parseInt(hour.getEnd().split(":")[1]);

                                            Calendar start = GregorianCalendar.getInstance();
                                            start.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), startHour, startMinute);
                                            Calendar end = GregorianCalendar.getInstance();
                                            end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), endHour, endMinute);
                                            if(start.compareTo(calendar)<=0 && end.compareTo(calendar)>=0) {
                                                notificationManager.notify(1, this.getNotification(day.getPupilHour().get(hour).getKey().getAlias()).build());
                                                break;
                                            }
                                        }
                                    }

                                    if(day.getPupilHour()!=null) {
                                        for(Hour hour : day.getPupilHour().keySet()) {
                                            int startHour = Integer.parseInt(hour.getStart().split(":")[0]);
                                            int startMinute = Integer.parseInt(hour.getStart().split(":")[1]);
                                            int endHour = Integer.parseInt(hour.getEnd().split(":")[0]);
                                            int endMinute = Integer.parseInt(hour.getEnd().split(":")[1]);

                                            Calendar start = GregorianCalendar.getInstance();
                                            start.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), startHour, startMinute);
                                            Calendar end = GregorianCalendar.getInstance();
                                            end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), endHour, endMinute);
                                            if(start.compareTo(calendar)<=0 && end.compareTo(calendar)>=0) {
                                                notificationManager.notify(1, this.getNotification(day.getPupilHour().get(hour).getKey().getAlias()).build());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private NotificationCompat.Builder getNotification(String subject) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), NotificationChannel.DEFAULT_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLights(0xFFff0000, 500, 500);
        builder.setContentTitle(subject);
        builder.setContentText(subject);
        builder.setContentIntent(PendingIntent.getActivity(this.getApplicationContext(), 99, new Intent(getApplicationContext(), TimeTable.class), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder;
    }
}
