/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Builder;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.MarkEntryActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.activities.TimerActivity;
import de.domjos.schooltools.activities.TimerEntryActivity;
import de.domjos.schooltools.activities.ToDoActivity;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

public class MemoryService extends IntentService {

    public MemoryService() {
        super("MemoryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = 1;
        for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
            try {
                if(Helper.compareDateWithCurrentDate(Converter.convertStringToDate(memory.getDate()))) {
                    Builder builder = new Builder(this.getApplicationContext());
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setLights(0xFFff0000, 500, 500);
                    builder.setContentTitle(memory.getTitle());
                    builder.setContentText(memory.getDescription());
                    Intent linkedIntent = null;
                    switch (memory.getType()) {
                        case Note:
                            linkedIntent = new Intent(this.getApplicationContext(), NoteActivity.class);
                            break;
                        case Test:
                            linkedIntent = new Intent(this.getApplicationContext(), MarkEntryActivity.class);
                            linkedIntent.putExtra("id", memory.getId());
                            linkedIntent.putExtra("enabled", false);
                            break;
                        case toDo:
                            linkedIntent = new Intent(this.getApplicationContext(), ToDoActivity.class);
                            break;
                        case timerEvent:
                            linkedIntent = new Intent(this.getApplicationContext(), TimerActivity.class);
                            linkedIntent.putExtra("date", memory.getDate());
                            break;
                    }

                    builder.setContentIntent(PendingIntent.getActivity(this.getApplicationContext(), 99, linkedIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    if(notificationManager!=null) {
                        notificationManager.notify(id, builder.build());
                    }
                    id++;
                } else {
                    if(MainActivity.settings.isDeleteMemories()) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    }
                }
            } catch (Exception ex) {
                Helper.printException(this.getApplicationContext(), ex);
                if(MainActivity.settings.isDeleteMemories()) {
                    MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                }
            }
        }

    }
}
