/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.services.NoteWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent serviceIntent = new Intent(context, NoteWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        remoteViews.setTextViewText(R.id.lblHeader, context.getString(R.string.main_nav_notes));
        remoteViews.setRemoteAdapter(R.id.lvNotes, serviceIntent);
        remoteViews.setOnClickPendingIntent(R.id.lblHeader, PendingIntent.getActivity(context, 0, new Intent(context, NoteActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS) && intent.hasExtra("name")) {
                String name = bundle.getString("name");
                if(name!=null) {
                    if(name.equals(NoteWidget.this.getClass().getCanonicalName())) {
                        int[] ids = bundle.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.lvNotes);
                    }
                }
            }
        }
    }
}

