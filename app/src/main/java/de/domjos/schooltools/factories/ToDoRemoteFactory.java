/*
 * Copyright (C) 2018  Dominic Joas
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
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.helper.SQLite;

public class ToDoRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<String> toDos;
    private int appWidgetId;

    public ToDoRemoteFactory(Context context, Intent intent) {
        this.toDos = new LinkedList<>();
        this.context =  context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        this.sqLite = new SQLite(this.context, "schoolTools.db", 1);
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
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.todo_widget);
        String callItem = this.toDos.get(position);
        row.setTextViewText(R.id.lblHeader, callItem);
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
        this.toDos.clear();
        List<ToDo> toDos = this.sqLite.getToDos("");
        Map<ToDo, Integer> todoMap = new HashMap<>();
        for(ToDo toDo : toDos) {
            todoMap.put(toDo, toDo.getImportance());
        }

        Object[] a = todoMap.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<ToDo, Integer>) o2).getValue().compareTo(((Map.Entry<ToDo, Integer>) o1).getValue());
            }
        });
        for(Object obj : a) {
            Map.Entry<ToDo, Integer> entry = (Map.Entry<ToDo, Integer>) obj;

            if(toDos.size() % 5 == 0) {
                break;
            }
            this.toDos.add(entry.getKey().getTitle());
        }
    }
}
