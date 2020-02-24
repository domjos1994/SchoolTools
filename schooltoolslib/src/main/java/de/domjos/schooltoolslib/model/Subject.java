/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.schooltoolslib.model.timetable.Teacher;

/**
 * Model-Class for the Subject of a lesson
 * @author Dominic Joas
 * @version 1.0
 */
public class Subject extends BaseDescriptionObject {
    private int hoursInWeek;
    private boolean mainSubject;
    private String alias;
    private String backgroundColor;
    private Teacher teacher;

    public Subject() {
        super();
        this.hoursInWeek = 0;
        this.mainSubject = false;
        this.alias = "";
        this.backgroundColor = "";
        this.teacher = null;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    @Override
    public String toString() {
        return this.getTitle();
    }
}
