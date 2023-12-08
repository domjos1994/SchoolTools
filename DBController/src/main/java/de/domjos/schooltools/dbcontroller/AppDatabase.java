/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.domjos.schooltools.dbcontroller.converter.DateConverter;
import de.domjos.schooltools.dbcontroller.model.general.Subject;
import de.domjos.schooltools.dbcontroller.model.general.Teacher;
import de.domjos.schooltools.dbcontroller.repository.general.SubjectDao;
import de.domjos.schooltools.dbcontroller.repository.general.TeacherDao;

@Database(entities = {Teacher.class, Subject.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TeacherDao teacherDao();
    public abstract SubjectDao subjectDao();
}
