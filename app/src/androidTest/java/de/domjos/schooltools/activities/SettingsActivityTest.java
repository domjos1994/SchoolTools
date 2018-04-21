/*
 * Copyright (C) 2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.settings.UserSettings;
import de.domjos.schooltools.utils.Helper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for the MarkListActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class SettingsActivityTest  {
    private Context context;
    private UserSettings settings;

    @Rule
    public ActivityTestRule<SettingsActivity> rule = new ActivityTestRule<>(SettingsActivity.class);

    @Before
    public void init() {
        this.context = Helper.getContext();
        this.settings = new UserSettings(this.context);

        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.settings_general_start_default)));
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putStringSet("lsStart", set);
        set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.settings_general_shown_modules_entries)));
        editor.putStringSet("lsShownModules", set);
        editor.commit();
    }

    @Test
    public void testShownModulesSettings() throws Exception {
        Set<String> widgets;
        onView(withText(R.string.settings_general)).perform(click());

        onView(withText(R.string.settings_general_shown_modules_header)).perform(click());
        onView(withText(R.string.main_nav_mark_list)).perform(click());
        onView(withText("OK")).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_nav_mark_list)));

        onView(withText(R.string.settings_general_shown_modules_header)).perform(click());
        onView(withText(R.string.main_nav_mark)).perform(click());
        onView(withText("OK")).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_nav_mark)));

        onView(withText(R.string.settings_general_shown_modules_header)).perform(click());
        onView(withText(R.string.main_nav_timetable)).perform(click());
        onView(withText("OK")).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_nav_timetable)));

        onView(withText(R.string.settings_general_shown_modules_header)).perform(click());
        onView(withText(R.string.main_nav_timer)).perform(click());
        onView(withText("OK")).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_nav_timer)));
    }

    @Test
    public void testStartWidgetSettings() throws Exception {
        Set<String> widgets;
        onView(withText(R.string.settings_general)).perform(click());

        onView(withText(R.string.settings_general_start_header)).perform(click());
        onView(withText(R.string.main_today)).perform(click());
        onView(withText(R.string.sys_save)).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_today)));

        onView(withText(R.string.settings_general_start_header)).perform(click());
        onView(withText(R.string.main_top5Notes)).perform(click());
        onView(withText(R.string.sys_save)).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_top5Notes)));

        onView(withText(R.string.settings_general_start_header)).perform(click());
        onView(withText(R.string.main_savedMarkList)).perform(click());
        onView(withText(R.string.sys_save)).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(!widgets.contains(context.getString(R.string.main_savedMarkList)));

        onView(withText(R.string.settings_general_start_header)).perform(click());
        onView(withText(R.string.main_nav_buttons)).perform(click());
        onView(withText(R.string.sys_save)).perform(click());
        widgets = settings.getStartWidgets(context);
        assertTrue(widgets.contains(context.getString(R.string.main_nav_buttons)));
    }
}
