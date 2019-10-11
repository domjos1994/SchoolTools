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
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Year-Adapter
 * @see de.domjos.schooltools.activities.MarkYearActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class YearAdapter extends ArrayAdapter<Year> {
    private Context context;

    public YearAdapter(Context context, int resource, ArrayList<Year> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.mark_year_item);
        Year year = this.getItem(position);

        TextView lblSubjectTitle = rowView.findViewById(R.id.lblMarkYear);

        if(year!=null) {
            if(lblSubjectTitle!=null) {
                lblSubjectTitle.setText(year.getTitle());
            }
        }

        return rowView;
    }
}
