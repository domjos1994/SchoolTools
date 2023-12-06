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

@Entity(tableName = "subjects")
public class Subject extends BaseDescriptionObject {
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "alias")
    private String alias;

    @ColumnInfo(name = "hoursInWeek")
    private String hoursInWeek;

    @ColumnInfo(name = "isMainSubject")
    private boolean isMainSubject;

    @ColumnInfo(name = "backgroundColor")
    private int backgroundColor;
}
