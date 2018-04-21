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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Mark-Entry-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class MarkEntryActivity extends AppCompatActivity {
    private int currentID;
    private Validator validator;

    private TextView lblTestYear, lblTestSubject;
    private EditText txtTestTitle, txtTestWeight, txtTestMark,
            txtTestAverage, txtTestDate, txtTestThemes, txtTestDescription, txtTestMemoryDate;
    private CheckBox chkTestMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mark_entry_activity);

        try {
            this.initControls();
            this.initValidator();
            this.loadData();
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }

        this.chkTestMemory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    txtTestMemoryDate.setVisibility(View.VISIBLE);
                } else {
                    txtTestMemoryDate.setVisibility(View.GONE);
                    txtTestMemoryDate.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if(currentID!=0) {
            List<Test> tests = MainActivity.globals.getSqLite().getTests("ID=" + currentID);
            if(tests!=null) {
                if(!tests.isEmpty()) {
                    Test test = tests.get(0);
                    txtTestTitle.setText(test.getTitle());
                    txtTestWeight.setText(String.valueOf(test.getWeight()));
                    if(test.getMark()!=0.0) {
                        txtTestMark.setText(String.valueOf(test.getMark()));
                    }
                    if(test.getAverage()!=0.0) {
                        txtTestAverage.setText(String.valueOf(test.getAverage()));
                    }
                    if(test.getTestDate()!=null) {
                        txtTestDate.setText(Converter.convertDateToString(test.getTestDate()));
                    }
                    if(test.getMemoryDate()!=null) {
                        chkTestMemory.setChecked(true);
                        txtTestMemoryDate.setVisibility(View.VISIBLE);
                        txtTestMemoryDate.setText(Converter.convertDateToString(test.getMemoryDate()));
                    } else {
                        txtTestMemoryDate.setVisibility(View.GONE);
                        chkTestMemory.setChecked(false);
                    }
                    txtTestThemes.setText(test.getThemes());
                    txtTestDescription.setText(test.getDescription());
                }
            }
        }
    }

    private void initControls() {
        try {
            // init navigation
            OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    try {
                        switch (item.getItemId()) {
                            case R.id.navTimeTableSubDelete:
                                MainActivity.globals.getSqLite().deleteEntry("tests", "ID", currentID, "");
                                MainActivity.globals.getSqLite().deleteEntry("schoolYears", "test=" + currentID);
                                setResult(RESULT_OK);
                                finish();
                                break;
                            case R.id.navTimeTableSubSave:
                                if(validator.getState()) {
                                    Test test = new Test();
                                    test.setID(currentID);
                                    test.setTitle(txtTestTitle.getText().toString());
                                    test.setWeight(Double.parseDouble(txtTestWeight.getText().toString()));
                                    if(!txtTestMark.getText().toString().equals("")) {
                                        test.setMark(Double.parseDouble(txtTestMark.getText().toString()));
                                    }
                                    if(!txtTestAverage.getText().toString().equals("")) {
                                        test.setAverage(Double.parseDouble(txtTestAverage.getText().toString()));
                                    }
                                    if(!txtTestDate.getText().toString().equals("")) {
                                        test.setTestDate(Converter.convertStringToDate(txtTestDate.getText().toString()));
                                    }
                                    if(!txtTestMemoryDate.getText().toString().equals("")) {
                                        test.setMemoryDate(Converter.convertStringToDate(txtTestMemoryDate.getText().toString()));
                                    }
                                    test.setThemes(txtTestThemes.getText().toString());
                                    test.setDescription(txtTestDescription.getText().toString());
                                    MainActivity.globals.getSqLite().insertOrUpdateSchoolYear(lblTestSubject.getText().toString(), lblTestYear.getText().toString(), test);
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                break;
                            case R.id.navTimeTableSubCancel:
                                setResult(RESULT_OK);
                                finish();
                                break;
                        }
                    } catch (Exception ex) {
                        Helper.printException(getApplicationContext(), ex);
                    }
                    return false;
                }

            };
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(listener);
            navigation.getMenu().removeItem(R.id.navTimeTableSubAdd);
            navigation.getMenu().removeItem(R.id.navTimeTableSubEdit);
            Helper.removeShiftMode(navigation);

            // init other Controls
            this.lblTestSubject = this.findViewById(R.id.lblTestSubject);
            this.lblTestYear = this.findViewById(R.id.lblTestYear);
            this.lblTestSubject.setText(this.getIntent().getStringExtra("subject"));
            this.lblTestYear.setText(this.getIntent().getStringExtra("year"));
            this.currentID = getIntent().getIntExtra("id", 0);
            boolean enabled = this.getIntent().getBooleanExtra("enabled", true);

            this.txtTestTitle = this.findViewById(R.id.txtTestTitle);
            this.txtTestWeight = this.findViewById(R.id.txtTestWeight);
            this.txtTestMark = this.findViewById(R.id.txtTestMark);
            this.txtTestAverage = this.findViewById(R.id.txtTestAverage);
            this.txtTestDate = this.findViewById(R.id.txtTestDate);
            this.chkTestMemory = this.findViewById(R.id.chkTestMemory);
            this.txtTestMemoryDate = this.findViewById(R.id.txtTestMemoryDate);
            this.txtTestMemoryDate.setVisibility(View.GONE);
            this.txtTestThemes = this.findViewById(R.id.txtTestThemes);
            this.txtTestDescription = this.findViewById(R.id.txtTestDescription);

            if(!enabled) {
                this.txtTestTitle.setEnabled(false);
                this.txtTestWeight.setEnabled(false);
                this.txtTestMark.setEnabled(false);
                this.txtTestAverage.setEnabled(false);
                this.txtTestDate.setEnabled(false);
                this.chkTestMemory.setEnabled(false);
                this.txtTestMemoryDate.setEnabled(false);
                this.txtTestThemes.setEnabled(false);
                this.txtTestDescription.setEnabled(false);
                navigation.getMenu().getItem(0).setEnabled(false);
                navigation.getMenu().getItem(1).setEnabled(false);
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTestTitle, 2, 500);
        this.validator.addDoubleValidator(txtTestWeight);
        this.validator.addEmptyValidator(txtTestWeight);
        this.validator.addDoubleValidator(txtTestMark);
        this.validator.addDoubleValidator(txtTestAverage);
        this.validator.addDateValidator(txtTestDate);
    }
}
