
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
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.marklist.MarkListInterface;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Mark-List-Activity
 * @see de.domjos.schooltools.activities.MarkListActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class MarkListAdapter extends ArrayAdapter<Map.Entry<Double, Double>> {
    private Context context;
    private boolean dictatMode;
    private MarkListInterface.ViewMode viewMode;

    public MarkListAdapter(Context context, int resource, ArrayList<Map.Entry<Double, Double>> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    public void setDictatMode(boolean dictatMode) {
        this.dictatMode = dictatMode;
    }

    public void setViewMode(MarkListInterface.ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.marklist_item);
        Map.Entry<Double, Double> entry = this.getItem(position);

        TextView lblFirstColumn = rowView.findViewById(R.id.lblFirstColumn);
        TextView lblLastColumn = rowView.findViewById(R.id.lblLastColumn);

        String points = this.context.getString(R.string.marklist_points);
        String mark = this.context.getString(R.string.marklist_mark);
        String failures = this.context.getString(R.string.marklist_failures);
        String format = "%s %s";

        if(entry != null) {
            if(this.dictatMode) {
                switch (this.viewMode) {
                    case bestMarkFirst:
                        lblFirstColumn.setText(String.format(format, mark, entry.getKey()));
                        lblLastColumn.setText(String.format(format, entry.getValue(), failures));
                        break;
                    case worstMarkFirst:
                        lblFirstColumn.setText(String.format(format, mark, entry.getKey()));
                        lblLastColumn.setText(String.format(format, entry.getValue(), failures));
                        break;
                    case highestPointsFirst:
                        lblFirstColumn.setText(String.format(format, entry.getKey(), failures));
                        lblLastColumn.setText(String.format(format, mark, entry.getValue()));
                        break;
                    case lowestPointsFirst:
                        lblFirstColumn.setText(String.format(format, entry.getKey(), failures));
                        lblLastColumn.setText(String.format(format, mark, entry.getValue()));
                        break;
                }
            } else {
                switch (this.viewMode) {
                    case bestMarkFirst:
                        lblFirstColumn.setText(String.format(format, mark, entry.getKey()));
                        lblLastColumn.setText(String.format(format, entry.getValue(), points));
                        break;
                    case worstMarkFirst:
                        lblFirstColumn.setText(String.format(format, mark, entry.getKey()));
                        lblLastColumn.setText(String.format(format, entry.getValue(), points));
                        break;
                    case highestPointsFirst:
                        lblFirstColumn.setText(String.format(format, entry.getKey(), points));
                        lblLastColumn.setText(String.format(format, mark, entry.getValue()));
                        break;
                    case lowestPointsFirst:
                        lblFirstColumn.setText(String.format(format, entry.getKey(), points));
                        lblLastColumn.setText(String.format(format, mark, entry.getValue()));
                        break;
                }
            }
        }

        return rowView;
    }
}
