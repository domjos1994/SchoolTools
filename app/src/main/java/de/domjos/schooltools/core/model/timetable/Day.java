/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.timetable;

import java.util.AbstractMap;
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
    private Map<Hour, Map.Entry<Subject, Teacher>> pupilHour;
    private Map<Hour, Map.Entry<Subject, SchoolClass>> teacherHour;

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

    public Map<Hour, Map.Entry<Subject, Teacher>> getPupilHour() {
        return this.pupilHour;
    }

    public void addPupilHour(Hour hour, Subject subject, Teacher teacher) {
        this.pupilHour.put(hour, new AbstractMap.SimpleEntry<>(subject, teacher));
    }

    public Map<Hour, Map.Entry<Subject, SchoolClass>> getTeacherHour() {
        return this.teacherHour;
    }

    public void addTeacherHour(Hour hour, Subject subject, SchoolClass schoolClass) {
        this.teacherHour.put(hour, new AbstractMap.SimpleEntry<>(subject, schoolClass));
    }
}
