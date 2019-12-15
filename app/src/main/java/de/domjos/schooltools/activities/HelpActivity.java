/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import android.widget.TextView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Help-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class HelpActivity extends AbstractActivity {
    private File logFile;

    public HelpActivity() {
        super(R.layout.help_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {}

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
                        Helper.sendMailWithAttachment("webmaster@domjos.de", "SchoolTools-LogFile", this.logFile, HelpActivity.this);
                        MessageHelper.printMessage(getString(R.string.message_help_send_success), R.mipmap.ic_launcher_round, HelpActivity.this);
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, HelpActivity.this);
                    }
                }
                break;
            case R.id.menHelpDeleteLog:
                if(this.logFile.exists()) {
                    if(this.logFile.delete()) {
                        MessageHelper.printMessage(getString(R.string.message_help_delete_success), R.mipmap.ic_launcher_round, HelpActivity.this);
                    }
                }
                break;
            case R.id.menHelpAbout:
                Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
                intent.putExtra(WhatsNewActivity.isWhatsNew, false);
                intent.putExtra(WhatsNewActivity.isShownAlways, true);
                intent.putExtra(WhatsNewActivity.INFO_PARAM, "");
                intent.putExtra(WhatsNewActivity.TITLE_PARAM, "help_about");
                intent.putExtra(WhatsNewActivity.CONTENT_PARAM, "help_about_content");
                intent.putExtra(WhatsNewActivity.PARENT_CLASS, HelpActivity.class.getName());
                startActivity(intent);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initControls() {
        try {
            this.logFile = new File(MainActivity.globals.getLogFile());
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, HelpActivity.this);
        }


        // init toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = this.getIntent();
        String id = "help_start";
        if(intent!=null) {
            String tmp = intent.getStringExtra("helpId");
            if(tmp!=null) {
                if(!tmp.equals("")) {
                    id = tmp;
                }
            }
        }

        TextView lblContent = this.findViewById(R.id.lblContent);
        Helper.showHTMLInTextView(getApplicationContext(), id, lblContent);
    }
}
