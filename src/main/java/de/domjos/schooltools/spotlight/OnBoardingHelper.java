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
import android.widget.EditText;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.NoteActivity;

public class OnBoardingHelper {
    private final static String TUTORIAL = "tutorial";

    public static void tutorialNote(Activity activity, EditText txtTitle, EditText txtDate) {
        if(!readPref(activity, NoteActivity.class.getName(), 1)) {
            SpotlightHelper spotlightHelper = new SpotlightHelper(activity);
            spotlightHelper.addTarget(txtTitle, R.string.on_boarding_note, R.string.on_boarding_note_title, null, null);
            spotlightHelper.addTarget(txtDate, R.string.on_boarding_note, R.string.on_boarding_note_date, null, null);
            spotlightHelper.show();
            writePref(activity, NoteActivity.class.getName(), 1);
        }
    }


    private static void writePref(Context context, String type, int part) {
        SharedPreferences preference = context.getSharedPreferences(OnBoardingHelper.TUTORIAL, Context.MODE_PRIVATE);
        preference.edit().putInt(type, part).apply();
    }

    private static boolean readPref(Context context, String type, int part) {
        SharedPreferences preference = context.getSharedPreferences(OnBoardingHelper.TUTORIAL, Context.MODE_PRIVATE);
        return preference.getInt(type, 0)>=part;
    }
}
