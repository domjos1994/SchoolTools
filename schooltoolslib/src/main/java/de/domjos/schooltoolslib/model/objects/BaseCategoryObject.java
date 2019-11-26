/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model.objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;

public class BaseCategoryObject extends BaseDescriptionObject {
    private String category;

    public BaseCategoryObject() {
        super();
        this.category = "";
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
