/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.factories;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.helper.SQLite;

/**
 * @author Dominic Joas
 */

public class NoteRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<String> notes;

    public NoteRemoteFactory(Context context) {
        this.notes = new LinkedList<>();
        this.context =  context;
        this.sqLite = new SQLite(this.context, "schoolTools.db", 1);
    }

    @Override
    public void onCreate() {
        this.reloadNotes();
    }

    @Override
    public void onDataSetChanged() {
        this.reloadNotes();
    }

    @Override
    public void onDestroy() {
        this.notes.clear();
    }

    @Override
    public int getCount() {
        return this.notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        String callItem = this.notes.get(position);
        row.setTextViewText(R.id.lblHeader, callItem);
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return this.notes.indexOf(this.notes.get(position));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void reloadNotes() {
        this.notes.clear();
        List<Note> note_list = this.sqLite.getNotes("");

        for(int i = 0; i<=this.getLength(note_list); i++) {
            this.notes.add(note_list.get(i).getTitle() + "\n");
        }
    }

    private int getLength(List<Note> notes) {
        int size = notes.size()-1;
        if(size>=4) {
            return 4;
        } else {
            return size;
        }
    }
}
