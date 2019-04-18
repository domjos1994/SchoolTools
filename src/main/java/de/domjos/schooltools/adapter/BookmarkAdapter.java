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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Bookmark;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Time-Table-Adapter
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class BookmarkAdapter extends ArrayAdapter<Bookmark> {
    private Context context;

    public BookmarkAdapter(Context context, List<Bookmark> objects) {
        super(context, R.layout.bookmark_item, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.bookmark_item);
        Bookmark entry = this.getItem(position);

        AppCompatImageView ivBookmarkPreview = rowView.findViewById(R.id.ivBookmarkPreview);
        TextView lblBookmarkTitle = rowView.findViewById(R.id.lblBookmarkTitle);
        TextView lblBookmarkTags = rowView.findViewById(R.id.lblBookmarkTags);
        TextView lblBookmarkSubject = rowView.findViewById(R.id.lblBookmarkSubject);

        if(entry!=null) {
            if(ivBookmarkPreview!=null) {
                String spl[] = entry.getLink().split("\\.");
                String extension = spl[spl.length-1];

                if(Arrays.asList("pdf", "doc", "docx", "odt", "ppt", "pptx", "odp", "xls", "xlsx", "ods").contains(extension.toLowerCase())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivBookmarkPreview.setImageDrawable(this.context.getDrawable(R.drawable.ic_insert_drive_file_black_24dp));
                    } else {
                        ivBookmarkPreview.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_insert_drive_file_black_24dp));
                    }
                } else if(Arrays.asList("png", "jpg").contains(extension.toLowerCase())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivBookmarkPreview.setImageDrawable(this.context.getDrawable(R.drawable.ic_image_black_24dp));
                    } else {
                        ivBookmarkPreview.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_image_black_24dp));
                    }
                } else {
                    if(entry.getPreview()!=null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(entry.getPreview(), 0, entry.getPreview().length);
                        ivBookmarkPreview.setImageBitmap(bitmap);
                    }
                }
            }

            if(lblBookmarkTitle!=null) {
                lblBookmarkTitle.setText(entry.getTitle());
            }
            if(lblBookmarkTags!=null) {
                lblBookmarkTags.setText(entry.getTags());
            }
            if(lblBookmarkSubject!=null) {
                if(entry.getSubject()!=null) {
                    lblBookmarkSubject.setText(entry.getSubject().getTitle());
                }
            }
        }

        return rowView;
    }
}
