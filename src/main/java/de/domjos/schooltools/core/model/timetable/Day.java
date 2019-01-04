/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.timetable;

import java.util.LinkedHashMap;
import java.util.Map;

import de.domjos.schooltools.core.model.Subject;

/**
 * Model-Class for the day of a Time-Table
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class Day {
    private int positionInWeek;
    private Map<Hour, PupilHour> pupilHour;
    private Map<Hour, TeacherHour> teacherHour;

    public Day() {
        this.positionInWeek = 0;
        this.pupilHour = new LinkedHashMap<>();
        this.teacherHour = new LinkedHashMap<>();
    }

    public int getPositionInWeek() {
        return this.positionInWeek;
    }

    public void setPositionInWeek(int positionInWeek) {
        this.positionInWeek = positionInWeek;
    }

    public Map<Hour, PupilHour> getPupilHour() {
        return this.pupilHour;
    }

    public void addPupilHour(Hour hour, Subject subject, Teacher teacher, String roomNumber) {
        this.pupilHour.put(hour, new PupilHour(subject, teacher, roomNumber));
    }

    public Map<Hour, TeacherHour> getTeacherHour() {
        return this.teacherHour;
    }

    public void addTeacherHour(Hour hour, Subject subject, SchoolClass schoolClass, String roomNumber) {
        this.teacherHour.put(hour, new TeacherHour(subject, schoolClass, roomNumber));
    }
}
