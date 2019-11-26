/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.customwidgets.model;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import de.domjos.customwidgets.utils.Helper;

public abstract class AbstractActivity extends AppCompatActivity {
    private int id;
    private Map.Entry<String, byte[]> entry;

    public AbstractActivity(int id, Map.Entry<String, byte[]> entry) {
        super();
        this.id = id;
        this.entry = entry;
    }

    public void setBackground(Map.Entry<String, byte[]> entry) {
        this.entry = entry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.id);
        this.initControls();
        this.initValidator();
        this.initActions();
        Helper.setBackgroundToActivity(this, this.entry);

    }

    protected abstract void initControls();
    protected void initValidator() {}
    protected abstract void initActions();
}
