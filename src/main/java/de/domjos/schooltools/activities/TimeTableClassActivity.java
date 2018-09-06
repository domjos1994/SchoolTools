/*
 * Copyright (C) 2017-2018  Dominic Joas
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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.ClassAdapter;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Class-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableClassActivity extends AppCompatActivity {
    private ListView lvSchoolClass;
    private ClassAdapter classAdapter;
    private EditText txtSchoolClassName, txtSchoolClassNumberOfPupil, txtSchoolClassDescription;
    private BottomNavigationView navigation;
    private int currentID;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_class_activity);
        this.initControls();
        this.initValidation();
        Helper.closeSoftKeyboard(TimeTableClassActivity.this);

        this.lvSchoolClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SchoolClass schoolClass = classAdapter.getItem(position);
                if (schoolClass != null) {
                    currentID = schoolClass.getID();
                    txtSchoolClassName.setText(schoolClass.getTitle());
                    txtSchoolClassNumberOfPupil.setText(String.valueOf(schoolClass.getNumberOfPupils()));
                    txtSchoolClassDescription.setText(schoolClass.getDescription());
                    navigation.getMenu().getItem(1).setEnabled(true);
                    navigation.getMenu().getItem(2).setEnabled(true);
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
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimeTableSubAdd:
                        currentID = 0;
                        controlFields(true, true);
                        return true;
                    case R.id.navTimeTableSubEdit:
                        controlFields(true, false);
                        return true;
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("classes", "ID", currentID, "");

                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, true);
                        reloadSchoolClass();
                        return true;
                    case R.id.navTimeTableSubSave:
                        if (validator.getState()) {
                            SchoolClass schoolClass = new SchoolClass();
                            schoolClass.setID(currentID);
                            schoolClass.setTitle(txtSchoolClassName.getText().toString());
                            if (!txtSchoolClassNumberOfPupil.getText().toString().equals("")) {
                                schoolClass.setNumberOfPupils(Integer.parseInt(txtSchoolClassNumberOfPupil.getText().toString()));
                            }
                            schoolClass.setDescription(txtSchoolClassDescription.getText().toString());
                            MainActivity.globals.getSqLite().insertOrUpdateClass(schoolClass);

                            currentID = 0;
                            navigation.getMenu().getItem(1).setEnabled(false);
                            navigation.getMenu().getItem(2).setEnabled(false);
                            controlFields(false, true);
                            reloadSchoolClass();
                        }
                        return true;
                    case R.id.navTimeTableSubCancel:
                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, true);
                        return true;
                }
                return false;
            }

        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.txtSchoolClassName = this.findViewById(R.id.txtSchoolClassName);
        this.txtSchoolClassNumberOfPupil = this.findViewById(R.id.txtSchoolClassNumberOfPupil);
        this.txtSchoolClassDescription = this.findViewById(R.id.txtSchoolClassDescription);
        this.classAdapter = new ClassAdapter(TimeTableClassActivity.this, R.layout.timetable_class_item, new ArrayList<SchoolClass>());
        this.lvSchoolClass = this.findViewById(R.id.lvSchoolClass);
        this.lvSchoolClass.setAdapter(this.classAdapter);
        this.classAdapter.notifyDataSetChanged();
        this.controlFields(false, false);
        this.reloadSchoolClass();
        this.navigation.getMenu().getItem(1).setEnabled(false);
        this.navigation.getMenu().getItem(2).setEnabled(false);
    }

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtSchoolClassName, 1, 500);
    }

    private void controlFields(boolean editMode, boolean reset) {
        this.txtSchoolClassName.setEnabled(editMode);
        this.txtSchoolClassNumberOfPupil.setEnabled(editMode);
        this.txtSchoolClassDescription.setEnabled(editMode);
        this.lvSchoolClass.setEnabled(!editMode);

        Helper.showMenuControls(editMode, this.navigation);

        if (reset) {
            this.txtSchoolClassName.setText("");
            this.txtSchoolClassNumberOfPupil.setText("");
            this.txtSchoolClassDescription.setText("");
            this.lvSchoolClass.setSelection(-1);
        }
    }

    private void reloadSchoolClass() {
        this.classAdapter.clear();
        for (SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
            this.classAdapter.add(schoolClass);
        }
    }
}
