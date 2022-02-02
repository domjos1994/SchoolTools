/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.SubjectHourAdapter;
import de.domjos.schooltoolslib.model.timetable.Day;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.PupilHour;
import de.domjos.schooltoolslib.model.timetable.TeacherHour;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.customwidgets.model.ScreenWidget;

public final class TimeTableEventScreenWidget extends ScreenWidget {
    private SubjectHourAdapter timeTableEventAdapter;

    public TimeTableEventScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public void init() {
        ListView lvTodayCurrentTimeTable = view.findViewById(R.id.lvTodayCurrentTimeTableEvents);
        this.timeTableEventAdapter = new SubjectHourAdapter(super.activity, R.layout.timetable_subject_item, new ArrayList<>());
        lvTodayCurrentTimeTable.setAdapter(this.timeTableEventAdapter);
        this.timeTableEventAdapter.notifyDataSetChanged();
    }

    public void initCurrentTimeTableEvent() {
        List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("");
        if(timeTables!=null) {
            if(!timeTables.isEmpty()) {
                for(TimeTable timeTable : timeTables) {
                    if(timeTable.isCurrentTimeTable()) {
                        Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
                        calendar.setTime(new Date());
                        int position = calendar.get(Calendar.DAY_OF_WEEK);
                        position = position-1;
                        if(position==0) {
                            position = 7;
                        }

                        Day[] days = timeTable.getDays();
                        for(Day day : days) {
                            if(day!=null) {
                                int dayPos = day.getPositionInWeek();
                                if (dayPos == position) {
                                    Date date = new Date();
                                    if (day.getPupilHour() != null) {
                                        int counter = 0;
                                        for (Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                            Date start = de.domjos.customwidgets.utils.ConvertHelper.convertStringTimeToDate(super.activity.getApplicationContext(), entry.getKey().getStart(), R.mipmap.ic_launcher_round);
                                            Date end = de.domjos.customwidgets.utils.ConvertHelper.convertStringTimeToDate(super.activity.getApplicationContext(), entry.getKey().getEnd(), R.mipmap.ic_launcher_round);

                                            if(start != null && end != null)  {
                                                boolean isAfterStart = start.before(date);
                                                boolean isBeforeEnd = end.after(date);

                                                if(isAfterStart && isBeforeEnd) {
                                                    timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getSubject()));

                                                    if(day.getPupilHour().size()-1>counter) {
                                                        Object[] objects = day.getPupilHour().keySet().toArray();
                                                        Hour hour = (Hour)objects[counter+1];
                                                        if(day.getPupilHour().values().toArray()[counter] instanceof PupilHour) {
                                                            PupilHour mapEntry = (PupilHour) day.getPupilHour().values().toArray()[counter+1];
                                                            timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(hour, mapEntry.getSubject()));
                                                        }
                                                    }
                                                }
                                            }
                                            counter++;
                                        }
                                    }
                                    if (day.getTeacherHour() != null) {
                                        int counter = 0;
                                        for (Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                                            Date start = de.domjos.customwidgets.utils.ConvertHelper.convertStringTimeToDate(super.activity.getApplicationContext(), entry.getKey().getStart(), R.mipmap.ic_launcher_round);
                                            Date end = ConvertHelper.convertStringTimeToDate(super.activity.getApplicationContext(), entry.getKey().getEnd(), R.mipmap.ic_launcher_round);

                                            if(start != null && end != null)  {
                                                boolean isAfterStart = start.before(date);
                                                boolean isBeforeEnd = end.after(date);

                                                if(isAfterStart && isBeforeEnd) {
                                                    timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getSubject()));

                                                    if(day.getTeacherHour().size()-1>counter) {
                                                        Object[] objects = day.getTeacherHour().keySet().toArray();
                                                        Hour hour = (Hour)objects[counter+1];
                                                        if(day.getTeacherHour().values().toArray()[counter+1] instanceof TeacherHour) {
                                                            TeacherHour mapEntry = (TeacherHour) day.getTeacherHour().values().toArray()[counter+1];
                                                            timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(hour, mapEntry.getSubject()));
                                                        }
                                                    }
                                                }
                                            }
                                            counter++;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}
