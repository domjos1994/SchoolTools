/*
 * Copyright (C) 2017-2022  Dominic Joas
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.adapter.ScreenWidgetAdapter;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.customwidgets.model.ScreenWidget;
import de.domjos.schooltools.helper.Helper;

public final class Top5NotesScreenWidget extends ScreenWidget {
    private ScreenWidgetAdapter screenWidgetAdapter;

    public Top5NotesScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        ListView lvCurrentNotes = super.view.findViewById(R.id.lvNotes);
        this.screenWidgetAdapter = new ScreenWidgetAdapter(super.activity, R.drawable.ic_note_black_24dp, new ArrayList<>());
        lvCurrentNotes.setAdapter(this.screenWidgetAdapter);
        this.screenWidgetAdapter.notifyDataSetChanged();

        lvCurrentNotes.setOnTouchListener(Helper.addOnTouchListenerForScrolling());

        lvCurrentNotes.setOnItemClickListener((parent, view, position, id) -> {
            Note note = (Note) screenWidgetAdapter.getItem(position);
            if(note!=null) {
                Intent intent = new Intent(activity.getApplicationContext(), NoteActivity.class);
                intent.putExtra("ID", note.getId());
                activity.startActivity(intent);
            }
        });
    }

    public void addNotes() {
        this.screenWidgetAdapter.clear();
        if(super.view.getVisibility()==View.VISIBLE) {
            List<Note> notes = MainActivity.globals.getSqLite().getNotes("");
            for(int i = 0; i<=4; i++) {
                if(notes.size()-1>=i) {
                    this.screenWidgetAdapter.add(notes.get(i));
                }
            }

            if(this.screenWidgetAdapter.isEmpty()) {
                Note note = new Note();
                note.setTitle(super.activity.getString(R.string.main_noEntry));
                this.screenWidgetAdapter.add(note);
            }
        }
    }
}
