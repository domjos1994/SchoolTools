/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.customwidgets.utils.Converter;

/**
 *
 */
public class EventHelper {
    private List<Memory> memoryList;
    private Context context;

    public EventHelper(Context context) {
        this(MainActivity.globals.getSqLite().getCurrentMemories(), context);
    }

    public EventHelper(Memory memory, Context context) {
        this(Collections.singletonList(memory), context);
    }

    private EventHelper(List<Memory> memories, Context context) {
        this.memoryList = memories;
        this.context = context;
    }

    public Intent openCalendar() throws Exception {
        Memory memory = memoryList.get(0);

        if(memory!=null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Converter.convertStringToDate(memory.getDate(), this.context));
            return new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, memory.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, memory.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, memory.getType().toString())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.ALL_DAY, true)
                .putExtra(Intent.EXTRA_TEXT, memory.getType().toString());
        }
        return null;
    }

    public void saveMemoriesToCalendar(Activity curActivity) throws Exception {
        for(Memory memory : this.memoryList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Converter.convertStringToDate(memory.getDate(), this.context));

            String eventUriString = "CONTENT_PARAM://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1);
            eventValues.put(CalendarContract.Events.TITLE, memory.getTitle());
            eventValues.put(CalendarContract.Events.DESCRIPTION, memory.getDescription());
            eventValues.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
            eventValues.put(Intent.EXTRA_TEXT, memory.getType().toString());
            eventValues.put(CalendarContract.Events.ALL_DAY, true);
            eventValues.put("eventTimezone", "UTC/GMT +2:00");
            curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        }
    }
}
