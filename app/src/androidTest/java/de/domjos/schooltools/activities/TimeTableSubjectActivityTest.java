/*
 * Copyright (C) 2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.domjos.schooltools.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Tests for the TimeTableSubjectActivity
 * @author Dominic Joas
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
public class TimeTableSubjectActivityTest {

    @Rule
    public ActivityTestRule<TimeTableSubjectActivity> rule = new ActivityTestRule<>(TimeTableSubjectActivity.class);


    @Test
    public void testColorComboBox() throws Throwable {
        ViewInteraction buttonInterAction = onView(withId(R.id.navTimeTableSubAdd));
        buttonInterAction.perform(click());

        TextView lblSelectedColor = rule.getActivity().findViewById(R.id.lblSelectedColor);

        onData(anything()).inAdapterView(withId(R.id.cmbSubjectBackgroundColor)).atPosition(3).perform(click());


        assertThat(lblSelectedColor.getBackground(), instanceOf(ColorDrawable.class));
        ColorDrawable colorDrawable = (ColorDrawable) lblSelectedColor.getBackground();
        assertThat(colorDrawable.getColor(), equalTo(rule.getActivity().getApplicationContext().getColor(R.color.Yellow)));
    }
}
