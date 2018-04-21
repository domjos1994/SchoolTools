
/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.File;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Log4JHelper;

/**
 * Activity For the Help-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class HelpActivity extends AppCompatActivity {
    private File logFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        this.initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelpSendLog:
                if(this.logFile.exists()) {
                    try {
                        Helper.sendMailWithAttachment("webmaster@domjos.de", "SchoolTools-LogFile", this.logFile, getApplicationContext());
                        Helper.createToast(getApplicationContext(), getString(R.string.message_help_send_success));
                    } catch (Exception ex) {
                        Helper.printException(getApplicationContext(), ex);
                    }
                }
                break;
            case R.id.menHelpDeleteLog:
                if(this.logFile.exists()) {
                    if(this.logFile.delete()) {
                        Helper.createToast(getApplicationContext(), getString(R.string.message_help_delete_success));
                    }
                }
                break;
            case R.id.menHelpAbout:
                startActivity(new Intent(this.getApplicationContext(), HelpAboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        try {
            this.logFile = new File(Log4JHelper.LOGFILE);
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }


        // init toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebView lblContent = this.findViewById(R.id.lblContent);
        String content = Helper.readFileFromRaw(this.getApplicationContext(), R.raw.help).replace("\n","").replace("\t","");
        lblContent.loadData(content, "", "utf-8");
    }
}
