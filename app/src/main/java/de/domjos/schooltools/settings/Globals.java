/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;

import de.domjos.schooltools.helper.SQLite;

/**
 * Created by Dominic Joas on 20.07.2017.
 */

public class Globals {
    private SQLite sqLite;
    private boolean startScreen;
    private UserSettings userSettings;
    private GeneralSettings generalSettings;

    public Globals() {
        this.sqLite = null;
        this.startScreen = false;
        this.userSettings = null;
        this.generalSettings = null;
    }

    public SQLite getSqLite() {
        return sqLite;
    }

    public void setSqLite(SQLite sqLite) {
        this.sqLite = sqLite;
    }

    void setStartScreen(boolean startScreen) {
        this.startScreen = startScreen;
    }

    boolean isStartScreen() {
        return this.startScreen;
    }

    public UserSettings getUserSettings() {
        return this.userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public GeneralSettings getGeneralSettings() {
        return this.generalSettings;
    }

    public void setGeneralSettings(GeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }
}
