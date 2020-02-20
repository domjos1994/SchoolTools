/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;

/**
 * Activity For the Class-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableClassActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvSchoolClass;
    private EditText txtSchoolClassName, txtSchoolClassNumberOfPupil, txtSchoolClassDescription;
    private BottomNavigationView navigation;
    private long currentID;

    private Validator validator;

    public TimeTableClassActivity() {
        super(R.layout.timetable_class_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initActions() {
        Helper.closeSoftKeyboard(TimeTableClassActivity.this);

        this.lvSchoolClass.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            SchoolClass schoolClass = (SchoolClass) listObject;
            if (schoolClass != null) {
                currentID = schoolClass.getId();
                txtSchoolClassName.setText(schoolClass.getTitle());
                txtSchoolClassNumberOfPupil.setText(String.valueOf(schoolClass.getNumberOfPupils()));
                txtSchoolClassDescription.setText(schoolClass.getDescription());
                navigation.getMenu().getItem(1).setEnabled(true);
                navigation.getMenu().getItem(2).setEnabled(true);
            }
        });

        this.lvSchoolClass.setOnReloadListener(this::reloadSchoolClass);

        this.lvSchoolClass.setOnDeleteListener(listObject -> {
            MainActivity.globals.getSqLite().deleteEntry("classes", "ID", listObject.getId(), "");

            currentID = 0;
            navigation.getMenu().getItem(1).setEnabled(false);
            navigation.getMenu().getItem(2).setEnabled(false);
            controlFields(false, true);
            reloadSchoolClass();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, getApplicationContext(), "help_timetable"));
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener
                = item -> {
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
                                schoolClass.setId(currentID);
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
                };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.txtSchoolClassName = this.findViewById(R.id.txtSchoolClassName);
        this.txtSchoolClassNumberOfPupil = this.findViewById(R.id.txtSchoolClassNumberOfPupil);
        this.txtSchoolClassDescription = this.findViewById(R.id.txtSchoolClassDescription);
        this.lvSchoolClass = this.findViewById(R.id.lvSchoolClass);
        this.controlFields(false, false);
        this.reloadSchoolClass();
        this.navigation.getMenu().getItem(1).setEnabled(false);
        this.navigation.getMenu().getItem(2).setEnabled(false);
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(TimeTableClassActivity.this, R.mipmap.ic_launcher_round);
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
        }
    }

    private void reloadSchoolClass() {
        this.lvSchoolClass.getAdapter().clear();
        for (SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
            this.lvSchoolClass.getAdapter().add(schoolClass);
        }
    }
}
