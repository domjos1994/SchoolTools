/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller.model.general;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "subjects")
public class Subject extends BaseDescriptionObject {
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "alias")
    private String alias;

    @ColumnInfo(name = "hoursInWeek")
    private int hoursInWeek;

    @ColumnInfo(name = "isMainSubject")
    private boolean isMainSubject;

    @ColumnInfo(name = "backgroundColor")
    private int backgroundColor;

    private long teacherID;

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

    public int getHoursInWeek() {
        return this.hoursInWeek;
    }

    public void setHoursInWeek(int hoursInWeek) {
        this.hoursInWeek = hoursInWeek;
    }

    public boolean isMainSubject() {
        return this.isMainSubject;
    }

    public void setMainSubject(boolean mainSubject) {
        if(mainSubject) {
            this.hoursInWeek = 4;
        } else {
            this.hoursInWeek = 2;
        }
        this.isMainSubject = mainSubject;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public long getTeacherID() {
        return this.teacherID;
    }

    public void setTeacherID(long teacherID) {
        this.teacherID = teacherID;
    }
}
