/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.services.NoteWidgetService;

/**
 * Widget to show Top5 Notes on Screen
 * @see de.domjos.schooltools.factories.NoteRemoteFactory
 * @see de.domjos.schooltools.services.NoteWidgetService
 * @author Dominic Joas
 * @version 0.1
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
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Helper.receiveBroadCast(context, intent, R.id.lvNotes);
        super.onReceive(context, intent);
    }
}

