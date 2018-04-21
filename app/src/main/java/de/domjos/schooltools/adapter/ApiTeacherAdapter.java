
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Teacher-List in the ApiActivity
 * @see de.domjos.schooltools.activities.ApiActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class ApiTeacherAdapter extends ArrayAdapter<Map.Entry<Teacher, Boolean>> {
    private Context context;

    public ApiTeacherAdapter(Context context, int resource, ArrayList<Map.Entry<Teacher, Boolean>> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.api_teacher_item);
        final Map.Entry<Teacher, Boolean> entry = this.getItem(position);

        CheckBox chkSelected = (CheckBox) rowView.findViewById(R.id.chkSelected);
        TextView lblFirstName = (TextView) rowView.findViewById(R.id.lblFirstName);
        TextView lblLastName = (TextView) rowView.findViewById(R.id.lblLastName);

        if(entry!=null) {
            chkSelected.setChecked(entry.getValue());
            chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entry.setValue(isChecked);
                }
            });
            lblFirstName.setText(entry.getKey().getFirstName());
            lblLastName.setText(entry.getKey().getLastName());
        }

        return rowView;
    }
}
