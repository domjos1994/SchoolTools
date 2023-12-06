/*
 * Copyright (C) 2017-2023  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.dbcontroller.model.general;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

public class BaseObject {
    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(name = "entry_date")
    private Date created;

    public BaseObject() {
        this.ID = 0L;
        this.created = new Date();
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getID() {
        return this.ID;
    }

    public void setID(long id) {
        this.ID = id;
    }
}
