/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.Dialog;
import android.graphics.Color;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

import android.text.Editable;
import android.text.TextWatcher;
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

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.schooltoolslib.model.timetable.Day;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.PupilHour;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltoolslib.model.timetable.TeacherHour;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;
import de.domjos.schooltools.settings.UserSettings;

/**
 * Activity For the TimeTable-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableEntryActivity extends AbstractActivity {

    private BottomNavigationView navigation;
    private EditText txtTimeTableTitle, txtTimeTableDescription;
    private CheckBox chkTimeTableCurrent;
    private Spinner spTimeTableClass, spTimeTableYear;
    private TableLayout gridContent;
    private TimeTable currentItem = null;
    private ScrollView svControls;

    private Validator validator;
    private Map<String, Integer> mpSubjects;

    public TimeTableEntryActivity() {
        super(R.layout.timetable_entry_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.mpSubjects = new LinkedHashMap<>();
        this.initGrid();
        Helper.closeSoftKeyboard(TimeTableEntryActivity.this);

        this.spTimeTableYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeTitle(spTimeTableYear.getSelectedItem(), spTimeTableClass.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeTitle(spTimeTableYear.getSelectedItem(), spTimeTableClass.getSelectedItem());
            }
        });

        this.spTimeTableClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeTitle(spTimeTableYear.getSelectedItem(), spTimeTableClass.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeTitle(spTimeTableYear.getSelectedItem(), spTimeTableClass.getSelectedItem());
            }
        });

        this.txtTimeTableTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeTitle(spTimeTableYear.getSelectedItem(), spTimeTableClass.getSelectedItem());
            }
        });
    }

    private void changeTitle(Object year, Object cls) {
        if(this.txtTimeTableTitle.getText().toString().equals("")) {
            String title = "";
            if(year!=null) {
                title = year.toString();
                if(cls!=null) {
                   title += " - " + cls.toString();
                }
            } else {
               if(cls!=null) {
                   title = cls.toString();
               }
            }
            this.txtTimeTableTitle.setText(title);
        }
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        OnNavigationItemSelectedListener navListener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
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
                                currentItem.setCurrentTimeTable(chkTimeTableCurrent.isChecked());
                                currentItem.setDescription(txtTimeTableDescription.getText().toString());
                                if(spTimeTableClass.getSelectedItem()!=null) {
                                    if(!spTimeTableClass.getSelectedItem().toString().equals("")) {
                                        SchoolClass schoolClass = new SchoolClass();
                                        schoolClass.setTitle(spTimeTableClass.getSelectedItem().toString());
                                        currentItem.setSchoolClass(schoolClass);
                                    } else {
                                        currentItem.setSchoolClass(null);
                                    }
                                } else {
                                    currentItem.setSchoolClass(null);
                                }
                                if(spTimeTableYear.getSelectedItem()!=null) {
                                    if(!spTimeTableYear.getSelectedItem().toString().equals("")) {
                                        List<Year> years = MainActivity.globals.getSqLite().getYears("title='" + spTimeTableYear.getSelectedItem() + "'");
                                        if(years!=null) {
                                            if(!years.isEmpty()) {
                                                currentItem.setYear(years.get(0));
                                            }
                                        }
                                    } else {
                                        currentItem.setYear(null);
                                    }
                                } else {
                                    currentItem.setYear(null);
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
                                                if(!MainActivity.globals.getUserSettings().isTimeTableMode()) {
                                                    if(txtCurrent.getTag().toString().contains(" - ")) {
                                                        String[] rows = txtCurrent.getTag().toString().split("\n");
                                                        String[] spl = rows[0].trim().split(" - ");
                                                        Subject subject = MainActivity.globals.getSqLite().getSubjects("ID=" + spl[0].trim()).get(0);
                                                        Teacher teacher = null;
                                                        if(!spl[1].trim().equals("0")) {
                                                            teacher = MainActivity.globals.getSqLite().getTeachers("ID=" + spl[1].trim()).get(0);
                                                        }
                                                        if(rows.length==2) {
                                                            day.addPupilHour(hour, subject, teacher, rows[1].trim());
                                                        } else {
                                                            day.addPupilHour(hour, subject, teacher, "");
                                                        }
                                                    }
                                                } else {
                                                    if(txtCurrent.getTag().toString().contains(" - ")) {
                                                        String[] rows = txtCurrent.getTag().toString().trim().split("\n");
                                                        String[] spl = rows[0].trim().split(" - ");
                                                        Subject subject = MainActivity.globals.getSqLite().getSubjects("ID=" + spl[0].trim()).get(0);
                                                        SchoolClass schoolClass = null;
                                                        if(!spl[1].trim().equals("0")) {
                                                            schoolClass = MainActivity.globals.getSqLite().getClasses("ID=" + spl[1].trim()).get(0);
                                                        }
                                                        if(rows.length==2) {
                                                            day.addTeacherHour(hour, subject, schoolClass, rows[1].trim());
                                                        } else {
                                                            day.addTeacherHour(hour, subject, schoolClass, "");
                                                        }

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
        this.chkTimeTableCurrent = this.findViewById(R.id.chkTimeTableCurrent);

        this.spTimeTableClass = this.findViewById(R.id.spTimeTableClass);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(TimeTableEntryActivity.this, R.layout.spinner_item, new ArrayList<String>());
        this.spTimeTableClass.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();
        classAdapter.add("");
        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("")) {
            classAdapter.add(schoolClass.getTitle());
        }

        this.spTimeTableYear = this.findViewById(R.id.spTimeTableYear);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(TimeTableEntryActivity.this, R.layout.spinner_item, new ArrayList<String>());
        this.spTimeTableYear.setAdapter(yearAdapter);
        yearAdapter.notifyDataSetChanged();
        yearAdapter.add("");
        for(Year year : MainActivity.globals.getSqLite().getYears("")) {
            yearAdapter.add(year.getTitle());
        }

        this.svControls = this.findViewById(R.id.svControls);

        this.txtTimeTableDescription = this.findViewById(R.id.txtTimeTableDescription);
        TimeTableEntryActivity.initTimes(this.gridContent);
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
            if (this.currentItem.getYear() != null) {
                this.spTimeTableYear.setSelection(yearAdapter.getPosition(this.currentItem.getYear().getTitle()));
            }
            if (this.currentItem.getSchoolClass() != null) {
                this.spTimeTableClass.setSelection(classAdapter.getPosition(this.currentItem.getSchoolClass().getTitle()));
            }
            this.txtTimeTableDescription.setText(this.currentItem.getDescription());
            this.chkTimeTableCurrent.setChecked(this.currentItem.isCurrentTimeTable());

            TimeTableEntryActivity.loadTimeTable(this.currentItem, this.gridContent, this.mpSubjects);
        }

        this.controlFields(this.currentItem==null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, getApplicationContext(), "help_timetable"));
    }

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTimeTableTitle, 3, 500);
        this.validator.addEmptyValidator(spTimeTableYear, this.getString(R.string.timetable_year));
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

    public static void loadTimeTable(TimeTable currentItem, TableLayout gridContent, Map<String, Integer> mpSubjects) {
        for(Day day : currentItem.getDays()) {
            if(day!=null) {
                for(int row = 1; row<=gridContent.getChildCount()-1; row++) {
                    TableRow tableRow = (TableRow)gridContent.getChildAt(row);
                    TextView txtTime = (TextView) tableRow.getChildAt(0);
                    TextView txtColumn = (TextView) tableRow.getChildAt(day.getPositionInWeek()+1);

                    if(!MainActivity.globals.getUserSettings().isTimeTableMode()) {
                        for(Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                            if(txtTime.getTag()!=null) {
                                int timeID = Integer.parseInt(txtTime.getTag().toString().trim());
                                if(entry.getKey().getID()==timeID) {
                                    Subject subject = entry.getValue().getSubject();
                                    if(mpSubjects!=null && subject!=null) {
                                        if(subject.getAlias()!=null) {
                                            Integer it = mpSubjects.get(subject.getAlias());
                                            if(it!=null) {
                                                if (mpSubjects.containsKey(subject.getAlias())) {
                                                    mpSubjects.put(subject.getAlias(), (it + 1));
                                                } else {
                                                    mpSubjects.put(subject.getAlias(), 1);
                                                }
                                            }
                                        }
                                    }
                                    Teacher teacher = entry.getValue().getTeacher();
                                    String roomNumber = entry.getValue().getRoomNumber();

                                    txtColumn.setText(subject.getAlias());
                                    if(subject.getBackgroundColor()!=null) {
                                        if(!subject.getBackgroundColor().isEmpty()) {
                                            txtColumn.setBackgroundColor(Integer.parseInt(subject.getBackgroundColor()));
                                        }
                                    }
                                    if(teacher!=null) {
                                        txtColumn.setTag(subject.getID() + " - " + teacher.getID());
                                    } else {
                                        txtColumn.setTag(subject.getID() + " - " + 0);
                                    }
                                    if(roomNumber!=null) {
                                        if(!roomNumber.isEmpty()) {
                                            txtColumn.setTag(txtColumn.getTag() + "\n" + roomNumber);
                                        }
                                    }
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        for(Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                            if(txtTime.getTag()!=null) {
                                int timeID = Integer.parseInt(txtTime.getTag().toString().trim());
                                if(entry.getKey().getID()==timeID) {
                                    Subject subject = entry.getValue().getSubject();
                                    if(mpSubjects.containsKey(subject.getAlias())) {
                                        mpSubjects.put(subject.getAlias(), (mpSubjects.get(subject.getAlias()) + 1));
                                    } else {
                                        mpSubjects.put(subject.getAlias(), 1);
                                    }
                                    SchoolClass schoolClass = entry.getValue().getSchoolClass();
                                    String roomNumber = entry.getValue().getRoomNumber();

                                    txtColumn.setText(subject.getAlias());
                                    if(subject.getBackgroundColor()!=null) {
                                        if(!subject.getBackgroundColor().isEmpty()) {
                                            txtColumn.setBackgroundColor(Integer.parseInt(subject.getBackgroundColor()));
                                        }
                                    }
                                    if(schoolClass!=null) {
                                        txtColumn.setTag(subject.getID() + " - " + schoolClass.getID());
                                    } else {
                                        txtColumn.setTag(subject.getID() + " - " + 0);
                                    }

                                    if(roomNumber!=null) {
                                        if(!roomNumber.isEmpty()) {
                                            txtColumn.setTag(txtColumn.getTag() + "\n" + roomNumber);
                                        }
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

    private void initGrid() {
        for(int i = 1; i<=this.gridContent.getChildCount()-1; i++) {
            final TableRow tableRow = (TableRow) this.gridContent.getChildAt(i);
            for(int j = 0; j<=tableRow.getChildCount()-1; j++) {
                final TextView textView = (TextView) tableRow.getChildAt(j);
                final int finalJ = j;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tableRow.getChildCount()!=0) {
                            if(tableRow.getChildAt(0).getTag()!=null) {
                                Subject oldSubject = null;

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
                                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(TimeTableEntryActivity.this, R.layout.spinner_item, new ArrayList<String>());
                                    spSubjects.setAdapter(subjectAdapter);
                                    subjectAdapter.notifyDataSetChanged();

                                    final Spinner spOptional = dialog.findViewById(R.id.spTeacher);
                                    ArrayAdapter<String> optionalAdapter = new ArrayAdapter<>(TimeTableEntryActivity.this, R.layout.spinner_item, new ArrayList<String>());
                                    spOptional.setAdapter(optionalAdapter);
                                    optionalAdapter.notifyDataSetChanged();

                                    final EditText txtRoomNumber = dialog.findViewById(R.id.txtRoomNumber);

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

                                    if(!MainActivity.globals.getUserSettings().isTimeTableMode()) {
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
                                        String tag[] = textView.getTag().toString().split("\n");
                                        int subjectID = Integer.parseInt(tag[0].trim().split(" - ")[0].trim());
                                        int optionalID = Integer.parseInt(tag[0].trim().split(" - ")[1].trim());
                                        List<Subject> subjects = MainActivity.globals.getSqLite().getSubjects("ID=" + subjectID);
                                        if(subjects!=null) {
                                            if(!subjects.isEmpty()) {
                                                oldSubject = subjects.get(0);
                                                if(mpSubjects.containsKey(oldSubject.getAlias())) {
                                                    mpSubjects.put(oldSubject.getAlias(), mpSubjects.get(oldSubject.getAlias())-1);
                                                }
                                                String formatted = String.format("%s: %s", oldSubject.getID(), oldSubject.getTitle());

                                                boolean isAvailable = false;
                                                for(int i = 0; i<=subjectAdapter.getCount()-1; i++) {
                                                    String item = subjectAdapter.getItem(i);
                                                    if(item!=null) {
                                                        if (item.equals(formatted)) {
                                                            spSubjects.setSelection(i);
                                                            isAvailable = true;
                                                            break;
                                                        }
                                                    }
                                                }

                                                if(!isAvailable) {
                                                    subjectAdapter.add(formatted);
                                                }
                                            }
                                        }

                                        listFromAdapter(subjectAdapter, spSubjects, subjectID);

                                        if(optionalID!=0) {
                                            listFromAdapter(optionalAdapter, spOptional, optionalID);
                                        }
                                        if(tag.length==2) {
                                            txtRoomNumber.setText(tag[1]);
                                        }
                                    }

                                    final Subject finalOldSubject = oldSubject;
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
                                                textView.setTag(textView.getTag() + "\n" + txtRoomNumber.getText());
                                            } else {
                                                textView.setText("");
                                                textView.setBackgroundResource(R.drawable.tbl_border);
                                                textView.setTag(null);

                                                if(finalOldSubject !=null) {
                                                    if(mpSubjects.containsKey(finalOldSubject.getAlias())) {
                                                        if(mpSubjects.get(finalOldSubject.getAlias())==1) {
                                                            mpSubjects.remove(finalOldSubject.getAlias());
                                                        } else {
                                                            mpSubjects.put(finalOldSubject.getAlias(), (mpSubjects.get(finalOldSubject.getAlias()) - 1));
                                                        }
                                                    }
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

    private static void listFromAdapter(ArrayAdapter<String> adapter, Spinner sp, int id) {
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

    public static void initTimes(TableLayout grid) {
        Map<Double, Hour> times = new TreeMap<>();
        List<Hour> hours = MainActivity.globals.getSqLite().getHours("");
        for(Hour hour : hours) {
            times.put(Double.parseDouble(hour.getStart().replace(":", ".")), hour);
        }

        List hourList = Arrays.asList(times.values().toArray());
        int max = hourList.size()-1;
        for(int i = 1; i<=grid.getChildCount()-1; i++) {
            TableRow row = (TableRow) grid.getChildAt(i);
            TextView textView = (TextView) row.getChildAt(0);
            if((i-1)<=max) {
                Hour hour = (Hour) hourList.get(i-1);
                textView.setText(String.format("%s%n%s", hour.getStart(), hour.getEnd()));
                textView.setTag(String.valueOf(hour.getID()));

                if(hour.isBreak()) {
                    textView.setTextSize(14);
                    textView.setText(textView.getText().toString().replace("\n", ":"));
                    for(int j = 1; j<=row.getChildCount()-1; j++) {
                        row.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                        row.setBackgroundResource(R.drawable.tbl_border);
                    }
                }
            } else {
                grid.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }
}
