/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.MarkActivity;
import de.domjos.schooltools.activities.MarkEntryActivity;
import de.domjos.schooltools.activities.MarkListActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.activities.TimeTableEntryActivity;
import de.domjos.schooltools.activities.TimerEntryActivity;
import de.domjos.schooltools.activities.ToDoEntryActivity;
import de.domjos.schooltools.activities.ToDoListActivity;
import de.domjos.schooltools.core.SearchItem;
import de.domjos.schooltools.core.model.mark.SchoolYear;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.helper.Helper;

/**
 * @author Dominic Joas
 */

public class SearchAdapter extends ArrayAdapter<SearchItem> {
    private Context context;

    public SearchAdapter(Context context) {
        super(context, R.layout.search_item, new ArrayList<SearchItem>());
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.search_item);
        final SearchItem entry = this.getItem(position);

        LinearLayout linearLayout = rowView.findViewById(R.id.llSearch);
        TextView lblTitle = rowView.findViewById(R.id.lblItemTitle);
        TextView lblType = rowView.findViewById(R.id.lblItemType);

        if(entry!=null) {
            if(linearLayout!=null) {
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if(entry.getType().equals(context.getString(R.string.main_nav_mark_list))) {
                            intent = new Intent(context, MarkListActivity.class);
                        }
                        if(entry.getType().equals(context.getString(R.string.mark_test))) {
                            intent = new Intent(context, MarkEntryActivity.class);
                            List<SchoolYear> schoolYears = MainActivity.globals.getSqLite().getSchoolYears("");
                            for(SchoolYear schoolYear : schoolYears) {
                                boolean isFound = false;
                                for(Test test : schoolYear.getTests()) {
                                    if(test.getID()==entry.getID()) {
                                        intent.putExtra("subject", schoolYear.getSubject().getTitle());
                                        intent.putExtra("year", schoolYear.getYear().getTitle());
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(isFound) {
                                    break;
                                }
                            }
                        }
                        if(entry.getType().equals(context.getString(R.string.timetable_lesson))) {
                            intent = new Intent(context, MarkActivity.class);
                        }
                        if(entry.getType().equals(context.getString(R.string.main_nav_notes))) {
                            intent = new Intent(context, NoteActivity.class);
                        }
                        if(entry.getType().equals(context.getString(R.string.todo_list))) {
                            intent = new Intent(context, ToDoListActivity.class);
                        }
                        if(entry.getType().equals(context.getString(R.string.main_nav_todo))) {
                            intent = new Intent(context, ToDoEntryActivity.class);
                            List<ToDoList> toDoLists = MainActivity.globals.getSqLite().getToDoLists("");
                            for(ToDoList toDoList : toDoLists) {
                                boolean isFound = false;
                                for(ToDo toDo : toDoList.getToDos()) {
                                    if(toDo.getID()==entry.getID()) {
                                        intent.putExtra("list", toDoList.getTitle());
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(isFound) {
                                    break;
                                }
                            }
                        }
                        if(entry.getType().equals(context.getString(R.string.main_nav_timetable))) {
                            intent = new Intent(context, TimeTableEntryActivity.class);
                        }
                        if(entry.getType().equals(context.getString(R.string.main_nav_timer))) {
                            intent = new Intent(context, TimerEntryActivity.class);
                            intent.putExtra("date", entry.getExtra());
                        }

                        if(intent!=null) {
                            intent.putExtra("id", entry.getID());
                            context.startActivity(intent);
                        }
                    }
                });
            }

            if(lblTitle!=null) {
                lblTitle.setText(entry.getTitle());
            }

            if(lblType!=null) {
                lblType.setText(entry.getType());
            }
        }

        return rowView;
    }
}
