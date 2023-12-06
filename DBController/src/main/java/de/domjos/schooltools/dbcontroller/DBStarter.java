/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

public abstract class DBStarter {

    public static void init(Context context) {
        AppDatabase db =
                Room.databaseBuilder(context, AppDatabase.class, "schoolToolsDB").build();

        Log.v("Database", "Database started...\n" + db);
    }
}
