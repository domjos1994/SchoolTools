/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.factories;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.helper.SQLite;

public class ToDoRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<String> toDos;

    public ToDoRemoteFactory(Context context) {
        this.toDos = new LinkedList<>();
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

                    this.toDos.add(((ToDo) entry.getKey()).getTitle());
                }
            }
        }
    }
}
