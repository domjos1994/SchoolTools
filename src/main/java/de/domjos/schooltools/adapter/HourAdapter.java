
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
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Hour-Activity
 * @see de.domjos.schooltools.activities.TimeTableHourActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class HourAdapter extends ArrayAdapter<Hour> {
    private Context context;

    public HourAdapter(Context context, int resource, ArrayList<Hour> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_hour_item);
        Hour entry = this.getItem(position);

        TextView lblHoursStart = rowView.findViewById(R.id.lblHoursStart);
        TextView lblHoursEnd = rowView.findViewById(R.id.lblHoursEnd);
        TextView lblHoursBreak = rowView.findViewById(R.id.lblHoursBreak);

        if(entry!=null) {
            lblHoursStart.setText(entry.getStart());
            lblHoursEnd.setText(entry.getEnd());

            if(entry.isBreak()) {
                lblHoursBreak.setText(this.context.getString(R.string.timetable_hour_break));
            } else {
                lblHoursBreak.setText(this.context.getString(R.string.timetable_hour_hour));
            }
        }

        return rowView;
    }
}
