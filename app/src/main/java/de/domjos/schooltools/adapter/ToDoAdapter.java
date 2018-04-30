/*
 * Copyright (C) 2017  Dominic Joas
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the ToDo-Adapter
 * @see de.domjos.schooltools.activities.ToDoActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDoAdapter extends ArrayAdapter<ToDo> {
    private Context context;

    public ToDoAdapter(Context context, int resource, ArrayList<ToDo> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.todo_item);
        ToDo toDo = this.getItem(position);

        TextView lblToDoTitle = rowView.findViewById(R.id.lblToDoTitle);
        ImageView ivCheck = rowView.findViewById(R.id.ivCheck);

        if(toDo!=null) {
            if(lblToDoTitle!=null) {
                lblToDoTitle.setText(toDo.getTitle());
            }
            if(ivCheck!=null) {
                if(toDo.isSolved()) {
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivCheck.setVisibility(View.GONE);
                }
            }
        }

        return rowView;
    }
}
