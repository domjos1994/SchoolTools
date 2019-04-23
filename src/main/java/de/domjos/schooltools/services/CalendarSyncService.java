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
