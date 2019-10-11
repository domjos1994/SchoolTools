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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Subject-Adapter
 * @see de.domjos.schooltools.activities.TimeTableSubjectActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class SubjectHourAdapter extends ArrayAdapter<Map.Entry<Hour, Subject>> {
    private Context context;

    public SubjectHourAdapter(Context context, int resource, ArrayList<Map.Entry<Hour, Subject>> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_subject_hour_item);
        Map.Entry<Hour, Subject> entry = this.getItem(position);

        TextView lblSubjectTitle = rowView.findViewById(R.id.lblSubjectTitle);
        TextView lblSubjectAlias = rowView.findViewById(R.id.lblSubjectAlias);
        TextView lblHour = rowView.findViewById(R.id.lblHour);
        LinearLayout ll = rowView.findViewById(R.id.item);

        if(entry!=null) {
            if(lblSubjectTitle!=null) {
                lblSubjectTitle.setText(entry.getValue().getTitle());
            }
            if(lblSubjectAlias!=null) {
                lblSubjectAlias.setText(entry.getValue().getAlias());
            }
            if(lblHour!=null) {
                lblHour.setText(String.format("%s%n%s", entry.getKey().getStart(), entry.getKey().getEnd()));
            }
            if(ll!=null) {
                ll.setBackgroundColor(Integer.parseInt(entry.getValue().getBackgroundColor()));
            }
        }

        return rowView;
    }
}
