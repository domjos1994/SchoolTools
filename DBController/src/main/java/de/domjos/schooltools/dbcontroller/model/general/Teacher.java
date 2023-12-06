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
import androidx.room.Entity;

@Entity(tableName = "teachers")
public class Teacher extends BaseObject {
    @ColumnInfo(name="lastName")
    private String lastName;

    @ColumnInfo(name="firstName")
    private String firstName;

    public Teacher() {
        super();

        this.firstName = "";
        this.lastName = "";
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
