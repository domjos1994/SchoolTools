/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.TeacherAdapter;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Teacher-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableTeacherActivity extends AbstractActivity {

    private ListView lvTeachers;
    private TeacherAdapter teacherAdapter;
    private EditText txtTeacherFirstName, txtTeacherLastName, txtTeacherDescription;
    private BottomNavigationView navigation;
    private int currentID;

    private Validator validator;

    public TimeTableTeacherActivity() {
        super(R.layout.timetable_teacher_activity);
    }

    @Override
    protected void initActions() {
        Helper.closeSoftKeyboard(TimeTableTeacherActivity.this);

        this.lvTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Teacher teacher = teacherAdapter.getItem(position);
                if(teacher!=null) {
                    currentID = teacher.getID();
                    txtTeacherFirstName.setText(teacher.getFirstName());
                    txtTeacherLastName.setText(teacher.getLastName());
                    txtTeacherDescription.setText(teacher.getDescription());
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
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_timetable"));
    }

    @Override
    protected void initControls() {
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
                        MainActivity.globals.getSqLite().deleteEntry("teachers", "ID", currentID, "");

                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, true);
                        reloadTeachers();
                        return true;
                    case R.id.navTimeTableSubSave:
                        if(validator.getState()) {
                            Teacher teacher = new Teacher();
                            teacher.setID(currentID);
                            teacher.setFirstName(txtTeacherFirstName.getText().toString());
                            teacher.setLastName(txtTeacherLastName.getText().toString());
                            teacher.setDescription(txtTeacherDescription.getText().toString());

                            if(validateName(teacher) || currentID!=0) {
                                MainActivity.globals.getSqLite().insertOrUpdateTeacher(teacher);

                                currentID = 0;
                                navigation.getMenu().getItem(1).setEnabled(false);
                                navigation.getMenu().getItem(2).setEnabled(false);
                                controlFields(false, true);
                                reloadTeachers();
                            } else {
                                Helper.createToast(getApplicationContext(), getString(R.string.message_validator_teachers));
                            }
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
        this.txtTeacherFirstName = this.findViewById(R.id.txtTeacherFirstName);
        this.txtTeacherLastName = this.findViewById(R.id.txtTeacherLastName);
        this.txtTeacherDescription = this.findViewById(R.id.txtTeacherDescription);
        this.teacherAdapter = new TeacherAdapter(TimeTableTeacherActivity.this, R.layout.timetable_teacher_item, new ArrayList<Teacher>());
        this.lvTeachers = this.findViewById(R.id.lvTeacher);
        this.lvTeachers.setAdapter(this.teacherAdapter);
        this.teacherAdapter.notifyDataSetChanged();
        this.controlFields(false, false);
        this.reloadTeachers();
        this.navigation.getMenu().getItem(1).setEnabled(false);
        this.navigation.getMenu().getItem(2).setEnabled(false);
    }

    private boolean validateName(Teacher teacher) {
        List<Teacher> teacherList = MainActivity.globals.getSqLite().getTeachers("");
        for(Teacher currentTeacher : teacherList) {
            if(!teacher.getFirstName().isEmpty()) {
                if(
                    teacher.getLastName().equals(currentTeacher.getLastName()) &&
                    teacher.getFirstName().equals(currentTeacher.getFirstName())) {

                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTeacherLastName, 2, 500);
    }

    private void controlFields(boolean editMode, boolean reset) {
        this.txtTeacherFirstName.setEnabled(editMode);
        this.txtTeacherLastName.setEnabled(editMode);
        this.txtTeacherDescription.setEnabled(editMode);
        this.lvTeachers.setEnabled(!editMode);

        Helper.showMenuControls(editMode, this.navigation);

        if(reset) {
            this.txtTeacherFirstName.setText("");
            this.txtTeacherLastName.setText("");
            this.txtTeacherDescription.setText("");
            this.lvTeachers.setSelection(-1);
        }
    }

    private void reloadTeachers() {
        this.teacherAdapter.clear();
        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
            this.teacherAdapter.add(teacher);
        }
    }
}
