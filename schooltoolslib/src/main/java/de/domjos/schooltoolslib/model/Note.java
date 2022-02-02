/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model;

import java.util.Date;

import de.domjos.customwidgets.model.BaseDescriptionObject;

/**
 * Model-Class for Notices
 * @author Dominic Joas
 * @version 1.0
 */
public class Note extends BaseDescriptionObject {
    private Date memoryDate;

    public Note() {
        super();
        this.memoryDate = null;
    }

    public Date getMemoryDate() {
        if(this.memoryDate!=null) {
            return (Date) this.memoryDate.clone();
        } else {
            return null;
        }
    }

    public void setMemoryDate(Date memoryDate) {
        if(memoryDate!=null) {
            this.memoryDate = (Date) memoryDate.clone();
        } else {
            this.memoryDate = null;
        }
    }
}
