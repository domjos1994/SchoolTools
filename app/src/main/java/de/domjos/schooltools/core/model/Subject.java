/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model;

import de.domjos.schooltools.core.model.timetable.Teacher;

/**
 * Model-Class for the Subject of a lesson
 * @see de.domjos.schooltools.activities.TimeTableSubjectActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class Subject {
    private int ID, hoursInWeek;
    private boolean mainSubject;
    private String title;
    private String alias;
    private String description;
    private String backgroundColor;
    private Teacher teacher;

    public Subject() {
        this.ID = 0;
        this.hoursInWeek = 0;
        this.mainSubject = false;
        this.title = "";
        this.alias = "";
        this.description = "";
        this.backgroundColor = "";
        this.teacher = null;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHoursInWeek() {
        return this.hoursInWeek;
    }

    public void setHoursInWeek(int hoursInWeek) {
        this.hoursInWeek = hoursInWeek;
    }

    public boolean isMainSubject() {
        return this.mainSubject;
    }

    public void setMainSubject(boolean mainSubject) {
        this.mainSubject = mainSubject;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
