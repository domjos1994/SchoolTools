/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import de.domjos.schooltools.R;

public class HelpAboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_about_activity);
        this.initControls();


    }

    @SuppressWarnings("deprecation")
    private void initControls() {
        // init toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView lblContent = this.findViewById(R.id.lblContent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lblContent.setText(Html.fromHtml(this.getString(R.string.help_about_content), Html.FROM_HTML_MODE_COMPACT));
        } else {
            lblContent.setText(Html.fromHtml(this.getString(R.string.help_about_content)));
        }
    }
}
