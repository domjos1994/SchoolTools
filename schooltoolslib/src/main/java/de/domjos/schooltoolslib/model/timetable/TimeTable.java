/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltoolslib.model.timetable;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.schooltoolslib.model.mark.Year;

/**
 * Model-Class for the Time-Table
 * @see de.domjos.schooltoolslib.model.timetable.Day
 * @see de.domjos.schooltoolslib.model.timetable.SchoolClass
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTable extends BaseDescriptionObject {
    private SchoolClass schoolClass;
    private Day[] days;
    private Year year;
    private boolean currentTimeTable;

    public TimeTable() {
        super();
        this.year = null;
        this.schoolClass = null;
        this.days = new Day[7];
        this.currentTimeTable = false;
    }

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
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

    public boolean isCurrentTimeTable() {
        return this.currentTimeTable;
    }

    public void setCurrentTimeTable(boolean currentTimeTable) {
        this.currentTimeTable = currentTimeTable;
    }

    @Override
    public String toString() {
        return super.getTitle();
    }
}
