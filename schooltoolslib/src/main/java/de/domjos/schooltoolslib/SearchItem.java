/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib;

import de.domjos.customwidgets.model.objects.BaseObject;

/**
 * @author Dominic Joas
 */

public class SearchItem extends BaseObject {
    private String type;
    private String extra;

    public SearchItem(int id, String title, String type) {
        super();
        super.setID(id);
        super.setTitle(title);
        this.type = type;
        this.extra = "";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
