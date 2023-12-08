/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.domjos.schooltools.dbcontroller.model.general.Teacher;

@Dao
public interface TeacherDao {

    @Query("SELECT * FROM teachers")
    List<Teacher> getTeachers();

    @Query("SELECT * FROM teachers WHERE id=:id")
    Teacher getTeacher(long id);

    @Query("SELECT * FROM teachers WHERE firstName=:firstName and lastName=:lastName")
    Teacher getTeacherByName(String firstName, String lastName);

    @Query("SELECT count(id) FROM teachers")
    long countTeacher();

    @Insert
    void insertAll(Teacher... teachers);

    @Update
    void updateAll(Teacher... teachers);

    @Delete
    void deleteAll(Teacher... teachers);
}
