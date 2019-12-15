/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.factories;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.BookmarkActivity;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.Bookmark;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.IntentHelper;
import de.domjos.schooltools.helper.SQLite;

/**
 * Factory to load Notes in widget
 * @see de.domjos.schooltools.widgets.TimeTableWidget
 * @see de.domjos.schooltools.services.TimeTableWidgetService
 * @author Dominic Joas
 * @version 0.1
 */
public class BookmarkRemoteFactory implements RemoteViewsService.RemoteViewsFactory  {
    private SQLite sqLite;
    private Context context;
    private final List<String> bookmarks;
    private int appWidgetId;

    public BookmarkRemoteFactory(Context context, Intent intent) {
        super();

        this.context =  context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        this.sqLite = new SQLite(this.context);
        this.bookmarks = new LinkedList<>();
    }

    @Override
    public void onCreate() {
        this.reloadBookmarks();
    }

    @Override
    public void onDataSetChanged() {
        this.reloadBookmarks();
    }

    @Override
    public void onDestroy() {
        this.bookmarks.clear();
    }

    @Override
    public int getCount() {
        return this.bookmarks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.bookmark_widget_item);
        String callItem = this.bookmarks.get(position);
        row.setTextViewText(R.id.widgetItemTaskNameLabel, callItem);

        Bundle extras = new Bundle();
        extras.putString("data", callItem);
        Intent fillInIntent = new Intent(context, BookmarkActivity.class);
        fillInIntent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fillInIntent, 0);
        row.setOnClickPendingIntent(R.id.widgetItemContainer, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            row = this.changeSize(row);
        }

        return row;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private RemoteViews changeSize(RemoteViews row) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = manager.getAppWidgetOptions(appWidgetId);
            final int minWidth = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            if(minWidth==120) {
                row.setTextViewTextSize(R.id.widgetItemTaskNameLabel, 0, 14);
            } else {
                row.setTextViewTextSize(R.id.widgetItemTaskNameLabel, 0, 18);
            }
        } else {
            row.setTextViewTextSize(R.id.widgetItemTaskNameLabel, 0, 18);
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
        return this.bookmarks.indexOf(this.bookmarks.get(position));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void reloadBookmarks() {
        this.bookmarks.clear();
        List<Bookmark> bookmark_list = this.sqLite.getBookmarks("");

        for(int i = 0; i<=bookmark_list.size()-1; i++) {
            this.bookmarks.add(bookmark_list.get(i).getTitle());
        }
    }

    private Intent getIntent(Bookmark bookmark, Context context) {
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
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
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
            IntentHelper.openWebBrowser(context, s);
        }
        return null;
    }
}
