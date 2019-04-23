package de.domjos.schooltools.services;

import android.accounts.Account;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.syncAdapter.Authenticator;

public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    public static Account GetAccount(Context context, String type) {
        return new Account(context.getString(R.string.app_name), type);
    }


    @Override
    public void onCreate() {
        Log.i("APP", "Service created");
        this.mAuthenticator = new Authenticator(this);
    }

    /** When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */

    @Override
    public IBinder onBind(Intent intent) {
        return this.mAuthenticator.getIBinder();
    }

    @Override
    public void onDestroy() {
        Log.i("APP", "Service destroyed");
    }
}
