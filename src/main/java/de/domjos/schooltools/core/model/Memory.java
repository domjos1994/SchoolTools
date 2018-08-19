/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model;

import android.content.Context;

import de.domjos.schooltools.R;

/**
 * Model-Class for the Memories
 * @see de.domjos.schooltools.activities.MainActivity
 * @see de.domjos.schooltools.services.MemoryService
 * @author Dominic Joas
 * @version 1.0
 */
public class Memory {
    private int id;
    private String title;
    private String description;
    private Type type;
    private String date;

    public Memory() {
        this.title = "";
        this.description = "";
        this.type = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStringType(Context context) {
        switch(this.type) {
            case Test:
                return context.getString(R.string.mark_test);
            case Note:
                return context.getString(R.string.main_nav_notes);
            case toDo:
                return context.getString(R.string.main_nav_todo);
            case timerEvent:
                return context.getString(R.string.main_nav_timer);
            default:
                return "";
        }
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public enum Type {
        Test,
        Note,
        toDo,
        timerEvent
    }
}
