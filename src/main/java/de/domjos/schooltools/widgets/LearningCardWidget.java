package de.domjos.schooltools.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.LearningCardOverviewActivity;

/**
 * Implementation of App Widget functionality.
 */
public class LearningCardWidget extends AppWidgetProvider {
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.learning_card_widget);
        watchWidget = new ComponentName(context, LearningCardWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.cmdStartQuery, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.learning_card_widget);
            watchWidget = new ComponentName(context, LearningCardWidget.class);

            Intent queryIntent = new Intent(context, LearningCardOverviewActivity.class);
            queryIntent.putExtra(LearningCardOverviewActivity.MODE, LearningCardOverviewActivity.RANDOM);
            queryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(queryIntent);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

