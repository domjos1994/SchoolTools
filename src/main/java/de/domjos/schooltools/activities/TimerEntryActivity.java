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
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Timer-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimerEntryActivity extends AppCompatActivity {

    private CheckBox chkTimerMemory;
    private EditText txtTimerTitle, txtTimerDescription, txtTimerCategories, txtTimerMemoryDate;
    private Spinner spTimerSubject, spTimerTeacher, spTimerClass;

    private ArrayAdapter<String> subjectAdapter, teacherAdapter, classAdapter;

    private Date currentDate;
    private int currentID;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_entry_activity);
        this.initControls();
        this.reloadComboBoxes();
        this.initValidator();
        this.fillData();

        this.chkTimerMemory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    txtTimerMemoryDate.setVisibility(View.VISIBLE);
                } else {
                    txtTimerMemoryDate.setText("");
                    txtTimerMemoryDate.setVisibility(View.GONE);
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

    private void fillData() {
        List<TimerEvent> timerEvents = MainActivity.globals.getSqLite().getTimerEvents("ID=" + currentID);
        if(timerEvents!=null) {
            if(!timerEvents.isEmpty()) {
                TimerEvent event = timerEvents.get(0);
                txtTimerTitle.setText(event.getTitle());
                txtTimerDescription.setText(event.getDescription());
                txtTimerCategories.setText(event.getCategory());
                chkTimerMemory.setChecked(true);
                txtTimerMemoryDate.setVisibility(View.VISIBLE);
                txtTimerMemoryDate.setText(Converter.convertDateToString(event.getMemoryDate()));
                if(event.getSubject()!=null) {
                    spTimerSubject.setSelection(subjectAdapter.getPosition(event.getSubject().getTitle()));
                }
                if(event.getTeacher()!=null) {
                    spTimerTeacher.setSelection(teacherAdapter.getPosition(String.format("%s %s", event.getTeacher().getFirstName(), event.getTeacher().getLastName())));
                }
                if(event.getSchoolClass()!=null) {
                    spTimerClass.setSelection(classAdapter.getPosition(event.getSchoolClass().getTitle()));
                }
            }
        }
    }

    private void reloadComboBoxes() {
        this.subjectAdapter.clear();
        this.subjectAdapter.add("");
        for(Subject subject : MainActivity.globals.getSqLite().getSubjects("")) {
            this.subjectAdapter.add(subject.getTitle());
        }

        this.teacherAdapter.clear();
        this.teacherAdapter.add("");
        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
            this.teacherAdapter.add(teacher.getFirstName() + " " + teacher.getLastName());
        }

        this.classAdapter.clear();
        this.classAdapter.add("");
        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
            this.classAdapter.add(schoolClass.getTitle());
        }
    }

    private void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTimerTitle, 3, 500);
        this.validator.addDateValidator(txtTimerMemoryDate);
    }

    private void initControls() {
        try {
            // init navigation_learning_card_group
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (Helper.checkMenuID(item)) {
                        case R.id.navTimeTableSubDelete:
                            MainActivity.globals.getSqLite().deleteEntry("timerEvents", "ID", currentID, "");
                            setResult(RESULT_OK);
                            finish();
                            break;
                        case R.id.navTimeTableSubSave:
                            try {
                                if(validator.getState()) {
                                    TimerEvent timerEvent = new TimerEvent();
                                    timerEvent.setID(currentID);
                                    timerEvent.setEventDate(currentDate);
                                    timerEvent.setTitle(txtTimerTitle.getText().toString());
                                    timerEvent.setDescription(txtTimerDescription.getText().toString());
                                    timerEvent.setCategory(txtTimerCategories.getText().toString());
                                    if(!txtTimerMemoryDate.getText().toString().equals("")) {
                                        timerEvent.setMemoryDate(Converter.convertStringToDate(txtTimerMemoryDate.getText().toString()));
                                    }
                                    if(spTimerSubject.getSelectedItem()!=null) {
                                        if(!spTimerSubject.getSelectedItem().equals("")) {
                                            timerEvent.setSubject(MainActivity.globals.getSqLite().getSubjects("title='" + spTimerSubject.getSelectedItem() + "'").get(0));
                                        }
                                    }
                                    if(spTimerClass.getSelectedItem()!=null) {
                                        if(!spTimerClass.getSelectedItem().equals("")) {
                                            timerEvent.setSchoolClass(MainActivity.globals.getSqLite().getClasses("title='" + spTimerClass.getSelectedItem() + "'").get(0));
                                        }
                                    }
                                    if(spTimerTeacher.getSelectedItem()!=null) {
                                        if(!spTimerTeacher.getSelectedItem().equals("")) {
                                            String firstName = spTimerTeacher.getSelectedItem().toString().split(" ")[0];
                                            String lastName = spTimerTeacher.getSelectedItem().toString().split(" ")[1];
                                            timerEvent.setTeacher(MainActivity.globals.getSqLite().getTeachers("firstName='" + firstName + "' AND lastName='" + lastName + "'").get(0));
                                        }
                                    }
                                    MainActivity.globals.getSqLite().insertOrUpdateTimerEvent(timerEvent);
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } catch (Exception ex) {
                                Helper.printException(getApplicationContext(), ex);
                            }
                            break;
                        case R.id.navTimeTableSubCancel:
                            setResult(RESULT_OK);
                            finish();
                            break;
                        default:
                    }
                    return false;
                }
            };
            navigation.getMenu().removeItem(R.id.navTimeTableSubAdd);
            navigation.getMenu().removeItem(R.id.navTimeTableSubEdit);
            navigation.setOnNavigationItemSelectedListener(listener);

            // init other controls
            this.currentID = this.getIntent().getIntExtra("id", 0);
            this.currentDate = Converter.convertStringToDate(this.getIntent().getStringExtra("date"));
            TextView lblTimerDate = this.findViewById(R.id.lblTimerDate);
            lblTimerDate.setText(this.getIntent().getStringExtra("date"));
            this.txtTimerTitle = this.findViewById(R.id.txtTimerTitle);
            this.txtTimerDescription = this.findViewById(R.id.txtTimerDescription);
            this.txtTimerCategories = this.findViewById(R.id.txtTimerCategories);
            this.txtTimerMemoryDate = this.findViewById(R.id.txtTimerMemoryDate);
            this.chkTimerMemory = this.findViewById(R.id.chkTimerMemory);
            this.txtTimerMemoryDate.setVisibility(View.GONE);

            this.spTimerSubject = this.findViewById(R.id.spTimerSubject);
            this.subjectAdapter = new ArrayAdapter<>(TimerEntryActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
            this.spTimerSubject.setAdapter(this.subjectAdapter);
            this.subjectAdapter.notifyDataSetChanged();

            this.spTimerTeacher = this.findViewById(R.id.spTimerTeacher);
            this.teacherAdapter = new ArrayAdapter<>(TimerEntryActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
            this.spTimerTeacher.setAdapter(this.teacherAdapter);
            this.teacherAdapter.notifyDataSetChanged();

            this.spTimerClass = this.findViewById(R.id.spTimerClass);
            this.classAdapter = new ArrayAdapter<>(TimerEntryActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
            this.spTimerClass.setAdapter(this.classAdapter);
            this.classAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Helper.printException(getApplicationContext(), ex);
        }
    }
}
