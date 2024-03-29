/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import de.domjos.schooltools.factories.TimeTableRemoteFactory;

/**
 * Service for the TimeTableWidget
 * @see de.domjos.schooltools.factories.TimeTableRemoteFactory
 * @see de.domjos.schooltools.widgets.TimeTableWidget
 * @author Dominic Joas
 * @version 0.1
 */
public class TimeTableWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimeTableRemoteFactory(this.getApplicationContext(), intent);
    }
}
