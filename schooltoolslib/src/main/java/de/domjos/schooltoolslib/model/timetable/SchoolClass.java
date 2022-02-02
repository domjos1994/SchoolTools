/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltoolslib.model.timetable;

import de.domjos.customwidgets.model.BaseDescriptionObject;

/**
 * Model-Class for the school-class of a Time-Table
 * @see de.domjos.schooltoolslib.model.timetable.Day
 * @author Dominic Joas
 * @version 1.0
 */
public class SchoolClass extends BaseDescriptionObject {
    private int numberOfPupils;

    public SchoolClass() {
        super();
        this.numberOfPupils = 0;
    }

    public int getNumberOfPupils() {
        return numberOfPupils;
    }

    public void setNumberOfPupils(int numberOfPupils) {
        this.numberOfPupils = numberOfPupils;
    }
}
