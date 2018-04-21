/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.timetable;

/**
 * Model-Class for the teacher of a Time-Table
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @see de.domjos.schooltools.core.model.timetable.Day
 * @author Dominic Joas
 * @version 1.0
 */
public class Teacher {
    private int ID;
    private String firstName, lastName, description;

    public Teacher() {
        this.ID = 0;
        this.firstName = "";
        this.lastName = "";
        this.description = "";
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
