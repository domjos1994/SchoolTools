/*
 * Copyright (C)  2019 Domjos
 * This file is part of UniTrackerMobile <https://github.com/domjos1994/UniTrackerMobile>.
 *
 * UniTrackerMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniTrackerMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniTrackerMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.schooltools.spotlight;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.activities.ApiActivity;
import de.domjos.schooltools.helper.Helper;

@SuppressWarnings("SameParameterValue")
public class OnBoardingHelper {
    private final static String TUTORIAL = "tutorial";

    public static void tutorialNote(Activity activity, ScrollView svControls, BottomNavigationView view, ImageButton cmdSpeak) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(view, R.string.on_boarding_note, R.string.on_boarding_note_navigation, null, null);
                spotlightHelper.addTarget(svControls, R.string.on_boarding_note, R.string.on_boarding_note_controls, null, null);
                spotlightHelper.addTarget(cmdSpeak, R.string.on_boarding_note, R.string.on_boarding_note_speak, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialMarkList(Activity activity, Spinner spMarkListType, FloatingActionButton settings, Toolbar toolbar) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(spMarkListType, R.string.on_boarding_markList, R.string.on_boarding_markList_type, null, null);
                spotlightHelper.addTarget(settings, R.string.on_boarding_markList, R.string.on_boarding_markList_settings, null, null);
                spotlightHelper.addTargetToHamburger(toolbar, R.string.on_boarding_markList, R.string.on_boarding_markList_menu, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialMark(Activity activity, Spinner spYear, Spinner spSubject, SwipeRefreshDeleteList list) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(spYear, R.string.on_boarding_mark, R.string.on_boarding_mark_year, null, null);
                spotlightHelper.addTarget(spSubject, R.string.on_boarding_mark, R.string.on_boarding_mark_subject, null, null);
                spotlightHelper.addTarget(spSubject, R.string.on_boarding_mark, R.string.on_boarding_mark_both, null, null);
                spotlightHelper.addTarget(list, R.string.on_boarding_mark, R.string.on_boarding_mark_list, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialTimer(Activity activity, FloatingActionButton floatingActionButton, SwipeRefreshDeleteList list, ImageView ivPrevious, ImageView ivNext) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(floatingActionButton, R.string.on_boarding_timer, R.string.on_boarding_timer_new, null, null);
                spotlightHelper.addTarget(list, R.string.on_boarding_timer, R.string.on_boarding_timer_list, null, null);
                spotlightHelper.addTarget(ivPrevious, R.string.on_boarding_timer, R.string.on_boarding_timer_previous, null, null);
                spotlightHelper.addTarget(ivNext, R.string.on_boarding_timer, R.string.on_boarding_timer_next, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialToDo(Activity activity, Spinner toDoList, SwipeRefreshDeleteList entries) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(toDoList, R.string.on_boarding_todo, R.string.on_boarding_todo_select, null, null);
                spotlightHelper.addTarget(entries, R.string.on_boarding_todo, R.string.on_boarding_todo_list, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialBookmark(Activity activity, Spinner subject, BottomNavigationView navigationView, ImageButton cmdAttachment, SwipeRefreshDeleteList list) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(subject, R.string.on_boarding_bookmark, R.string.on_boarding_bookmark_select, null, null);
                spotlightHelper.addTarget(navigationView, R.string.on_boarding_bookmark, R.string.on_boarding_bookmark_new, null, null);
                spotlightHelper.addTarget(cmdAttachment, R.string.on_boarding_bookmark, R.string.on_boarding_bookmark_attachment, null, null);
                spotlightHelper.addTarget(list, R.string.on_boarding_bookmark, R.string.on_boarding_bookmark_open, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialLearningCard(Activity activity, BottomNavigationView navigationView, Button cmdStart) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(navigationView, R.string.on_boarding_learningCard, R.string.on_boarding_learningCard_navigation, null, null);
                spotlightHelper.addTarget(cmdStart, R.string.on_boarding_learningCard, R.string.on_boarding_learningCard_start, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    public static void tutorialTimeTable(Activity activity, BottomNavigationView navigationView, FloatingActionButton floatingActionButton, SwipeRefreshDeleteList list) {
        if(!readPref(activity, 1)) {
            OnBoardingHelper.execute(activity, () -> {
                SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
                spotlightHelper.addTarget(navigationView, R.string.on_boarding_timeTable, R.string.on_boarding_timeTable_navigation, null, null);
                spotlightHelper.addTarget(floatingActionButton, R.string.on_boarding_timeTable, R.string.on_boarding_timeTable_add, null, null);
                spotlightHelper.addTarget(list, R.string.on_boarding_timeTable, R.string.on_boarding_timeTable_list, null, ()->writePref(activity, 1));
                spotlightHelper.show();
            });
        }
    }

    private static void execute(Activity activity, Runnable runnable) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);

                    activity.runOnUiThread(runnable);
                } catch (Exception ex) {
                    activity.runOnUiThread(()-> MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity));
                }
                return null;
            }
        };
        task.execute();
    }

    private static void writePref(Activity activity, int part) {
        SharedPreferences preference = activity.getSharedPreferences(OnBoardingHelper.TUTORIAL, Context.MODE_PRIVATE);
        preference.edit().putInt(activity.getClass().getName(), part).apply();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean readPref(Activity activity, int part) {
        SharedPreferences preference = activity.getSharedPreferences(OnBoardingHelper.TUTORIAL, Context.MODE_PRIVATE);
        return preference.getInt(activity.getClass().getName(), 0)>=part;
    }
}
