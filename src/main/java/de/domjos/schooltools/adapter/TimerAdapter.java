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
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Timer-Adapter
 * @see de.domjos.schooltools.activities.TimerActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class TimerAdapter extends ArrayAdapter<TimerEvent> {
    private Context context;

    public TimerAdapter(Context context, int resource, ArrayList<TimerEvent> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.timer_item);
        TimerEvent timerEvent = this.getItem(position);

        TextView lblToDoTitle = rowView.findViewById(R.id.lblTimerTitle);

        if(timerEvent!=null) {
            if(lblToDoTitle!=null) {
                lblToDoTitle.setText(timerEvent.getTitle());
            }
        }

        return rowView;
    }
}
