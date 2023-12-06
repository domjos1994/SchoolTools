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

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.IOException;

import de.domjos.schooltools.dbcontroller.model.general.Teacher;
import de.domjos.schooltools.dbcontroller.repository.general.TeacherDao;

@RunWith(AndroidJUnit4.class)
public abstract class BaseTest {
    protected AppDatabase db;
    protected TeacherDao teacherDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        this.db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.teacherDao = this.db.teacherDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

}
