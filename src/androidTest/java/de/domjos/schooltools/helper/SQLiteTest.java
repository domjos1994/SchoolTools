/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import de.domjos.schooltools.activities.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Tests for the database
 * @author domjos
 * @version 0.1
 */
@RunWith(AndroidJUnit4.class)
public class SQLiteTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {

    }
}
