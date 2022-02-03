/*
 * Copyright (C) 2017-2022  Dominic Joas
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

import android.widget.TextView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Help-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class HelpActivity extends AbstractActivity {

    public HelpActivity() {
        super(R.layout.help_activity);
    }

    @Override
    protected void initActions() {
        Helper.setBackgroundToActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menHelpAbout) {
            Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
            intent.putExtra(WhatsNewActivity.isWhatsNew, false);
            intent.putExtra(WhatsNewActivity.isShownAlways, true);
            intent.putExtra(WhatsNewActivity.INFO_PARAM, "");
            intent.putExtra(WhatsNewActivity.TITLE_PARAM, "help_about");
            intent.putExtra(WhatsNewActivity.CONTENT_PARAM, "help_about_content");
            intent.putExtra(WhatsNewActivity.PARENT_CLASS, HelpActivity.class.getName());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initControls() {
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
