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
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Adapter for the List-View of the Note-Adapter
 * @see de.domjos.schooltools.activities.NoteActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    private Context context;

    public NoteAdapter(Context context, int resource, ArrayList<Note> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = Helper.getRowView(this.context, parent, R.layout.note_item);
        Note note = this.getItem(position);

        TextView lblNoteTitle = rowView.findViewById(R.id.lblNoteTitle);
        TextView lblNoteMemoryDate = rowView.findViewById(R.id.lblNoteMemoryDate);

        if(note!=null) {
            if(lblNoteTitle!=null) {
                lblNoteTitle.setText(note.getTitle());
            }
            if(lblNoteMemoryDate!=null) {
                if(note.getMemoryDate()!=null) {
                    lblNoteMemoryDate.setText(Converter.convertDateToString(note.getMemoryDate()));
                }
            }
        }

        return rowView;
    }
}
