/*
 * Copyright (C) 2017-2019  Dominic Joas
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
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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
    private static Activity getCurrentActivity(){
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                currentActivity[0] = resumedActivities.iterator().next();
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
