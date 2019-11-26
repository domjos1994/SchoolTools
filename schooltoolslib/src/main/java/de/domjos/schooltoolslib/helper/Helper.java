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
