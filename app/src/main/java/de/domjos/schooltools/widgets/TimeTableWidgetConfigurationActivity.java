/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.widgets;


import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;

/**
 * Configuration Screen for TimeTableWidget
 * @see de.domjos.schooltools.widgets.TimeTableWidget
 * @see de.domjos.schooltools.services.TimeTableWidgetService
 * @see de.domjos.schooltools.factories.TimeTableRemoteFactory
 * @author Dominic Joas
 * @version 0.1
 */
public class TimeTableWidgetConfigurationActivity extends AppCompatActivity {
    private int appWidgetID;
    private Button cmdSave;
    private Spinner cmbTimeTables;
    private ArrayAdapter<String> timeTableAdapter;
    private SQLite sqLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_widget_configuration_activity);
        setResult(RESULT_CANCELED);
        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            this.appWidgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        this.initControls();
        this.reloadTimeTables();

        this.cmdSave.setOnClickListener(v -> {
            if(this.cmbTimeTables.getSelectedItemPosition()!=-1) {
                String selected = timeTableAdapter.getItem(cmbTimeTables.getSelectedItemPosition());

                if (selected != null) {
                    List<TimeTable> timeTableList = sqLite.getTimeTables("");
                    for (TimeTable timeTable : timeTableList) {
                        if (selected.equals(timeTable.getTitle())) {
                            getSettings(timeTable.getId());
                        }
                    }
                }
            }
        });
    }

    private void reloadTimeTables() {
        this.sqLite = new SQLite(this.getApplicationContext());
        for(TimeTable timeTable : sqLite.getTimeTables("")) {
            this.timeTableAdapter.add(timeTable.getTitle());
        }
    }

    private void initControls() {
        this.cmdSave = this.findViewById(R.id.cmdSave);

        this.timeTableAdapter = new ArrayAdapter<>(TimeTableWidgetConfigurationActivity.this, R.layout.spinner_item, new ArrayList<>());
        this.cmbTimeTables = this.findViewById(R.id.cmbTimeTable);
        this.cmbTimeTables.setAdapter(this.timeTableAdapter);
        this.timeTableAdapter.notifyDataSetChanged();
    }

    private void setTT_ID(long id) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        editor.putLong("tt_id_" + this.appWidgetID, id);
        editor.apply();
    }

    private void getSettings(long timeTableID) {
        try {
            this.setTT_ID(timeTableID);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
            TimeTableWidget.updateAppWidget(this.getApplicationContext(), appWidgetManager, this.appWidgetID);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetID);
            resultValue.putExtra("timeTableID", timeTableID);
            setResult(RESULT_OK, resultValue);
            finish();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getApplicationContext());
        }
    }
}
