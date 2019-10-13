/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.timetable;

/**
 * Model-Class for the hour of a Time-Table
 * @see de.domjos.schooltools.activities.TimeTableActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class Hour {
    private int ID;
    private String start;
    private String end;
    private boolean Break;

    public Hour() {
        this.ID = 0;
        this.start = "";
        this.end = "";
        this.Break = false;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isBreak() {
        return this.Break;
    }

    public void setBreak(boolean aBreak) {
        Break = aBreak;
    }

    public String toString() {
        return String.format("%s - %s", this.start, this.end);
    }
}
