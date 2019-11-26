/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model;

import android.content.Context;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.schooltoolslib.R;

/**
 * Model-Class for the Memories
 * @author Dominic Joas
 * @version 1.0
 */
public class Memory extends BaseDescriptionObject {
    private Type type;
    private String date;

    public Memory() {
        super();
        this.type = null;
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
