/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller.model.general;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TeacherSubjects {
    @Embedded
    private Teacher teacher;

    @Relation(
        parentColumn = "id",
        entityColumn = "teacherID"
    )
    private List<Subject> subjects;

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
