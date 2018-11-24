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
import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.objects.BaseCategoryObject;
import de.domjos.schooltools.helper.Helper;

import java.util.List;

public class LearningCardGroupAdapter extends ArrayAdapter<LearningCardGroup> {
    private Context context;

    public LearningCardGroupAdapter(Context context, int resource, List<LearningCardGroup> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.learning_card_group_item);
        BaseCategoryObject entry = this.getItem(position);

        TextView lblSchoolClass = rowView.findViewById(R.id.lblLearningCardGroupTitle);

        if(entry!=null) {
            lblSchoolClass.setText(entry.getTitle());
        }

        return rowView;
    }
}
