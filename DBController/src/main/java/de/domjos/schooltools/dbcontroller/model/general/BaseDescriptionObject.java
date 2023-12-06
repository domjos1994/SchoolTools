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

public class BaseDescriptionObject extends BaseObject {

    @ColumnInfo(name="description")
    private String description;

    public BaseDescriptionObject() {
        super();
        this.description = "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
