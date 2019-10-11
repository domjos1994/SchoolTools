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
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Teacher-Adapter
 * @see de.domjos.schooltools.activities.TimeTableTeacherActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class TeacherAdapter extends ArrayAdapter<Teacher> {
    private Context context;

    public TeacherAdapter(Context context, int resource, ArrayList<Teacher> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_teacher_item);
        Teacher entry = this.getItem(position);

        TextView lblTeacherName = rowView.findViewById(R.id.lblTeacherName);

        if(entry!=null) {
            if(lblTeacherName!=null) {
                lblTeacherName.setText(String.format("%s %s", entry.getFirstName(), entry.getLastName()));
            }
        }

        return rowView;
    }
}
