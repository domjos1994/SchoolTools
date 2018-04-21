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
 * Model-Class for the school-class of a Time-Table
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @see de.domjos.schooltools.core.model.timetable.Day
 * @author Dominic Joas
 * @version 1.0
 */
public class SchoolClass {
    private int ID, numberOfPupils;
    private String title, description;

    public SchoolClass() {
        this.ID = 0;
        this.numberOfPupils = 0;
        this.title = "";
        this.description = "";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getNumberOfPupils() {
        return numberOfPupils;
    }

    public void setNumberOfPupils(int numberOfPupils) {
        this.numberOfPupils = numberOfPupils;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
