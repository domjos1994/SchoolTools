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
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.helper.Helper;

import java.util.List;

public class LearningCardAdapter extends ArrayAdapter<LearningCard> {
    private Context context;

    public LearningCardAdapter(Context context, int resource, List<LearningCard> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.learning_card_item);
        LearningCard entry = this.getItem(position);

        AppCompatImageView img = rowView.findViewById(R.id.imgIcon);
        TextView lblTitle = rowView.findViewById(R.id.lblLearningCardTitle);
        TextView lblQuestion = rowView.findViewById(R.id.lblLearningCardQuestion);

        if(entry!=null) {
            lblTitle.setText(entry.getTitle());
            lblQuestion.setText(entry.getQuestion());

            if(entry.getID()==0 && entry.getTitle().equals("") && entry.getQuestion().equals("")) {
                if(img!=null) {
                    img.setImageResource(android.R.drawable.ic_menu_add);
                }
            }
        }

        return rowView;
    }
}
