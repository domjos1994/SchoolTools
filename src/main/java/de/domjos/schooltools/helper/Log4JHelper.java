/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.app.Activity;

import de.domjos.schooltools.activities.MainActivity;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * @author Dominic Joas
 */

public class Log4JHelper {
    private final static LogConfigurator logConfigurator = new LogConfigurator();

    public static void configure(Activity activity) {
        MainActivity.globals.setLogFile(activity.getApplicationContext().getFileStreamPath("schoolTools.log").getAbsolutePath());
        logConfigurator.setFileName(MainActivity.globals.getLogFile());
        logConfigurator.setMaxFileSize(1024 * 1024);
        logConfigurator.setFilePattern("%d - [%c] - %p : %m%n");
        logConfigurator.setMaxBackupSize(10);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.configure();
    }

    public static org.apache.log4j.Logger getLogger( String name ) {
        return org.apache.log4j.Logger.getLogger(name);
    }
}
