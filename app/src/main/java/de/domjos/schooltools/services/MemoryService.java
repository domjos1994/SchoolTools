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
import android.content.Intent;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.*;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQuery;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQueryTraining;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;

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
        SQLite sqLite = MainActivity.globals.getSqLite();
        if(sqLite != null) {
            for(Memory memory : sqLite.getCurrentMemories()) {
                try {
                    if(Helper.compareDateWithCurrentDate(ConvertHelper.convertStringToDate(memory.getDate(), this.getApplicationContext()))) {
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

                        MessageHelper.showNotification(this.getApplicationContext(), memory.getTitle(), memory.getDescription(), R.mipmap.ic_launcher_round, linkedIntent, 99);
                    } else {
                        if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
                            MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                        }
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getApplicationContext());
                    if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    }
                }
            }

            for(LearningCardQuery query : this.loadPeriodicLearningCardQueries()) {
                MessageHelper.showNotification(this.getApplicationContext(), query.getTitle(), getApplicationContext().getString(R.string.learningCard_query_memory) + query.getTitle(), R.mipmap.ic_launcher_round, intent, 99);
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
                        if(training.getLearningCardQuery().getId()==learningCardQuery.getId()) {
                            learningCardQueries.add(learningCardQuery);
                        }
                    }
                }
            }
        }
        return learningCardQueries;
    }
}
