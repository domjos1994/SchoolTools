/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller.repository.general;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import de.domjos.schooltools.dbcontroller.BaseTest;
import de.domjos.schooltools.dbcontroller.model.general.Teacher;

public class TeacherDaoTest extends BaseTest {

    @Test
    public void testInsertDelete() {

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        this.teacherDao.insertAll(teacher);

        Assert.assertEquals(this.teacherDao.countTeacher(), 1);

        List<Teacher> teachers = this.teacherDao.getTeachers();
        this.teacherDao.deleteAll(teachers.get(0));

        Assert.assertEquals(this.teacherDao.countTeacher(), 0);
    }

    @Test
    public void testDelete() {

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        this.teacherDao.insertAll(teacher);

        Assert.assertEquals(this.teacherDao.countTeacher(), 1);

        List<Teacher> teachers = this.teacherDao.getTeachers();
        teacher = teachers.get(0);
        teacher.setFirstName("Joanne");
        this.teacherDao.updateAll(teacher);

        teacher = this.teacherDao.getTeacher(teacher.getID());
        Assert.assertEquals(teacher.getFirstName(), "Joanne");

        this.teacherDao.deleteAll(teachers.get(0));
        Assert.assertEquals(this.teacherDao.countTeacher(), 0);
    }

    @Test
    public void testDeleteGetFirstAndLastName() {

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        this.teacherDao.insertAll(teacher);

        Assert.assertEquals(this.teacherDao.countTeacher(), 1);

        List<Teacher> teachers = this.teacherDao.getTeachers();
        teacher = teachers.get(0);
        teacher.setFirstName("Joanne");
        this.teacherDao.updateAll(teacher);

        teacher = this.teacherDao.getTeacherByName(teacher.getFirstName(), teacher.getLastName());
        Assert.assertEquals(teacher.getFirstName(), "Joanne");

        this.teacherDao.deleteAll(teachers.get(0));
        Assert.assertEquals(this.teacherDao.countTeacher(), 0);
    }
}
