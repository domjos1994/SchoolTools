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
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Test-Adapter
 * @see de.domjos.schooltools.activities.MarkEntryActivity
 * @author Dominic Joas
 * @see 1.0
 */
public class TestAdapter extends ArrayAdapter<Test> {
    private Context context;

    public TestAdapter(Context context, int resource, ArrayList<Test> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.mark_item);
        Test test = this.getItem(position);

        TextView lblTestTitle = rowView.findViewById(R.id.lblTestTitle);
        TextView lblTestMark = rowView.findViewById(R.id.lblTestMark);

        if(test!=null) {
            if(lblTestTitle!=null) {
                lblTestTitle.setText(test.getTitle());
            }
            if(lblTestMark!=null) {
                lblTestMark.setText(String.valueOf(test.getMark()));
            }
        }

        return rowView;
    }
}
