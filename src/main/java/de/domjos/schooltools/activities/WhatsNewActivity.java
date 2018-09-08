/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import de.domjos.schooltools.R;

public class WhatsNewActivity extends AppCompatActivity {
    public final static String isWhatsNew = "isWhatsNew";
    public final static String title = "title";
    public final static String content = "content";
    public final static String info = "info";
    private TextView lblContent;
    private TextView lblInfo;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whats_new_activity);
        this.preferences = this.getSharedPreferences("desc_data", MODE_PRIVATE);
        this.initControls();
    }

    private void initControls() {
        this.lblContent = this.findViewById(R.id.lblContent);
        this.lblInfo = this.findViewById(R.id.lblInfo);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            String title = bundle.getString(WhatsNewActivity.title);
            String content = bundle.getString(WhatsNewActivity.content);
            String info = bundle.getString(WhatsNewActivity.info, "");

            if(bundle.getBoolean(WhatsNewActivity.isWhatsNew)) {
                content = "whats_new_content";
                title = "whats_new";
            }
            if(this.alreadyShown(title)) {
                setResult(RESULT_OK);
                finish();
            }

            this.setTitle(this.getStringResourceByName(title));
            this.fillContent(content, info);
        }
    }

    private void fillContent(String content, String info) {
        try {
            if(!info.trim().isEmpty()) {
                this.lblInfo.setText(this.getStringResourceByName(info));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lblContent.setText(Html.fromHtml(this.getStringResourceByName(content), Html.FROM_HTML_MODE_COMPACT));
            } else {
                lblContent.setText(Html.fromHtml(this.getStringResourceByName(content)));
            }
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

    private boolean alreadyShown(String key) {
        if(this.preferences.contains(key)) {
            if(this.preferences.getBoolean(key, false)) {
                return true;
            }
        }
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putBoolean(key, true);
        editor.apply();
        return false;
    }

    public static void resetShown(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("desc_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, false);
        editor.apply();
    }
}
