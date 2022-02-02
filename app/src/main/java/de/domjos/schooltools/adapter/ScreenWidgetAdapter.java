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
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltools.helper.Helper;

public class ScreenWidgetAdapter extends ArrayAdapter<BaseDescriptionObject> {
    private Context context;
    private int icon;

    public ScreenWidgetAdapter(Context context, int icon, ArrayList<BaseDescriptionObject> objects) {
        super(context, R.layout.screen_widget_item, objects);
        this.context = context;
        this.icon = icon;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.screen_widget_item);
        BaseDescriptionObject baseDescriptionObject = this.getItem(position);

        AppCompatImageView ivIcon = rowView.findViewById(R.id.ivScreenWidgetIcon);
        TextView lblToDoTitle = rowView.findViewById(R.id.lblScreenWidgetTitle);
        AppCompatImageView ivCheck = rowView.findViewById(R.id.ivScreenWidgetCheck);

        if(baseDescriptionObject!=null) {
            if(ivIcon!=null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivIcon.setImageDrawable(this.context.getDrawable(icon));
                } else {
                    ivIcon.setImageDrawable(this.context.getResources().getDrawable(icon));
                }
            }

            if(lblToDoTitle!=null) {
                lblToDoTitle.setText(baseDescriptionObject.getTitle());
            }
            if(ivCheck!=null) {
                if(baseDescriptionObject instanceof ToDo) {
                    if (((ToDo) baseDescriptionObject).isSolved()) {
                        ivCheck.setVisibility(View.VISIBLE);
                    } else {
                        ivCheck.setVisibility(View.GONE);
                    }
                } else {
                    ivCheck.setVisibility(View.GONE);
                }
            }
        }

        return rowView;
    }
}
