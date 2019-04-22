/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.adapter.NoteAdapter;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.custom.ScreenWidget;
import de.domjos.schooltools.helper.Helper;

public final class Top5NotesScreenWidget extends ScreenWidget {
    private NoteAdapter noteAdapter;

    public Top5NotesScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        ListView lvCurrentNotes = super.view.findViewById(R.id.lvNotes);
        this.noteAdapter = new NoteAdapter(super.activity, R.layout.note_item, new ArrayList<Note>());
        lvCurrentNotes.setAdapter(this.noteAdapter);
        this.noteAdapter.notifyDataSetChanged();

        lvCurrentNotes.setOnTouchListener(Helper.addOnTouchListenerForScrolling());

        lvCurrentNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteAdapter.getItem(position);
                if(note!=null) {
                    Intent intent = new Intent(activity.getApplicationContext(), NoteActivity.class);
                    intent.putExtra("ID", note.getID());
                    activity.startActivity(intent);
                }
            }
        });
    }

    public void addNotes() {
        this.noteAdapter.clear();
        if(super.view.getVisibility()==View.VISIBLE) {
            List<Note> notes = MainActivity.globals.getSqLite().getNotes("");
            for(int i = 0; i<=4; i++) {
                if(notes.size()-1>=i) {
                    this.noteAdapter.add(notes.get(i));
                }
            }

            if(this.noteAdapter.isEmpty()) {
                Note note = new Note();
                note.setTitle(super.activity.getString(R.string.main_noEntry));
                this.noteAdapter.add(note);
            }
        }
    }
}
