/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model;

import java.util.Date;

/**
 * Model-Class for Notices
 * @see de.domjos.schooltools.activities.NoteActivity
 * @author Dominic Joas
 * @version 1.0
 */
public class Note {
    private int ID;
    private String title;
    private String description;
    private Date memoryDate;

    public Note() {
        this.ID = 0;
        this.title = "";
        this.description = "";
        this.memoryDate = null;
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

    public Date getMemoryDate() {
        return this.memoryDate;
    }

    public void setMemoryDate(Date memoryDate) {
        this.memoryDate = memoryDate;
    }
}
