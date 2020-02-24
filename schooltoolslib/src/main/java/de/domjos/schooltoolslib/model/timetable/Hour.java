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
 * Model-Class for the hour of a Time-Table
 * @author Dominic Joas
 * @version 1.0
 */
public class Hour extends BaseDescriptionObject {
    private String start;
    private String end;
    private boolean Break;

    public Hour() {
        this.start = "";
        this.end = "";
        this.Break = false;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
        this.setTimeToTitle();
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
        this.setTimeToTitle();
    }

    public boolean isBreak() {
        return this.Break;
    }

    public void setBreak(boolean aBreak) {
        Break = aBreak;
    }

    public void setTimeToTitle() {
        super.setTitle(String.format("%s - %s", this.start, this.end));
    }

    @Override
    public String toString() {
        return String.format("%s - %s", this.start, this.end);
    }
}
