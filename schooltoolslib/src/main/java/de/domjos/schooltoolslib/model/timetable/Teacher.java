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

/**
 * Model-Class for the teacher of a Time-Table
 * @see de.domjos.schooltoolslib.model.timetable.Day
 * @author Dominic Joas
 * @version 1.0
 */
public class Teacher extends BaseDescriptionObject {
    private String firstName, lastName;

    public Teacher() {
        this.firstName = "";
        this.lastName = "";
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.setNameToTitle();
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.setNameToTitle();
    }

    private void setNameToTitle() {
        super.setTitle(String.format("%s %s", this.firstName, this.lastName));
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }
}
