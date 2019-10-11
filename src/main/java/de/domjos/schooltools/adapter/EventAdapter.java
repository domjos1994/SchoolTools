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
import android.content.Intent;
import androidx.annotation.NonNull;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.TimerActivity;
import de.domjos.schooltools.helper.Helper;

public class EventAdapter extends ArrayAdapter<Map.Entry<String, String>> {
    private Context context;

    public EventAdapter(Context context, int resource, ArrayList<Map.Entry<String, String>> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.main_today_event);
        final Map.Entry<String, String> entry = this.getItem(position);



        TextView lblTodayEventType = rowView.findViewById(R.id.lblTodayEventType);
        TextView lblTodayEventContent = rowView.findViewById(R.id.lblTodayEventContent);

        if(entry!=null) {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(entry.getKey().equals(context.getString(R.string.main_nav_timer)) || entry.getKey().equals("Er.(" + context.getString(R.string.mark_test) + ")")) {
                        Intent intent = new Intent(context, TimerActivity.class);
                        context.startActivity(intent);
                    }
                }
            });

            if(lblTodayEventType!=null) {
                lblTodayEventType.setText(entry.getKey());
            }

            if(lblTodayEventContent!=null) {
                lblTodayEventContent.setText(entry.getValue());
            }
        }

        return rowView;
    }
}
