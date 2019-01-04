/*
 * Copyright (C) 2017-2019  Dominic Joas
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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.utils.Helper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Tests for the MainActivity
 * @author Dominic Joas
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {
        Context context = Helper.getContext();

        Set<String> set = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.settings_start_shown_modules_entries)));
        set.add(context.getString(R.string.main_nav_buttons));
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putStringSet("lsShownModules", set);
        editor.commit();
    }

    @Test
    public void openMarkListActivity() throws Exception {
        onView(withId(R.id.trMarkList)).perform(click());
        Helper.assertTwoClasses(MarkListActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainMarkList));
        Thread.sleep(500);
        Helper.assertTwoClasses(MarkListActivity.class.getName());
    }

    @Test
    public void openCalculateMarkActivity() throws Exception {
        onView(withId(R.id.trMark)).perform(click());
        Helper.assertTwoClasses(MarkActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainCalculateMark));
        Thread.sleep(500);
        Helper.assertTwoClasses(MarkActivity.class.getName());
    }

    @Test
    public void openTimeTableActivity() throws Exception {
        onView(withId(R.id.trTimeTable)).perform(click());
        Helper.assertTwoClasses(TimeTableActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainTimeTable));
        Thread.sleep(500);
        Helper.assertTwoClasses(TimeTableActivity.class.getName());
    }

    @Test
    public void openNoteActivity() throws Exception {
        onView(withId(R.id.trNote)).perform(click());
        Helper.assertTwoClasses(NoteActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainNotes));
        Thread.sleep(500);
        Helper.assertTwoClasses(NoteActivity.class.getName());
    }

    @Test
    public void openTimerActivity() throws Exception {
        onView(withId(R.id.trTimer)).perform(click());
        Helper.assertTwoClasses(TimerActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainTimer));
        Thread.sleep(500);
        Helper.assertTwoClasses(TimerActivity.class.getName());
    }

    @Test
    public void openToDoActivity() throws Exception {
        onView(withId(R.id.trToDo)).perform(click());
        Helper.assertTwoClasses(ToDoActivity.class.getName());

        pressBack();

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.navMainToDo));
        Thread.sleep(500);
        Helper.assertTwoClasses(ToDoActivity.class.getName());
    }

    @Test
    public void openHelpActivity() {
        onView(withId(R.id.trHelp)).perform(click());
        Helper.assertTwoClasses(HelpActivity.class.getName());
    }

    @Test
    public void openSettingsActivityFromButtons() {
        onView(withId(R.id.trSettings)).perform(click());
        Helper.assertTwoClasses(SettingsActivity.class.getName());
    }

    @Test
    public void openSettingsActivityFromMenu() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(500);
        onView(withText(R.string.main_menu_settings)).perform(click());

        Helper.assertTwoClasses(SettingsActivity.class.getName());
    }

    @Test
    public void openExportActivityFromButtons() {
        onView(withId(R.id.trExport)).perform(click());
        Helper.assertTwoClasses(ApiActivity.class.getName());
    }

    @Test
    public void openExportActivityFromMenu() throws Exception {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getContext());
        Thread.sleep(500);
        onView(withText(R.string.main_menu_export)).perform(click());

        Helper.assertTwoClasses(ApiActivity.class.getName());
    }
}
