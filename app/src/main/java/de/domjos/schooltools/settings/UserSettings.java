/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Log4JHelper;

/**
 * Class which gets the settings from the SettingsActivity
 * @see de.domjos.schooltools.activities.SettingsActivity
 * In the MainActivity is a static public object of this class
 * @see de.domjos.schooltools.activities.MainActivity#settings
 * @author Dominic Joas
 * @version 1.0
 */
public class UserSettings {
    private SharedPreferences sharedPreferences;

    public UserSettings(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isExpertMode() {
        return this.sharedPreferences.getBoolean("swtSchoolMarkListExpert", false);
    }

    public boolean isTimeTableMode() {
        return this.sharedPreferences.getBoolean("swtSchoolTimeTableMode", false);
    }

    public boolean isApiCancelExport() {
        return this.sharedPreferences.getBoolean("swtApiCancelExport", false);
    }

    public boolean isApiOverrideEntries() {
        return this.sharedPreferences.getBoolean("swtApiOverrideEntries", false);
    }

    public int getMarkListMax() {
        String content = this.sharedPreferences.getString("txtSchoolMarkListExtendedMax", "200");
        try {
            return Integer.parseInt(content);
        } catch (Exception ex) {
            Log4JHelper.getLogger("warning").error(ex.getMessage(), ex);
            return 100;
        }
    }

    public int getBreakTime() {
        String content = this.sharedPreferences.getString("txtSchoolTimeTableBreakTime", "20");
        try {
            return Integer.parseInt(content);
        } catch (Exception ex) {
            Log4JHelper.getLogger("warning").error(ex.getMessage(), ex);
            return 100;
        }
    }

    public int getTimerNotificationDistance() {
        String content = this.sharedPreferences.getString("txtSchoolTimerNotification", "7");
        try {
            return Integer.parseInt(content);
        } catch (Exception ex) {
            Log4JHelper.getLogger("warning").error(ex.getMessage(), ex);
            return 7;
        }
    }

    public boolean isNotificationsShown() {
        return this.sharedPreferences.getBoolean("swtNotifications", false);
    }

    public boolean isDeleteMemories() {
        return this.sharedPreferences.getBoolean("swtDeleteMemories", false);
    }

    public Set<String> getStartWidgets(Context context) {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.settings_general_start_default)));
        return this.sharedPreferences.getStringSet("lsStart", set);
    }

    public Set<String> getShownModule(Context context) {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.settings_general_shown_modules_entries)));
        return this.sharedPreferences.getStringSet("lsShownModules", set);
    }

    public String getStartModule(Context context) {
        return this.sharedPreferences.getString("lsStartModule", context.getString(R.string.main_nav_main));
    }
}
