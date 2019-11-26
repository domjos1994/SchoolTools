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
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;

/**
 * Factory to load TimeTables in widget
 * @see de.domjos.schooltools.widgets.ToDoWidget
 * @see de.domjos.schooltools.services.ToDoWidgetService
 * @author Dominic Joas
 * @version 0.1
 */
public class ToDoRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<ToDo> toDos;
    private int appWidgetId;

    public ToDoRemoteFactory(Context context, Intent intent) {
        this.toDos = new LinkedList<>();
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        this.context =  context;
        this.sqLite = new SQLite(this.context);
    }

    @Override
    public void onCreate() {
        this.reloadToDos();
    }

    @Override
    public void onDataSetChanged() {
        this.reloadToDos();
    }

    @Override
    public void onDestroy() {
        this.toDos.clear();
    }

    @Override
    public int getCount() {
        return this.toDos.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        ToDo toDo = toDos.get(position);

        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.todo_widget);
        String callItem = toDo.getTitle() + ":\n" + toDo.getDescription();
        if(toDo.getDescription().isEmpty()) {
            callItem = toDo.getTitle();
        }
        row.setTextViewText(R.id.lblHeader, callItem);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switch (toDo.getImportance()) {
                    case 10:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.DarkRed));
                        break;
                    case 9:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.Red));
                        break;
                    case 8:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.OrangeRed));
                        break;
                    case 7:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.DarkOrange));
                        break;
                    case 6:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.Orange));
                        break;
                    case 5:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.Yellow));
                        break;
                    case 4:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.DarkGreen));
                        break;
                    case 3:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.YellowGreen));
                        break;
                    case 2:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.GreenYellow));
                        break;
                    case 1:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.Green));
                        break;
                    default:
                        row.setTextColor(R.id.lblHeader, this.context.getColor(R.color.LightGreen));
                        break;
                }
            } else {
                switch (toDo.getImportance()) {
                    case 10:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.DarkRed));
                        break;
                    case 9:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.Red));
                        break;
                    case 8:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.OrangeRed));
                        break;
                    case 7:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.DarkOrange));
                        break;
                    case 6:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.Orange));
                        break;
                    case 5:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.Yellow));
                        break;
                    case 4:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.DarkGreen));
                        break;
                    case 3:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.YellowGreen));
                        break;
                    case 2:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.GreenYellow));
                        break;
                    case 1:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.Green));
                        break;
                    default:
                        row.setTextColor(R.id.lblHeader, this.context.getResources().getColor(R.color.LightGreen));
                        break;
                }
            }
        } catch (Exception ex) {
            Helper.printException(context, ex);
        }

        return row;
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
        return this.toDos.indexOf(this.toDos.get(position));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void reloadToDos() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = preferences.getInt("todo_list_id_" + this.appWidgetId, -1);
        boolean solved = preferences.getBoolean("todo_list_solved_" + this.appWidgetId, false);
        String where = "";
        if(id!=-1) {
            where = "toDoList=" + id;
            if(solved) {
                where = where + " AND solved=0";
            }
        } else {
            if(solved) {
                where = "solved=0";
            }
        }

        this.toDos.clear();
        List<ToDo> toDos = this.sqLite.getToDos(where);
        Map<ToDo, Integer> todoMap = new HashMap<>();
        for(ToDo toDo : toDos) {
            todoMap.put(toDo, toDo.getImportance());
        }

        Object[] a = todoMap.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                if(o1 instanceof  Map.Entry && o2 instanceof Map.Entry) {
                    Map.Entry entry1 = (Map.Entry) o1;
                    Map.Entry entry2 = (Map.Entry) o2;

                    if(entry1.getValue() instanceof Integer && entry2.getValue() instanceof Integer) {
                        return ((Integer) entry1.getValue()).compareTo(((Integer) entry2.getValue()));
                    }
                }
                return 0;
            }
        });
        for(Object obj : a) {
            if(obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;

                if(entry.getKey() instanceof  ToDo) {
                    if(toDos.size() % 5 == 0) {
                        break;
                    }

                    this.toDos.add(((ToDo) entry.getKey()));
                }
            }
        }
    }
}
