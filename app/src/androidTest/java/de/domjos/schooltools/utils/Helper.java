/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.util.Log;
import android.view.View;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static org.junit.Assert.assertEquals;

/**
 * Helper for the instrumented Tests
 * @author Dominic Joas
 */
public class Helper {

    public static Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    /**
     * Gets the activity which is visible on the screen
     * @return the current activity
     */
    public static Activity getCurrentActivity(){
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                for (Activity act: resumedActivities){
                    currentActivity[0] = act;
                    break;
                }
            }
        });

        return currentActivity[0];
    }

    /**
     * Asserts the Class of the current Activity with another class
     * @param actName the other class
     */
    public static void assertTwoClasses(String actName) {
        Activity activity = Helper.getCurrentActivity();
        ComponentName cn = activity.getComponentName();
        String actClassName = cn.getClassName();
        assertEquals(actClassName, actName);
    }
}
