/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

public class GeneralSettings {
    private final String INTERNAL_VERSION = "internalVersion", INTERNAL_PHASE = "internalPhase";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public GeneralSettings(Context context) {
        this.preferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public void setCurrentInternalVersion(float version) {
        this.editor.putFloat(this.INTERNAL_VERSION, version);
        this.editor.apply();
    }

    public float getCurrentInternalVersion() {
        return this.preferences.getFloat(this.INTERNAL_VERSION, 0.0f);
    }

    public void setCurrentInternalPhase(String phase) {
        this.editor.putString(this.INTERNAL_PHASE, phase);
        this.editor.apply();
    }

    public String getCurrentInternalPhase() {
        return this.preferences.getString(this.INTERNAL_PHASE, "");
    }
}
