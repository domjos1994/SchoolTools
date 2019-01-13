/*
 * Copyright (C) 2017-2019  Dominic Joas
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
import de.domjos.schooltools.activities.*;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.core.model.learningCard.LearningCardQueryTraining;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
                    Builder builder = new Builder(this.getApplicationContext(), "default");
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
                            linkedIntent.putExtra("id", memory.getID());
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
                    if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getID());
                    }
                }
            } catch (Exception ex) {
                Helper.printException(this.getApplicationContext(), ex);
                if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
                    MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getID());
                }
            }
        }

        for(LearningCardQuery query : this.loadPeriodicLearningCardQueries()) {
            Builder builder = new Builder(this.getApplicationContext(), "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLights(0xFFff0000, 501, 501);
            builder.setContentTitle(query.getTitle());
            builder.setContentText(getApplicationContext().getString(R.string.learningCard_query_memory) + query.getTitle());
            Intent overViewIntent = new Intent(this.getApplicationContext(), LearningCardOverviewActivity.class);
            overViewIntent.putExtra("queryID", query.getID());
            builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 99, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            if(notificationManager!=null) {
                notificationManager.notify(id, builder.build());
            }
        }
    }

    private List<LearningCardQuery> loadPeriodicLearningCardQueries() {
        List<LearningCardQuery> learningCardQueries = new LinkedList<>();
        List<LearningCardQuery> allQueries = MainActivity.globals.getSqLite().getLearningCardQueries("");
        for(LearningCardQuery learningCardQuery : allQueries) {
            if(learningCardQuery.isPeriodic()) {
                int days = learningCardQuery.getPeriod();

                Calendar start = Calendar.getInstance();
                start.setTime(new Date());
                start.add(Calendar.DAY_OF_WEEK, days * -1);
                start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

                Calendar end = Calendar.getInstance();
                end.setTime(new Date());
                end.add(Calendar.DAY_OF_WEEK, days * -1);
                end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 23, 59, 59);


                List<LearningCardQueryTraining> learningCardQueryTrainings = MainActivity.globals.getSqLite().getLearningCardQueryTraining("current_date>" + start.getTimeInMillis() + " AND current_date<=" + end.getTimeInMillis());
                for(LearningCardQueryTraining training : learningCardQueryTrainings) {
                    if(training.getLearningCardQuery()!=null) {
                        if(training.getLearningCardQuery().getID()==learningCardQuery.getID()) {
                            learningCardQueries.add(learningCardQuery);
                        }
                    }
                }
            }
        }
        return learningCardQueries;
    }
}
