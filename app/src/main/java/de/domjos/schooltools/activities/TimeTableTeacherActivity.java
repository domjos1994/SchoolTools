
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
import android.support.v7.app.AppCompatActivity;
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
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Teacher-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableTeacherActivity extends AppCompatActivity {

    private ListView lvTeachers;
    private TeacherAdapter teacherAdapter;
    private EditText txtTeacherFirstName, txtTeacherLastName, txtTeacherDescription;
    private BottomNavigationView navigation;
    private int currentID;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_teacher_activity);
        this.initControls();
        this.initValidation();
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
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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

                            if(validateName(teacher)) {
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
        this.navigation = (BottomNavigationView) findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.txtTeacherFirstName = (EditText) this.findViewById(R.id.txtTeacherFirstName);
        this.txtTeacherLastName = (EditText) this.findViewById(R.id.txtTeacherLastName);
        this.txtTeacherDescription = (EditText) this.findViewById(R.id.txtTeacherDescription);
        this.teacherAdapter = new TeacherAdapter(this.getApplicationContext(), R.layout.timetable_teacher_item, new ArrayList<Teacher>());
        this.lvTeachers = (ListView) this.findViewById(R.id.lvTeacher);
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

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTeacherLastName, 2, 500);
    }

    private void controlFields(boolean editMode, boolean reset) {
        this.txtTeacherFirstName.setEnabled(editMode);
        this.txtTeacherLastName.setEnabled(editMode);
        this.txtTeacherDescription.setEnabled(editMode);
        this.lvTeachers.setEnabled(!editMode);

        if(editMode) {
            this.navigation.getMenu().getItem(0).setEnabled(false);
            this.navigation.getMenu().getItem(1).setEnabled(false);
            this.navigation.getMenu().getItem(2).setEnabled(false);
            this.navigation.getMenu().getItem(3).setEnabled(true);
            this.navigation.getMenu().getItem(4).setEnabled(true);
        } else {
            this.navigation.getMenu().getItem(0).setEnabled(true);
            this.navigation.getMenu().getItem(3).setEnabled(false);
            this.navigation.getMenu().getItem(4).setEnabled(false);
        }

        if(reset) {
            this.txtTeacherFirstName.setText("");
            this.txtTeacherLastName.setText("");
            this.txtTeacherDescription.setText("");
            this.lvTeachers.setSelection(-1);
        }
    }

    public void reloadTeachers() {
        this.teacherAdapter.clear();
        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
            this.teacherAdapter.add(teacher);
        }
    }
}
