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
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Subject-Adapter
 * @see de.domjos.schooltools.activities.TimeTableSubjectActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class SubjectAdapter extends ArrayAdapter<Subject> {
    private Context context;

    public SubjectAdapter(Context context, int resource, ArrayList<Subject> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_subject_item);
        Subject entry = this.getItem(position);

        TextView lblSubjectTitle = rowView.findViewById(R.id.lblSubjectTitle);
        TextView lblSubjectAlias = rowView.findViewById(R.id.lblSubjectAlias);
        LinearLayout ll = rowView.findViewById(R.id.item);

        if(entry!=null) {
            if(lblSubjectTitle!=null) {
                lblSubjectTitle.setText(entry.getTitle());
            }
            if(lblSubjectAlias!=null) {
                lblSubjectAlias.setText(entry.getAlias());
            }
            if(ll!=null) {
                ll.setBackgroundColor(Integer.parseInt(entry.getBackgroundColor()));
            }
        }

        return rowView;
    }
}
