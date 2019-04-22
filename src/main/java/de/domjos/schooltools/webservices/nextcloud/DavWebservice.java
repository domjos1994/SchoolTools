/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.webservices.nextcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.net.URL;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;

public class DavWebservice extends AsyncTask<URL, Void, String> {
    private ProgressDialog dialog;

    public DavWebservice(Context context) {
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setTitle(R.string.app_name);
        this.dialog.setMessage("Retrieving data...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(URL... urls) {
        try {
            String url = MainActivity.globals.getUserSettings().getNextCloudHost();
            String user = MainActivity.globals.getUserSettings().getNextCloudUser();
            String pwd = MainActivity.globals.getUserSettings().getNextCloudPwd();
            String davUrl = url + "/remote.php/dav/";

            Sardine sardine = SardineFactory.begin();
            List<DavResource> resources = sardine.list(davUrl);
            for (DavResource res : resources) {
                System.out.println(res);
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.dialog.dismiss();
    }
}
