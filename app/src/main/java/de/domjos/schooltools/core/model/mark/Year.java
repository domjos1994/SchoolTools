/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.mark;

/**
 * Model of a Year
 * @see de.domjos.schooltools.adapter.YearAdapter
 * @author Dominic Joas
 * @version 1.0
 */
public class Year {
    private int ID;
    private String title, description;

    public Year() {
        this.ID = 0;
        this.title = "";
        this.description = "";
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
