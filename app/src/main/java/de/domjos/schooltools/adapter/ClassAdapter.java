
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
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Class-Activity
 * @see de.domjos.schooltools.activities.TimeTableClassActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class ClassAdapter extends ArrayAdapter<SchoolClass> {
    private Context context;

    public ClassAdapter(Context context, int resource, ArrayList<SchoolClass> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_class_item);
        SchoolClass entry = this.getItem(position);

        TextView lblSchoolClass = (TextView) rowView.findViewById(R.id.lblSchoolClass);

        if(entry!=null) {
            lblSchoolClass.setText(entry.getTitle());
        }

        return rowView;
    }
}
