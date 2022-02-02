/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.helper;

import android.content.Context;

import java.util.regex.Pattern;

public class Helper {

    public static String getLanguage(Context context, int id) {
        return context.getString(id);
    }

    public static boolean isInteger(String number) {
        return Pattern.matches("^\\d+$", number.trim());
    }

    public static boolean isDouble(String number) {
        return Pattern.matches("^[0-9]+(.|,)?[0-9]?$", number.trim());
    }
}
