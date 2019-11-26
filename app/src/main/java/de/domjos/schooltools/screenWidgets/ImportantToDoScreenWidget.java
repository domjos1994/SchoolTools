/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.ScreenWidgetAdapter;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.customwidgets.model.ScreenWidget;
import de.domjos.schooltools.helper.Helper;

public final class ImportantToDoScreenWidget extends ScreenWidget {
    private ScreenWidgetAdapter screenWidgetAdapter;

    public ImportantToDoScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        ListView lvImportantToDos = view.findViewById(R.id.lvImportantToDos);
        this.screenWidgetAdapter = new ScreenWidgetAdapter(super.activity, R.drawable.ic_done_all_black_24dp, new ArrayList<>());
        lvImportantToDos.setAdapter(this.screenWidgetAdapter);
        this.screenWidgetAdapter.notifyDataSetChanged();

        lvImportantToDos.setOnTouchListener(Helper.addOnTouchListenerForScrolling());
    }

    public void addToDos() {
        this.screenWidgetAdapter.clear();
        if(super.view.getVisibility()==View.VISIBLE) {
            List<ToDo> toDos = MainActivity.globals.getSqLite().getToDos("");
            Map<ToDo, Integer> todoMap = new HashMap<>();
            for(ToDo toDo : toDos) {
                todoMap.put(toDo, toDo.getImportance());
            }

            Object[] a = todoMap.entrySet().toArray();
            Arrays.sort(a, (o1, o2) -> {
                if(o2 instanceof Map.Entry && o1 instanceof Map.Entry) {
                    Map.Entry entry1 = (Map.Entry) o1;
                    Map.Entry entry2 = (Map.Entry) o2;

                    if(entry1.getValue() instanceof Integer && entry2.getValue() instanceof Integer) {
                        return ((Integer) entry1.getValue()).compareTo(((Integer) entry2.getValue()));
                    }
                }
                return -1;
            });
            toDos.clear();
            for(Object obj : a) {
                if(obj instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry) obj;
                    if(entry.getKey() instanceof ToDo) {
                        this.screenWidgetAdapter.add((ToDo) entry.getKey());
                    }
                }

                if(this.screenWidgetAdapter.getCount() % 5 == 0) {
                    break;
                }
            }
            if(this.screenWidgetAdapter.isEmpty()) {
                ToDo toDo = new ToDo();
                toDo.setTitle(super.activity.getString(R.string.main_noEntry));
                this.screenWidgetAdapter.add(toDo);
            }
        }
    }
}
