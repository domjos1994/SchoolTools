/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.timetable;

import de.domjos.schooltools.core.model.mark.Year;

/**
 * Model-Class for the Time-Table
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @see de.domjos.schooltools.core.model.timetable.Day
 * @see de.domjos.schooltools.core.model.timetable.SchoolClass
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTable {
    private int ID;
    private String title, description;
    private SchoolClass schoolClass;
    private Day[] days;
    private Year year;
    private boolean showNotifications;

    public TimeTable() {
        this.ID = 0;
        this.year = null;
        this.title = "";
        this.schoolClass = null;
        this.description = "";
        this.days = new Day[7];
        this.showNotifications = false;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Day[] getDays() {
        if(this.days!=null) {
            return this.days.clone();
        } else {
            return new Day[7];
        }
    }

    public void addDay(Day day) {
        if(day.getPositionInWeek()<=6 && day.getPositionInWeek()>=0) {
            days[day.getPositionInWeek()] = day;
        }
    }

    public boolean isShowNotifications() {
        return this.showNotifications;
    }

    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
