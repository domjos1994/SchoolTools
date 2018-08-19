/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model;

import java.util.Date;

import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.timetable.Teacher;

/**
 * Model-Class for the Timer-Events
 * @see de.domjos.schooltools.activities.TimerActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class TimerEvent {
    private int ID;
    private String title;
    private String description;
    private String category;
    private Date memoryDate;
    private Date eventDate;
    private Subject subject;
    private Teacher teacher;
    private SchoolClass schoolClass;

    public TimerEvent() {
        this.ID = 0;
        this.title = "";
        this.description = "";
        this.category = "";
        this.memoryDate = null;
        this.subject = null;
        this.teacher = null;
        this.schoolClass = null;
        this.eventDate = null;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getMemoryDate() {
        if(this.memoryDate!=null) {
            return (Date) this.memoryDate.clone();
        } else {
            return null;
        }
    }

    public void setMemoryDate(Date memoryDate) {
        if(memoryDate!=null) {
            this.memoryDate = (Date) memoryDate.clone();
        } else {
            this.memoryDate = null;
        }
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public Date getEventDate() {
        if(this.eventDate!=null) {
            return (Date) this.eventDate.clone();
        } else {
            return null;
        }
    }

    public void setEventDate(Date eventDate) {
        if(eventDate!=null) {
            this.eventDate = (Date) eventDate.clone();
        } else {
            this.eventDate = null;
        }
    }
}
