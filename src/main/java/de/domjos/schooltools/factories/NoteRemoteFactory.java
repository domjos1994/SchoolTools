/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.factories;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.helper.SQLite;

/**
 * Factory to load Notes in widget
 * @see de.domjos.schooltools.widgets.TimeTableWidget
 * @see de.domjos.schooltools.services.TimeTableWidgetService
 * @author Dominic Joas
 * @version 0.1
 */
public class NoteRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<String> notes;
    private int appWidgetId;

    public NoteRemoteFactory(Context context, Intent intent) {
        super();

        this.context =  context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        this.sqLite = new SQLite(this.context);
        this.notes = new LinkedList<>();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            row = this.changeSize(row);
        }

        return row;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private RemoteViews changeSize(RemoteViews row) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = manager.getAppWidgetOptions(appWidgetId);
            final int minWidth = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            if(minWidth==120) {
                row.setTextViewTextSize(R.id.lblHeader, 0, 14);
            } else {
                row.setTextViewTextSize(R.id.lblHeader, 0, 18);
            }
        } else {
            row.setTextViewTextSize(R.id.lblHeader, 0, 18);
        }
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
            this.notes.add(note_list.get(i).getTitle() + ":\n" + note_list.get(i).getDescription());
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
