/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.mark.Test;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.customwidgets.utils.Validator;

/**
 * Activity For the Mark-Entry-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class MarkEntryActivity extends AbstractActivity {
    private SQLite sql;
    private long currentID;
    private Validator validator;

    private TextView lblTestYear, lblTestSubject;
    private EditText txtTestTitle, txtTestWeight, txtTestMark,
            txtTestAverage, txtTestDate, txtTestThemes, txtTestDescription, txtTestMemoryDate;
    private CheckBox chkTestMemory, chkTestTimerEvent, chkTestToDoList;

    public MarkEntryActivity() {
        super(R.layout.mark_entry_activity);
    }

    @Override
    protected void initActions() {
        this.sql = MainActivity.globals.getSqLite();
        Helper.setBackgroundToActivity(this);

        try {
            this.loadData();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkEntryActivity.this);
        }

        this.chkTestMemory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                txtTestMemoryDate.setVisibility(View.VISIBLE);
            } else {
                txtTestMemoryDate.setVisibility(View.GONE);
                txtTestMemoryDate.setText("");
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

        if (id == R.id.menHelp) {
            startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
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
                        txtTestDate.setText(ConvertHelper.convertDateToString(test.getTestDate(), this.getApplicationContext()));
                    }
                    chkTestMemory.setChecked(true);
                    txtTestMemoryDate.setVisibility(View.VISIBLE);
                    txtTestMemoryDate.setText(ConvertHelper.convertDateToString(test.getMemoryDate(), this.getApplicationContext()));

                    txtTestThemes.setText(test.getThemes());
                    txtTestDescription.setText(test.getDescription());

                    if(sql.entryExists("timerEvents", "title='" + test.getTitle() + "' AND category='" + this.getString(R.string.mark_test) + "'")) {
                        this.chkTestTimerEvent.setChecked(true);
                    }

                    if(sql.entryExists("toDoLists", "title='" + test.getTitle() + "'")) {
                        this.chkTestToDoList.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    protected void initControls() {
        try {
            // init navigation_learning_card_group
            OnNavigationItemSelectedListener listener = item -> {
                try {
                    switch (Helper.checkMenuID(item)) {
                        case R.id.navTimeTableSubDelete:
                            MainActivity.globals.getSqLite().deleteEntry("tests", "ID", currentID, "");
                            MainActivity.globals.getSqLite().deleteEntry("schoolYears", "test=" + currentID);
                            setResult(RESULT_OK);
                            finish();
                            break;
                        case R.id.navTimeTableSubSave:
                            if(validator.getState()) {
                                Test test = new Test();
                                test.setId(currentID);
                                test.setTitle(txtTestTitle.getText().toString());
                                test.setWeight(Double.parseDouble(txtTestWeight.getText().toString()));
                                if(!txtTestMark.getText().toString().equals("")) {
                                    test.setMark(Double.parseDouble(txtTestMark.getText().toString()));
                                }
                                if(!txtTestAverage.getText().toString().equals("")) {
                                    test.setAverage(Double.parseDouble(txtTestAverage.getText().toString()));
                                }
                                if(!txtTestDate.getText().toString().equals("")) {
                                    test.setTestDate(ConvertHelper.convertStringToDate(txtTestDate.getText().toString(), this.getApplicationContext()));
                                }
                                if(!txtTestMemoryDate.getText().toString().equals("")) {
                                    test.setMemoryDate(ConvertHelper.convertStringToDate(txtTestMemoryDate.getText().toString(), this.getApplicationContext()));
                                }
                                test.setThemes(txtTestThemes.getText().toString());
                                test.setDescription(txtTestDescription.getText().toString());

                                if(chkTestTimerEvent.isChecked()) {
                                    if(!sql.entryExists("timerEvents", "title='" + test.getTitle() + "' AND category='" + test + "'")) {
                                        TimerEvent event = new TimerEvent();
                                        event.setTitle(test.getTitle());
                                        event.setDescription(test.getDescription());
                                        event.setEventDate(test.getTestDate());
                                        event.setMemoryDate(test.getMemoryDate());
                                        event.setSubject(MainActivity.globals.getSqLite().getSubjects("title='" + lblTestSubject.getText() + "'").get(0));
                                        event.setCategory(getString(R.string.mark_test));
                                        sql.insertOrUpdateTimerEvent(event);
                                    }
                                }

                                if(chkTestToDoList.isChecked() && !test.getThemes().isEmpty()) {
                                    if(!sql.entryExists("toDoLists", "title='" + test.getTitle() + "'")) {
                                        ToDoList toDoList = new ToDoList();
                                        toDoList.setTitle(test.getTitle());
                                        toDoList.setListDate(test.getTestDate());
                                        toDoList.setDescription(test.getDescription());
                                        sql.insertOrUpdateToDoList(toDoList);
                                        for(String theme : test.getThemes().split("\n")) {
                                            ToDo toDo = new ToDo();
                                            toDo.setTitle(theme);
                                            toDo.setSolved(false);
                                            toDo.setImportance(5);
                                            toDo.setMemoryDate(test.getMemoryDate());
                                            sql.insertOrUpdateToDo(toDo, toDoList.getTitle());
                                        }

                                    }
                                }

                                MainActivity.globals.getSqLite().insertOrUpdateSchoolYear(lblTestSubject.getText().toString(), lblTestYear.getText().toString(), test);
                                setResult(RESULT_OK);
                                finish();
                            }
                            break;
                        case R.id.navTimeTableSubCancel:
                            setResult(RESULT_OK);
                            finish();
                            break;
                        default:
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkEntryActivity.this);
                }
                return false;
            };
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(listener);
            navigation.getMenu().removeItem(R.id.navTimeTableSubAdd);
            navigation.getMenu().removeItem(R.id.navTimeTableSubEdit);

            // init other Controls
            this.lblTestSubject = this.findViewById(R.id.lblTestSubject);
            this.lblTestYear = this.findViewById(R.id.lblTestYear);
            this.lblTestSubject.setText(this.getIntent().getStringExtra("subject"));
            this.lblTestYear.setText(this.getIntent().getStringExtra("year"));
            this.currentID = getIntent().getLongExtra("id", 0);
            boolean enabled = this.getIntent().getBooleanExtra("enabled", true);

            this.txtTestTitle = this.findViewById(R.id.txtTestTitle);
            this.txtTestWeight = this.findViewById(R.id.txtTestWeight);
            this.txtTestMark = this.findViewById(R.id.txtTestMark);
            this.txtTestAverage = this.findViewById(R.id.txtTestAverage);
            this.txtTestDate = this.findViewById(R.id.txtTestDate);
            this.chkTestMemory = this.findViewById(R.id.chkTestMemory);
            this.txtTestMemoryDate = this.findViewById(R.id.txtTestMemoryDate);
            this.txtTestMemoryDate.setVisibility(View.GONE);
            this.chkTestTimerEvent = this.findViewById(R.id.chkTestTimerEvent);
            this.txtTestThemes = this.findViewById(R.id.txtTestThemes);
            this.chkTestToDoList = this.findViewById(R.id.chkTestToDoList);
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
                this.chkTestTimerEvent.setEnabled(false);
                this.chkTestToDoList.setEnabled(false);
                navigation.getMenu().getItem(0).setEnabled(false);
                navigation.getMenu().getItem(1).setEnabled(false);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkEntryActivity.this);
        }
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(MarkEntryActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addLengthValidator(txtTestTitle, 2, 500);
        this.validator.addDoubleValidator(txtTestWeight);
        this.validator.addEmptyValidator(txtTestWeight);
        this.validator.addDoubleValidator(txtTestMark);
        this.validator.addDoubleValidator(txtTestAverage);
        this.validator.addDateValidator(txtTestDate);
    }
}
