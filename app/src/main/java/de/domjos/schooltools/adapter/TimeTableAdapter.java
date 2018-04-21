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
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Time-Table-Adapter
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableAdapter extends ArrayAdapter<TimeTable> {
    private Context context;

    public TimeTableAdapter(Context context, int resource, ArrayList<TimeTable> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_item);
        TimeTable entry = this.getItem(position);

        TextView lblTimeTableTitle = rowView.findViewById(R.id.lblTimeTableTitle);
        TextView lblTimeTableClass = rowView.findViewById(R.id.lblTimeTableClass);
        TextView lblTimeTableYear = rowView.findViewById(R.id.lblTimeTableYear);

        if(entry!=null) {
            if(lblTimeTableTitle!=null) {
                lblTimeTableTitle.setText(entry.getTitle());
            }
            if(lblTimeTableClass!=null) {
                if(entry.getSchoolClass()!=null) {
                    lblTimeTableClass.setText(entry.getSchoolClass().getTitle());
                }
            }
            if(lblTimeTableYear!=null) {
                if(entry.getYear()!=null) {
                    lblTimeTableYear.setText(entry.getYear().getTitle());
                }
            }
        }

        return rowView;
    }
}
