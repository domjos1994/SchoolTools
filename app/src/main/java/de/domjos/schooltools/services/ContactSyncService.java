package de.domjos.schooltools.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import de.domjos.schooltools.adapter.syncAdapter.ContactSyncAdapter;

public class ContactSyncService extends Service {
    private ContactSyncAdapter calendarSyncAdapter;
    private static final Object calendarAdapterLock = new Object();

    public ContactSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        synchronized (ContactSyncService.calendarAdapterLock) {
            if (this.calendarSyncAdapter == null) {
                this.calendarSyncAdapter = new ContactSyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.calendarSyncAdapter.getSyncAdapterBinder();
    }
}
