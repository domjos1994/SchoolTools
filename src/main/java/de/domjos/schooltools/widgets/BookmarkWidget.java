package de.domjos.schooltools.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.services.BookmarkWidgetService;
import de.domjos.schooltools.services.NoteWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class BookmarkWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent serviceIntent = new Intent(context, BookmarkWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bookmark_widget);
        views.setTextViewText(R.id.lblHeader, context.getString(R.string.main_nav_bookmarks));
        views.setRemoteAdapter(R.id.lvBookmarks, serviceIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Helper.receiveBroadCast(context, intent, R.id.lvBookmarks);
        super.onReceive(context, intent);
    }
}

