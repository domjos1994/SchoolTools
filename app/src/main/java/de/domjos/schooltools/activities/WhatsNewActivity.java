/*
 * Copyright (C) 2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import de.domjos.schooltools.R;

public class WhatsNewActivity extends AppCompatActivity {
    private TextView lblContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whats_new_activity);
        this.initControls();
    }

    private void initControls() {
        final String phase = MainActivity.generals.getCurrentInternalPhase().toLowerCase();
        final String version = String.valueOf(MainActivity.generals.getCurrentInternalVersion()).replace(".", "_");

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.lblContent = this.findViewById(R.id.lblContent);
        try {
            this.lblContent.setText(Html.fromHtml(this.getStringResourceByName("whats_new_" + phase +  "_" + version)));
        } catch (Exception ex) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }
}
