/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;

public class ColorAdapter extends ArrayAdapter<String> {
    private Context context;

    public ColorAdapter(Context context) {
        super(context, R.layout.timetable_subject_color_item, new ArrayList<String>());
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timetable_subject_color_item);
        String entry = this.getItem(position);

        TextView lblText = rowView.findViewById(R.id.lblText);

        if(entry!=null) {
            if(lblText!=null) {
                lblText.setText(entry);
                lblText.setBackgroundColor(this.context.getResources().getColor(getSelectedColor(context, entry, R.color.White)));
            }
        }
        return rowView;
    }

    public static int getSelectedColor(Context context, String color, int def) {
        int colorToUse = def;
        String[] colorNames = context.getResources().getStringArray(R.array.colorNames);
        for(int i=0; i<colorNames.length; i++) {
            if (color.equals(colorNames[i].toLowerCase())) {
                TypedArray ta = context.getResources().obtainTypedArray(R.array.colors);
                colorToUse = ta.getResourceId(i, 0);
                ta.recycle();
                break;
            }
        }
        return colorToUse;
    }
}
