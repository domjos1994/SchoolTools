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
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;

/**
 * Activity For the Timer-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimerEntryActivity extends AbstractActivity {

    private CheckBox chkTimerMemory;
    private EditText txtTimerTitle, txtTimerDescription, txtTimerCategories, txtTimerMemoryDate;
    private Spinner spTimerSubject, spTimerTeacher, spTimerClass;

    private ArrayAdapter<String> subjectAdapter, teacherAdapter, classAdapter;

    private Date currentDate;
    private int currentID;
    private Validator validator;

    public TimerEntryActivity() {
        super(R.layout.timer_entry_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.reloadComboBoxes();
        this.fillData();

        this.chkTimerMemory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                txtTimerMemoryDate.setVisibility(View.VISIBLE);
            } else {
                txtTimerMemoryDate.setText("");
                txtTimerMemoryDate.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item,getApplicationContext(),"help_timer"));
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
                txtTimerMemoryDate.setText(ConvertHelper.convertDateToString(event.getMemoryDate(), this.getApplicationContext()));
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

    @Override
    protected void initValidator() {
        this.validator = new Validator(TimerEntryActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addLengthValidator(txtTimerTitle, 3, 500);
        this.validator.addDateValidator(txtTimerMemoryDate, false);
    }

    @Override
    protected void initControls() {
        try {
            // init navigation_learning_card_group
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            OnNavigationItemSelectedListener listener = item -> {
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
                                timerEvent.setId(currentID);
                                timerEvent.setEventDate(currentDate);
                                timerEvent.setTitle(txtTimerTitle.getText().toString());
                                timerEvent.setDescription(txtTimerDescription.getText().toString());
                                timerEvent.setCategory(txtTimerCategories.getText().toString());
                                if(!txtTimerMemoryDate.getText().toString().equals("")) {
                                    timerEvent.setMemoryDate(ConvertHelper.convertStringToDate(txtTimerMemoryDate.getText().toString(), this.getApplicationContext()));
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
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimerEntryActivity.this);
                        }
                        break;
                    case R.id.navTimeTableSubCancel:
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default:
                }
                return false;
            };
            navigation.getMenu().removeItem(R.id.navTimeTableSubAdd);
            navigation.getMenu().removeItem(R.id.navTimeTableSubEdit);
            navigation.setOnNavigationItemSelectedListener(listener);

            // init other controls
            this.currentID = this.getIntent().getIntExtra("id", 0);
            if(this.getIntent()!=null) {
                Intent intent = this.getIntent();
                String extra = intent.getStringExtra("date");
                if(extra!=null) {
                    this.currentDate = ConvertHelper.convertStringToDate(extra, this.getApplicationContext());
                }
            }

            TextView lblTimerDate = this.findViewById(R.id.lblTimerDate);
            lblTimerDate.setText(this.getIntent().getStringExtra("date"));
            this.txtTimerTitle = this.findViewById(R.id.txtTimerTitle);
            this.txtTimerDescription = this.findViewById(R.id.txtTimerDescription);
            this.txtTimerCategories = this.findViewById(R.id.txtTimerCategories);
            this.txtTimerMemoryDate = this.findViewById(R.id.txtTimerMemoryDate);
            this.chkTimerMemory = this.findViewById(R.id.chkTimerMemory);
            this.txtTimerMemoryDate.setVisibility(View.GONE);

            this.spTimerSubject = this.findViewById(R.id.spTimerSubject);
            this.subjectAdapter = new ArrayAdapter<>(TimerEntryActivity.this, R.layout.spinner_item, new ArrayList<>());
            this.spTimerSubject.setAdapter(this.subjectAdapter);
            this.subjectAdapter.notifyDataSetChanged();

            this.spTimerTeacher = this.findViewById(R.id.spTimerTeacher);
            this.teacherAdapter = new ArrayAdapter<>(TimerEntryActivity.this, R.layout.spinner_item, new ArrayList<>());
            this.spTimerTeacher.setAdapter(this.teacherAdapter);
            this.teacherAdapter.notifyDataSetChanged();

            this.spTimerClass = this.findViewById(R.id.spTimerClass);
            this.classAdapter = new ArrayAdapter<>(TimerEntryActivity.this, R.layout.spinner_item, new ArrayList<>());
            this.spTimerClass.setAdapter(this.classAdapter);
            this.classAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimerEntryActivity.this);
        }
    }
}
