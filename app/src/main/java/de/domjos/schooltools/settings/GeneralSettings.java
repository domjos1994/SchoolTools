/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

public class GeneralSettings {
    private final static String ACCEPT_MARK_LIST_MESSAGE = "acceptMarkListMessage";
    private final static String WIDGET_TIMETABLE_SPINNER = "widgetTimeTableSpinner";
    private final static String WIDGET_MARKLIST_SPINNER = "widgetTimeTableSpinner";
    private final static String CURRENT_DB_VERSION = "currentDBVersion";

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public GeneralSettings(Context context) {
        this.preferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public int getCurrentVersionCode(Context context) {
        int version;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (Exception ex) {
            version = 1;
        }
        return version;
    }

    public int getDatabaseVersion() {
        return this.preferences.getInt(GeneralSettings.CURRENT_DB_VERSION, 0);
    }

    public void setDatabaseVersion(int version) {
        this.editor.putInt(GeneralSettings.CURRENT_DB_VERSION, version);
        this.editor.apply();
    }

    public void setAcceptMarkListMessage(boolean accepted) {
        this.editor.putBoolean(GeneralSettings.ACCEPT_MARK_LIST_MESSAGE, accepted);
        this.editor.apply();
    }

    public boolean isAcceptMarkListMessage() {
        return this.preferences.getBoolean(GeneralSettings.ACCEPT_MARK_LIST_MESSAGE, false);
    }

    public void setWidgetTimetableSpinner(String spinner) {
        this.editor.putString(GeneralSettings.WIDGET_TIMETABLE_SPINNER, spinner);
        this.editor.apply();
    }

    public String getWidgetTimetableSpinner() {
        return this.preferences.getString(GeneralSettings.WIDGET_TIMETABLE_SPINNER, "");
    }

    public void setWidgetMarkListSpinner(String spinner) {
        this.editor.putString(GeneralSettings.WIDGET_MARKLIST_SPINNER, spinner);
        this.editor.apply();
    }

    public String getWidgetMarkListSpinner() {
        return this.preferences.getString(GeneralSettings.WIDGET_MARKLIST_SPINNER, "");
    }
}
