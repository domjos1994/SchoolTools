/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.domjos.schooltools.helper.Helper;

public abstract class AbstractActivity extends AppCompatActivity {
    private int id;

    public AbstractActivity(int id) {
        super();
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.id);
        this.initControls();
        this.initValidator();
        this.initActions();
        Helper.setBackgroundToActivity(this);

    }

    protected abstract void initControls();
    protected void initValidator() {}
    protected abstract void initActions();
}
