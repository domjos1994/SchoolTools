package de.domjos.schooltools.widgets;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.BookmarkActivity;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.Bookmark;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.IntentHelper;
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
        views.setTextViewText(R.id.widgetItemTaskNameLabel, context.getString(R.string.main_nav_bookmarks));
        views.setRemoteAdapter(R.id.widgetListView, serviceIntent);
        views.setOnClickPendingIntent(R.layout.bookmark_widget_item, PendingIntent.getActivity(context, 0, new Intent(context, BookmarkActivity.class), 0));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Helper.receiveBroadCast(context, intent, R.id.widgetListView);
        super.onReceive(context, intent);
    }

    private static Intent getIntent(Bookmark bookmark, Context context) {
        String s = bookmark.getLink();
        if(s.endsWith("pdf") || s.endsWith("doc") || s.endsWith("docx") || s.endsWith("ods")
                || s.endsWith("ppt") || s.endsWith("pptx") || s.endsWith("odp") || s.endsWith("xls")
                || s.endsWith("xlsx") || s.endsWith("odt") || s.endsWith("jpg") || s.endsWith("png")) {

            File file = new File(s);

            boolean fileExists = file.exists();
            if(!file.exists())  {
                if(bookmark.getData()!=null) {
                    try {
                        if(Helper.checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            if(file.createNewFile()) {
                                FileOutputStream outputStream = new FileOutputStream(file);
                                outputStream.write(bookmark.getData());
                                outputStream.close();
                                fileExists = true;
                            }
                        }
                    } catch (Exception ex) {
                        Helper.printException(context, ex);
                    }
                }
            }

            if(fileExists) {
                try {
                    return IntentHelper.openFileViaIntent(file, context);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            return IntentHelper.openWebBrowser(context, s);
        }
        return null;
    }
}

