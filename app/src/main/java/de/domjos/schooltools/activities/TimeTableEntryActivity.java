
/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.core.model.timetable.Day;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;
import de.domjos.schooltools.settings.UserSettings;

/**
 * Activity For the TimeTable-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableEntryActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private EditText txtTimeTableTitle, txtTimeTableDescription;
    private Spinner spTimeTableClass, spTimeTableYear;
    private TableLayout gridContent;
    private TimeTable currentItem = null;
    private ScrollView svControls;

    private ArrayAdapter<String> classAdapter;

    private Validator validator;
    private Map<String, Integer> mpSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_entry_activity);
        this.mpSubjects = new LinkedHashMap<>();
        this.initControls();
        this.reloadClasses();
        this.initGrid();
        Helper.closeSoftKeyboard(TimeTableEntryActivity.this);
    }

    private void initControls() {
        // init BottomNavigation
        OnNavigationItemSelectedListener navListener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navTimeTableSubEdit:
                        controlFields(true);
                        return true;
                    case R.id.navTimeTableSubDelete:
                        if(currentItem!=null) {
                            MainActivity.globals.getSqLite().deleteEntry("plans", "ID", currentItem.getID(), "");
                            MainActivity.globals.getSqLite().deleteEntry("timeTable", "plan", currentItem.getID(), "");
                        }
                        setResult(RESULT_OK);
                        finish();
                        return true;
                    case R.id.navTimeTableSubSave:
                        try {
                            if(validator.getState()) {
                                if(currentItem==null)  {
                                    currentItem = new TimeTable();
                                }
                                currentItem.setTitle(txtTimeTableTitle.getText().toString());
                                currentItem.setDescription(txtTimeTableDescription.getText().toString());
                                if(spTimeTableClass.getSelectedItem()!=null) {
                                    if(!spTimeTableClass.getSelectedItem().toString().equals("")) {
                                        SchoolClass schoolClass = new SchoolClass();
                                        schoolClass.setTitle(spTimeTableClass.getSelectedItem().toString());
                                        currentItem.setSchoolClass(schoolClass);
                                    }
                                }
                                if(spTimeTableYear.getSelectedItem()!=null) {
                                    if(!spTimeTableYear.getSelectedItem().toString().equals("")) {
                                        List<Year> years = MainActivity.globals.getSqLite().getYears("title='" + spTimeTableYear.getSelectedItem() + "'");
                                        if(years!=null) {
                                            if(!years.isEmpty()) {
                                                currentItem.setYear(years.get(0));
                                            }
                                        }
                                    }
                                }

                                for(int i = 0; i<=6; i++) {
                                    Day day = new Day();
                                    day.setPositionInWeek(i);
                                    for(int row = 1; row<=gridContent.getChildCount()-1; row++) {
                                        TableRow tableRow = (TableRow) gridContent.getChildAt(row);
                                        TextView txtHour = (TextView) tableRow.getChildAt(0);
                                        TextView txtCurrent = (TextView) tableRow.getChildAt(i+1);


                                        if(txtHour.getTag()!=null) {
                                            Hour hour = MainActivity.globals.getSqLite().getHours("ID=" + txtHour.getTag().toString().trim()).get(0);
                                            if(txtCurrent.getTag()!=null) {
                                                if(!MainActivity.settings.isTimeTableMode()) {
                                                    if(txtCurrent.getTag().toString().contains(" - ")) {
                                                        String spl[] = txtCurrent.getTag().toString().split(" - ");
                                                        Subject subject = MainActivity.globals.getSqLite().getSubjects("ID=" + spl[0].trim()).get(0);
                                                        Teacher teacher = null;
                                                        if(!spl[1].trim().equals("0")) {
                                                            teacher = MainActivity.globals.getSqLite().getTeachers("ID=" + spl[1].trim()).get(0);
                                                        }
                                                        day.addPupilHour(hour, subject, teacher);
                                                    }
                                                } else {
                                                    if(txtCurrent.getTag().toString().contains(" - ")) {
                                                        String spl[] = txtCurrent.getTag().toString().split(" - ");
                                                        Subject subject = MainActivity.globals.getSqLite().getSubjects("ID=" + spl[0].trim()).get(0);
                                                        SchoolClass schoolClass = null;
                                                        if(!spl[1].trim().equals("0")) {
                                                            schoolClass = MainActivity.globals.getSqLite().getClasses("ID=" + spl[1].trim()).get(0);
                                                        }
                                                        day.addTeacherHour(hour, subject, schoolClass);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    currentItem.addDay(day);
                                }

                                MainActivity.globals.getSqLite().insertOrUpdateTimeTable(currentItem);
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (Exception ex) {
                            Helper.printException(getApplicationContext(), ex);
                        }
                        return true;
                    case R.id.navTimeTableSubCancel:
                        setResult(RESULT_OK);
                        finish();
                        return true;
                    case R.id.navTimeTableShowData:
                        if(svControls.getVisibility()==View.GONE) {
                            svControls.setVisibility(View.VISIBLE);
                        } else {
                            svControls.setVisibility(View.GONE);
                        }
                        return true;
                }
                return false;
            }
        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init Controls
        int currentID = this.getIntent().getIntExtra("id", 0);
        this.gridContent = this.findViewById(R.id.gridContent);
        this.txtTimeTableTitle = this.findViewById(R.id.txtTimeTableTitle);
        this.spTimeTableClass = this.findViewById(R.id.spTimeTableClass);
        this.classAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spTimeTableClass.setAdapter(this.classAdapter);
        this.classAdapter.notifyDataSetChanged();
        this.spTimeTableYear = this.findViewById(R.id.spTimeTableYear);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spTimeTableYear.setAdapter(yearAdapter);
        yearAdapter.notifyDataSetChanged();
        for(Year year : MainActivity.globals.getSqLite().getYears("")) {
            yearAdapter.add(year.getTitle());
        }

        this.svControls = this.findViewById(R.id.svControls);

        this.txtTimeTableDescription = this.findViewById(R.id.txtTimeTableDescription);
        this.initTimes();
        this.initValidation();

        if(currentID!=0) {
            List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("ID=" + currentID);
            if(timeTables!=null) {
                if (!timeTables.isEmpty()) {
                    this.currentItem = timeTables.get(0);
                }
            }
        }

        if(this.currentItem!=null) {
            this.txtTimeTableTitle.setText(this.currentItem.getTitle());
            if(this.currentItem.getYear() != null) {
                this.spTimeTableYear.setSelection(yearAdapter.getPosition(this.currentItem.getYear().getTitle()));
            }
            if (this.currentItem.getSchoolClass() != null) {
                this.spTimeTableClass.setSelection(this.classAdapter.getPosition(this.currentItem.getSchoolClass().getTitle()));
            }
            this.txtTimeTableDescription.setText(this.currentItem.getDescription());

            for(Day day : this.currentItem.getDays()) {
                if(day!=null) {
                    for(int row = 1; row<=gridContent.getChildCount()-1; row++) {
                        TableRow tableRow = (TableRow)gridContent.getChildAt(row);
                        TextView txtTime = (TextView) tableRow.getChildAt(0);
                        TextView txtColumn = (TextView) tableRow.getChildAt(day.getPositionInWeek()+1);

                        if(!MainActivity.settings.isTimeTableMode()) {
                            for(Map.Entry<Hour, Map.Entry<Subject, Teacher>> entry : day.getPupilHour().entrySet()) {
                                if(txtTime.getTag()!=null) {
                                    int timeID = Integer.parseInt(txtTime.getTag().toString().trim());
                                    if(entry.getKey().getID()==timeID) {
                                        Subject subject = entry.getValue().getKey();
                                        if(this.mpSubjects.containsKey(subject.getAlias())) {
                                            this.mpSubjects.put(subject.getAlias(), (this.mpSubjects.get(subject.getAlias()) + 1));
                                        } else {
                                            this.mpSubjects.put(subject.getAlias(), 1);
                                        }
                                        Teacher teacher = entry.getValue().getValue();

                                        txtColumn.setText(subject.getAlias());
                                        txtColumn.setBackgroundColor(Integer.parseInt(subject.getBackgroundColor()));
                                        if(teacher!=null) {
                                            txtColumn.setTag(subject.getID() + " - " + teacher.getID());
                                        } else {
                                            txtColumn.setTag(subject.getID() + " - " + 0);
                                        }
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        } else {
                            for(Map.Entry<Hour, Map.Entry<Subject, SchoolClass>> entry : day.getTeacherHour().entrySet()) {
                                if(txtTime.getTag()!=null) {
                                    int timeID = Integer.parseInt(txtTime.getTag().toString().trim());
                                    if(entry.getKey().getID()==timeID) {
                                        Subject subject = entry.getValue().getKey();
                                        if(this.mpSubjects.containsKey(subject.getAlias())) {
                                            this.mpSubjects.put(subject.getAlias(), (this.mpSubjects.get(subject.getAlias()) + 1));
                                        } else {
                                            this.mpSubjects.put(subject.getAlias(), 1);
                                        }
                                        SchoolClass schoolClass = entry.getValue().getValue();

                                        txtColumn.setText(subject.getAlias());
                                        txtColumn.setBackgroundColor(Integer.parseInt(subject.getBackgroundColor()));
                                        if(schoolClass!=null) {
                                            txtColumn.setTag(subject.getID() + " - " + schoolClass.getID());
                                        } else {
                                            txtColumn.setTag(subject.getID() + " - " + 0);
                                        }
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        this.controlFields(this.currentItem==null);
    }

    public static int dip2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTimeTableTitle, 3, 500);
    }

    private void controlFields(boolean editMode) {
        this.txtTimeTableTitle.setEnabled(editMode);
        this.spTimeTableYear.setEnabled(editMode);
        this.spTimeTableClass.setEnabled(editMode);
        this.txtTimeTableDescription.setEnabled(editMode);
        for(int i = 0; i<=gridContent.getChildCount()-1; i++) {
            TableRow tableRow = (TableRow) gridContent.getChildAt(i);
            for(int j = 0; j<=tableRow.getChildCount()-1; j++) {
                TextView textView = (TextView) tableRow.getChildAt(j);
                textView.setEnabled(editMode);
            }
        }
        this.navigation.getMenu().getItem(0).setEnabled(!editMode);
        this.navigation.getMenu().getItem(1).setEnabled(!editMode);
        this.navigation.getMenu().getItem(1).setEnabled(editMode);
        this.navigation.getMenu().getItem(2).setEnabled(editMode);
    }

    private void initGrid() {
        for(int i = 1; i<=gridContent.getChildCount()-1; i++) {
            final TableRow tableRow = (TableRow) gridContent.getChildAt(i);
            for(int j = 0; j<=tableRow.getChildCount()-1; j++) {
                final TextView textView = (TextView) tableRow.getChildAt(j);
                final int finalJ = j;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tableRow.getChildCount()!=0) {
                            if(tableRow.getChildAt(0).getTag()!=null) {
                                int id = Integer.parseInt(tableRow.getChildAt(0).getTag().toString());
                                Hour hour = MainActivity.globals.getSqLite().getHours("ID=" + id).get(0);
                                if(finalJ != 0 && !hour.isBreak())  {
                                    final Dialog dialog = new Dialog(TimeTableEntryActivity.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.timetable_dialog);
                                    dialog.setCancelable(true);
                                    UserSettings settings = new UserSettings(getApplicationContext());
                                    TextView txtHeader = dialog.findViewById(R.id.txtHeader);
                                    if(settings.isTimeTableMode()) {
                                        txtHeader.setText(getText(R.string.timetable_dialog_title_teacher));
                                        dialog.setTitle(getString(R.string.timetable_dialog_title_teacher));
                                    } else {
                                        txtHeader.setText(getText(R.string.timetable_dialog_title_pupil));
                                        dialog.setTitle(getString(R.string.timetable_dialog_title_pupil));
                                    }


                                    ImageButton cmdAdd = dialog.findViewById(R.id.cmdAdd);

                                    final Spinner spSubjects = dialog.findViewById(R.id.spSubject);
                                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
                                    spSubjects.setAdapter(subjectAdapter);
                                    subjectAdapter.notifyDataSetChanged();

                                    final Spinner spOptional = dialog.findViewById(R.id.spTeacher);
                                    ArrayAdapter<String> optionalAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
                                    spOptional.setAdapter(optionalAdapter);
                                    optionalAdapter.notifyDataSetChanged();

                                    for(Subject subject : MainActivity.globals.getSqLite().getSubjects("")) {
                                        if(mpSubjects.get(subject.getAlias())!=null) {
                                            if(subject.getHoursInWeek()>mpSubjects.get(subject.getAlias())) {
                                                subjectAdapter.add(String.format("%s: %s", subject.getID(), subject.getTitle()));
                                            }
                                        } else {
                                            subjectAdapter.add(String.format("%s: %s", subject.getID(), subject.getTitle()));
                                        }

                                    }
                                    subjectAdapter.add("");
                                    spSubjects.setSelection(subjectAdapter.getPosition(""));

                                    if(!MainActivity.settings.isTimeTableMode()) {
                                        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
                                            optionalAdapter.add(String.format("%s: %s %s", teacher.getID(), teacher.getFirstName(), teacher.getLastName()));
                                        }
                                    } else {
                                        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
                                            optionalAdapter.add(String.format("%s: %s", schoolClass.getID(), schoolClass.getTitle()));
                                        }
                                    }
                                    optionalAdapter.add("");
                                    spOptional.setSelection(optionalAdapter.getPosition(""));

                                    if(textView.getTag()!=null) {
                                        int subjectID = Integer.parseInt(textView.getTag().toString().split(" - ")[0].trim());
                                        int optionalID = Integer.parseInt(textView.getTag().toString().split(" - ")[1].trim());

                                        listFromAdapter(subjectAdapter, spSubjects, subjectID);

                                        if(optionalID!=0) {
                                            listFromAdapter(optionalAdapter, spOptional, optionalID);
                                        }
                                    }

                                    cmdAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String item = spSubjects.getSelectedItem().toString();
                                            if(!item.trim().equals("")) {
                                                Subject subject = MainActivity.globals.getSqLite().getSubjects("ID=" + item.split(":")[0]).get(0);
                                                if(mpSubjects.containsKey(subject.getAlias())) {
                                                    mpSubjects.put(subject.getAlias(), (mpSubjects.get(subject.getAlias()) + 1));
                                                } else {
                                                    mpSubjects.put(subject.getAlias(), 1);
                                                }
                                                textView.setText(subject.getAlias());
                                                textView.setBackgroundColor(Integer.parseInt(subject.getBackgroundColor()));
                                                if(spOptional.getSelectedItem().toString().trim().equals("")) {
                                                    textView.setTag(item.split(":")[0] + " - " + 0);
                                                } else {
                                                    textView.setTag(item.split(":")[0] + " - " + spOptional.getSelectedItem().toString().split(":")[0].trim());
                                                }
                                            }
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private void listFromAdapter(ArrayAdapter<String> adapter, Spinner sp, int id) {
        for(int i = 0; i<=adapter.getCount()-1; i++) {
            String item = adapter.getItem(i);
            if(item!=null) {
                if(!item.isEmpty()) {
                    if(id==Integer.parseInt(item.split(":")[0])) {
                        sp.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void reloadClasses() {
        this.classAdapter.clear();
        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
            this.classAdapter.add(schoolClass.getTitle());
        }
    }

    private void initTimes() {
        Map<Double, Hour> times = new TreeMap<>();
        List<Hour> hours = MainActivity.globals.getSqLite().getHours("");
        for(Hour hour : hours) {
            times.put(Double.parseDouble(hour.getStart().replace(":", ".")), hour);
        }

        List hourList = Arrays.asList(times.values().toArray());
        int max = hourList.size()-1;
        for(int i = 1; i<=gridContent.getChildCount()-1; i++) {
            TableRow row = (TableRow) gridContent.getChildAt(i);
            TextView textView = (TextView) row.getChildAt(0);
            if((i-1)<=max) {
                Hour hour = (Hour) hourList.get(i-1);
                textView.setText(String.format("%s\n%s", hour.getStart(), hour.getEnd()));
                textView.setTag(String.valueOf(hour.getID()));

                if(hour.isBreak()) {
                    textView.setTextSize(14);
                    textView.setText(textView.getText().toString().replace("\n", ":"));
                    for(int j = 1; j<=row.getChildCount()-1; j++) {
                        row.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                        row.setBackgroundResource(R.drawable.tbl_border);
                    }
                }
            }
        }
    }
}
