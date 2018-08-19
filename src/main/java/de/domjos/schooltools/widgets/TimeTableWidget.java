/*
 * Copyright (C) 2017-2018  Dominic Joas
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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

import de.domjos.schooltools.R;
import de.domjos.schooltools.services.TimeTableWidgetService;

/**
 * Widget to show TimeTable on Screen
 * @see de.domjos.schooltools.widgets.TimeTableWidgetConfigurationActivity
 * @see de.domjos.schooltools.factories.TimeTableRemoteFactory
 * @see de.domjos.schooltools.services.TimeTableWidgetService
 * @author Dominic Joas
 * @version 0.1
 */
public class TimeTableWidget extends AppWidgetProvider {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent serviceIntent = new Intent(context, TimeTableWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.timetable_widget);
        remoteViews.setTextViewText(R.id.lblHeader, context.getString(R.string.main_nav_timetable));
        remoteViews.setRemoteAdapter(R.id.grdTimeTable, serviceIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

