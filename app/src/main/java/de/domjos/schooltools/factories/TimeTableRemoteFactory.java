/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.factories;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.timetable.Day;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.PupilHour;
import de.domjos.schooltoolslib.model.timetable.TeacherHour;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;

/**
 * Factory to load TimeTables in widget
 * @see de.domjos.schooltools.widgets.TimeTableWidget
 * @see de.domjos.schooltools.services.TimeTableWidgetService
 * @author Dominic Joas
 * @version 0.1
 */
public class TimeTableRemoteFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Map.Entry<String, Integer>> timeTableRow;
    private SQLite sqLite;
    private Context context;
    private int appWidgetId;
    private final static int DEFAULT_COLOR = Color.BLACK;

    public TimeTableRemoteFactory(Context context, Intent intent) {
        super();

        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        this.sqLite = new SQLite(this.context);
        this.timeTableRow = new LinkedList<>();
    }

    @Override
    public void onCreate() {
        this.reloadTimeTable();
    }

    @Override
    public void onDataSetChanged() {
        this.reloadTimeTable();
    }

    @Override
    public void onDestroy() {
        this.timeTableRow.clear();
    }

    @Override
    public int getCount() {
        return this.timeTableRow.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.timetable_widget);
        String callItem = this.timeTableRow.get(position).getKey();
        row.setTextViewText(R.id.lblHeader, callItem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.changeSize(row);
        }

        int color = this.timeTableRow.get(position).getValue();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(color==context.getColor(R.color.Gray)) {
                row.setTextColor(R.id.lblHeader, TimeTableRemoteFactory.DEFAULT_COLOR);
            } else {
                row.setTextColor(R.id.lblHeader, color);
            }
        } else {
            if(color==context.getResources().getColor(R.color.Gray)) {
                row.setTextColor(R.id.lblHeader,  TimeTableRemoteFactory.DEFAULT_COLOR);
            } else {
                row.setTextColor(R.id.lblHeader, color);
            }
        }
        return row;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void changeSize(RemoteViews row) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = manager.getAppWidgetOptions(appWidgetId);
            final int minWidth = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            if(minWidth==120) {
                row.setTextViewTextSize(R.id.lblHeader, 0, 12);
            } else {
                row.setTextViewTextSize(R.id.lblHeader, 0, 14);
            }
        } else {
            row.setTextViewTextSize(R.id.lblHeader, 0, 14);
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return this.timeTableRow.indexOf(this.timeTableRow.get(position));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void reloadTimeTable() {
        try {
            this.timeTableRow.clear();

            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_hour),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_mon),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_tue),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_wed),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_thu),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_fri),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_sat),  TimeTableRemoteFactory.DEFAULT_COLOR));
            this.timeTableRow.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.timetable_days_sun),  TimeTableRemoteFactory.DEFAULT_COLOR));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int id = preferences.getInt("tt_id_" + appWidgetId, -1);
            List<TimeTable> timeTables = this.sqLite.getTimeTables("ID=" + id);
            if(timeTables!=null) {
                if(!timeTables.isEmpty()) {
                    TimeTable timeTable = timeTables.get(0);
                    Day[] days = timeTable.getDays();

                    String[] strDays = new String[7];
                    Integer[] intColors = new Integer[7];

                    int j = 0;
                    for(int i = 0; i<=6; i++) {
                        if(days[i]!=null) {
                            if(days[i].getPupilHour()!=null) {
                                if(j<=days[i].getPupilHour().size()-1) {
                                    j = days[i].getPupilHour().size()-1;
                                }
                            }
                        }
                    }

                    for(int k = 0; k<=j; k++) {
                        for(int i = 0; i<=6; i++) {
                            strDays[i] = "";
                            intColors[i] =  TimeTableRemoteFactory.DEFAULT_COLOR;
                        }
                        for(int i = 0; i<=6; i++) {
                            if(days[i]!=null) {
                                Object[] objArray = days[i].getPupilHour().keySet().toArray();
                                if(objArray.length-1>=k) {
                                    Hour hour = (Hour) objArray[k];
                                    if(days[i]!=null) {
                                        if(days[i].getPupilHour()!=null) {
                                            for(Map.Entry<Hour, PupilHour> entry : days[i].getPupilHour().entrySet()) {
                                                if(entry.getKey().getStart().equals(hour.getStart()) && entry.getKey().getEnd().equals(hour.getEnd())) {
                                                    if(entry.getValue().getSubject()!=null) {
                                                        if(entry.getValue().getRoomNumber().equals("")) {
                                                            strDays[i] = entry.getValue().getSubject().getAlias().trim();
                                                        } else {
                                                            strDays[i] = entry.getValue().getSubject().getAlias().trim() + "\n" + entry.getValue().getRoomNumber();
                                                        }

                                                        intColors[i] = Integer.parseInt(entry.getValue().getSubject().getBackgroundColor());
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        if(days[i].getTeacherHour()!=null) {
                                            for(Map.Entry<Hour, TeacherHour> entry : days[i].getTeacherHour().entrySet()) {
                                                if(entry.getKey().getStart().equals(hour.getStart()) && entry.getKey().getEnd().equals(hour.getEnd())) {
                                                    if(entry.getValue().getSubject()!=null) {
                                                        if(entry.getValue().getRoomNumber().equals("")) {
                                                            strDays[i] = entry.getValue().getSubject().getAlias().trim();
                                                        } else {
                                                            strDays[i] = entry.getValue().getSubject().getAlias().trim() + "\n" + entry.getValue().getRoomNumber();
                                                        }

                                                        intColors[i] = Integer.parseInt(entry.getValue().getSubject().getBackgroundColor());
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(String.valueOf(k+1),  TimeTableRemoteFactory.DEFAULT_COLOR));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[0], intColors[0]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[1], intColors[1]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[2], intColors[2]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[3], intColors[3]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[4], intColors[4]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[5], intColors[5]));
                        this.timeTableRow.add(new AbstractMap.SimpleEntry<>(strDays[6], intColors[6]));
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }
}
