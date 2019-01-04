/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the ToDo-List-Adapter
 * @see de.domjos.schooltools.activities.ToDoListActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDoListsAdapter extends ArrayAdapter<ToDoList> {
    private Context context;

    public ToDoListsAdapter(Context context, int resource, ArrayList<ToDoList> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.todo_list_item);
        ToDoList toDoList = this.getItem(position);

        TextView lblToDoListTitle = rowView.findViewById(R.id.lblToDoListTitle);

        if(toDoList!=null) {
            if(lblToDoListTitle!=null) {
                lblToDoListTitle.setText(toDoList.getTitle());
            }
        }

        return rowView;
    }
}
