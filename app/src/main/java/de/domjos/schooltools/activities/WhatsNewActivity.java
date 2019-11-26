/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;

public final class WhatsNewActivity extends AbstractActivity {
    public final static String isShownAlways = "isShownAlways";
    public final static String isWhatsNew = "isWhatsNew";
    public final static String TITLE_PARAM = "TITLE_PARAM";
    public final static String CONTENT_PARAM = "CONTENT_PARAM";
    public final static String INFO_PARAM = "INFO_PARAM";
    public final static String PARENT_CLASS = "ParentClassSource";

    private String title, content, info;
    private boolean whatsNew, shownAlways;

    private TextView lblContent;
    private TextView lblInfo;
    private SharedPreferences preferences;

    public WhatsNewActivity() {
        super(R.layout.whats_new_activity, MainActivity.globals.getSqLite().getSetting("background"));
    }

    @Override
    protected void initActions(){}

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent= getIntent();
        String className = parentIntent.getStringExtra(WhatsNewActivity.PARENT_CLASS);

        Intent newIntent=null;
        try {
            if(className!=null) {
                newIntent = new Intent(WhatsNewActivity.this, Class.forName(className));
            } else {
                newIntent = new Intent(WhatsNewActivity.this, MainActivity.class);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    protected void initControls() {
        this.getParameters();

        this.preferences = this.getSharedPreferences("desc_data", MODE_PRIVATE);
        this.lblContent = this.findViewById(R.id.lblContent);
        this.lblInfo = this.findViewById(R.id.lblInfo);


        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = this.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        if(this.whatsNew) {
            content = "whats_new_content";
            title = "whats_new";
        }
        if(this.alreadyShown(title + "_" + MainActivity.globals.getGeneralSettings().getCurrentVersionCode(this))) {
            setResult(RESULT_OK);
            finish();
        }

        this.setTitle(this.getStringResourceByName(title));
        this.fillContent(content, info);
    }

    private void fillContent(String content, String info) {
        try {
            if(!info.trim().isEmpty()) {
                Helper.showHTMLInTextView(getApplicationContext(), info, this.lblInfo);
            } else {
                this.lblInfo.setVisibility(View.GONE);
            }

            Helper.showHTMLInTextView(getApplicationContext(), content, this.lblContent);
        } catch (Exception ex) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        if(packageName!=null && aString!=null) {
            int resId = getResources().getIdentifier(aString, "string", packageName);
            return getString(resId);
        } else {
            return "";
        }
    }

    private boolean alreadyShown(String key) {
        if(!this.shownAlways) {
            if(this.preferences.contains(key)) {
                if(this.preferences.getBoolean(key, false)) {
                    return true;
                }
            }
            SharedPreferences.Editor editor = this.preferences.edit();
            editor.putBoolean(key, true);
            editor.apply();
            return false;
        } else {
            return false;
        }
    }

    private void getParameters() {
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            this.title = bundle.getString(WhatsNewActivity.TITLE_PARAM);
            this.content = bundle.getString(WhatsNewActivity.CONTENT_PARAM);
            this.info = bundle.getString(WhatsNewActivity.INFO_PARAM, "");
            this.whatsNew = bundle.getBoolean(WhatsNewActivity.isWhatsNew);
            this.shownAlways = bundle.getBoolean(WhatsNewActivity.isShownAlways);
        }
    }

    public static void resetShown(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("desc_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, false);
        editor.apply();
    }
}
