/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class which contains converter for formats.
 * @author Dominic Joas
 * @version 0.0.1
 */
public class Converter {

    public static String convertDateToString(Date date) {
        if(date!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    public static Date convertStringToDate(String date) throws ParseException {
        if(date.isEmpty()) {
            return null;
        } else {
            if(Pattern.matches("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", date)) {
                if(date.contains("-")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    return sdf.parse(date);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                    return sdf.parse(date);
                }
            }
            return null;
        }
    }

    static Calendar convertStringDateToCalendar(String dt) throws ParseException {
        Date date = Converter.convertStringToDate(dt);
        if(date!=null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar;
        } else {
            return null;
        }
    }

    public static String convertURIToStringPath(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  projection, null, null, null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            }
            if (cursor != null) {
                cursor.moveToFirst();
            }
            if (cursor != null) {
                return cursor.getString(column_index);
            }
        } catch (Exception ex) {
              Helper.printException(context, ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static Time convertStringToTime(Context context, String time) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return new java.sql.Time(formatter.parse(time).getTime());
        } catch (Exception ex) {
            Helper.printException(context, ex);
        }
        return null;
    }

    public static Date convertStringTimeToDate(Context context, String time) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            return formatter.parse(String.format("%s.%s.%s %s", day, month, year, time));
        } catch (Exception ex) {
            Helper.printException(context, ex);
        }
        return null;
    }

    public static byte[] convertDrawableToByteArray(Context context, int id) {
        Drawable d;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            d = context.getDrawable(id);
        } else {
            d = context.getResources().getDrawable(id);
        }
        BitmapDrawable bitmapDrawable = ((BitmapDrawable)d);
        if(bitmapDrawable!=null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static Bitmap convertUriToBitmap(Context context, Uri uri) throws Exception {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    public static byte[] convertBitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }
}
