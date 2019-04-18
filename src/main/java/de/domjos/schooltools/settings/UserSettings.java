/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.view.Menu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.BookmarkActivity;
import de.domjos.schooltools.activities.LearningCardOverviewActivity;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.MarkActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.activities.TimeTableActivity;
import de.domjos.schooltools.activities.TimerActivity;
import de.domjos.schooltools.activities.ToDoActivity;
import de.domjos.schooltools.helper.Log4JHelper;
import de.domjos.schooltools.widgets.main.ButtonScreenWidget;

/**
 * Class which gets the settings from the SettingsActivity
 * @see de.domjos.schooltools.activities.SettingsActivity
 * In the MainActivity is a static public object of this class
 * @see de.domjos.schooltools.activities.MainActivity#globals
 * @author Dominic Joas
 * @version 1.0
 */
public class UserSettings {
    private SharedPreferences sharedPreferences;
    private Context context;

    public UserSettings(Context context) {
        this.context = context;
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

    public boolean isGeneralResetDatabase() {
        boolean reset = this.sharedPreferences.getBoolean("swtResetDatabase", false);
        if(reset) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean("swtResetDatabase", false);
            editor.apply();
        }
        return reset;
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

    public boolean isAutomaticallySubjects() {
        return this.sharedPreferences.getBoolean("swtSchoolTimeTableAutomaticallySubject", true);
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

    public boolean isDeleteToDoAfterDeadline() {
        return this.sharedPreferences.getBoolean("swtToDoDelete", false);
    }

    public boolean isNotificationsShown() {
        return this.sharedPreferences.getBoolean("swtNotifications", false);
    }

    public boolean isWhatsNew() {
        return this.sharedPreferences.getBoolean("swtWhatsNew", false);
    }

    public void setWhatsNew(boolean whatsNew) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean("swtWhatsNew", whatsNew);
        editor.apply();
    }

    public boolean isDeleteMemories() {
        return this.sharedPreferences.getBoolean("swtDeleteMemories", false);
    }

    public Set<String> getStartWidgets() {
        Set<String> set = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.settings_start_start_default)));
        return this.sharedPreferences.getStringSet("lsStart", set);
    }

    public Set<String> getShownModule() {
        Set<String> set = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.settings_start_shown_modules_entries)));
        return this.sharedPreferences.getStringSet("lsShownModules", set);
    }

    public String getStartModule() {
        return this.sharedPreferences.getString("lsStartModule", context.getString(R.string.main_nav_main));
    }


    public void hideMenuItems(NavigationView navigationView) {
        navigationView.getMenu().findItem(R.id.navMainMarkList).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainCalculateMark).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainTimeTable).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainTimer).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainToDo).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainNotes).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainLearningCards).setVisible(false);
        navigationView.getMenu().findItem(R.id.navMainBookMarks).setVisible(false);

        Set<String> modules = this.getShownModule();
        for(String content : modules) {
            this.hideMenu(content, R.string.main_nav_mark_list, R.id.navMainMarkList, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_mark, R.id.navMainCalculateMark, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timetable, R.id.navMainTimeTable, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timer, R.id.navMainTimer, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_notes, R.id.navMainToDo, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_todo, R.id.navMainNotes, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_learningCards, R.id.navMainLearningCards, navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_bookmarks, R.id.navMainBookMarks, navigationView.getMenu());
        }
    }

    public void openStartModule(ButtonScreenWidget buttonScreenWidget, Activity activity) {
        if(!MainActivity.globals.isStartScreen()) {
            MainActivity.globals.setStartScreen(true);
            String module = MainActivity.globals.getUserSettings().getStartModule();
            if(!module.equals(this.context.getString(R.string.main_nav_main))) {
                Intent intent = null;
                if(module.equals(this.context.getString(R.string.main_nav_mark_list))) {
                    buttonScreenWidget.openMarkListIntent();
                }
                if(module.equals(this.context.getString(R.string.main_nav_mark))) {
                    intent = new Intent(this.context, MarkActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_timetable))) {
                    intent = new Intent(this.context, TimeTableActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_timer))) {
                    intent = new Intent(this.context, TimerActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_todo))) {
                    intent = new Intent(this.context, ToDoActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_notes))) {
                    intent = new Intent(this.context, NoteActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_learningCards))) {
                    intent = new Intent(this.context, LearningCardOverviewActivity.class);
                }
                if(module.equals(this.context.getString(R.string.main_nav_bookmarks))) {
                    intent = new Intent(this.context, BookmarkActivity.class);
                }

                if(intent!=null) {
                    activity.startActivityForResult(intent, 99);
                }
            }
        }
    }

    private void hideMenu(String content, int id, int menu_id, Menu menu) {
        if(content.equals(this.context.getString(id))) {
            menu.findItem(menu_id).setVisible(true);
        }
    }
}
