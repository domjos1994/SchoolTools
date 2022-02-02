/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import de.domjos.schooltools.adapter.syncAdapter.CalendarSyncAdapter;

public class CalendarSyncService extends Service {
    private CalendarSyncAdapter calendarSyncAdapter;
    private static final Object calendarAdapterLock = new Object();

    public CalendarSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        synchronized (CalendarSyncService.calendarAdapterLock) {
            if (this.calendarSyncAdapter == null) {
                this.calendarSyncAdapter = new CalendarSyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.calendarSyncAdapter.getSyncAdapterBinder();
    }
}
