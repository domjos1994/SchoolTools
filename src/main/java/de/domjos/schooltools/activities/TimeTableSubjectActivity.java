/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.ColorAdapter;
import de.domjos.schooltools.adapter.SubjectAdapter;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.helper.Helper;

import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Subject-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableSubjectActivity extends AbstractActivity {
    private BottomNavigationView navigation;
    private ListView lvSubjects;
    private EditText txtSubjectHoursInWeek, txtSubjectDescription;
    private AutoCompleteTextView txtSubjectTitle, txtSubjectAlias;
    private TextView lblSelectedColor;
    private Spinner spSubjectTeachers;
    private CheckBox chkSubjectMainSubject;
    private int currentID;

    private ColorAdapter colorAdapter;
    private ArrayAdapter<String> adapter;
    private SubjectAdapter subjectAdapter;
    private AlertDialog.Builder colorBuilder;

    private Validator validator;

    public TimeTableSubjectActivity() {
        super(R.layout.timetable_subject_activity);
    }

    @Override
    protected void initActions() {
        Helper.closeSoftKeyboard(TimeTableSubjectActivity.this);
        this.reloadTeachers();
        this.reloadSubjects();
        this.controlFields(false, true);
        navigation.getMenu().getItem(1).setEnabled(false);
        navigation.getMenu().getItem(2).setEnabled(false);

        this.lvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjectAdapter.getItem(position);
                if(subject!=null) {
                    currentID = subject.getID();
                    txtSubjectTitle.setText(subject.getTitle());
                    txtSubjectAlias.setText(subject.getAlias());
                    txtSubjectDescription.setText(subject.getDescription());
                    txtSubjectHoursInWeek.setText(String.valueOf(subject.getHoursInWeek()));
                    chkSubjectMainSubject.setChecked(subject.isMainSubject());
                    int color = Integer.parseInt(subject.getBackgroundColor());
                    lblSelectedColor.setBackgroundColor(color);
                    lblSelectedColor.setText(getSelectedName(color));
                    if(subject.getTeacher()!=null) {
                        Teacher t = subject.getTeacher();
                        spSubjectTeachers.setSelection(adapter.getPosition(String.format("%s: %s %s", t.getID(), t.getFirstName(), t.getLastName())));
                    } else {
                        spSubjectTeachers.setSelection(-1);
                    }
                    navigation.getMenu().getItem(1).setEnabled(true);
                    navigation.getMenu().getItem(2).setEnabled(true);
                }
            }
        });

        this.txtSubjectAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(MainActivity.globals.getUserSettings().isAutomaticallySubjects()) {
                    setDefaultSubjectByAlias(editable);
                }
            }
        });

        this.txtSubjectTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(MainActivity.globals.getUserSettings().isAutomaticallySubjects()) {
                    setDefaultSubjectByTitle(editable);
                }
            }
        });

        this.lblSelectedColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorBuilder.create().show();
            }
        });
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return this.changeParent();
    }

    @Override
    public Intent getParentActivityIntent() {
        return this.changeParent();
    }

    private Intent changeParent() {
        int id = this.getIntent().getIntExtra("parent", 0);
        Intent intent = null;
        switch (id) {
            case R.layout.timetable_activity:
                intent = new Intent(this.getApplicationContext(), TimeTableActivity.class);
                break;
            case R.layout.mark_activity:
                intent = new Intent(this.getApplicationContext(), MarkActivity.class);
                break;
            case R.layout.learning_card_overview_activity:
                intent = new Intent(this.getApplicationContext(), LearningCardOverviewActivity.class);
            default:
        }

        return  intent;
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        OnNavigationItemSelectedListener navListener = new OnNavigationItemSelectedListener() {
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
                        MainActivity.globals.getSqLite().deleteEntry("subjects", "ID", currentID, "");

                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, true);
                        reloadSubjects();
                        return true;
                    case R.id.navTimeTableSubSave:
                        if(validator.getState()) {
                            if(isDuplicated()) {
                                Helper.createToast(getApplicationContext(), getString(R.string.message_validator_duplicated));
                            } else {
                                Subject subject = new Subject();
                                subject.setID(currentID);
                                subject.setTitle(txtSubjectTitle.getText().toString());
                                subject.setAlias(txtSubjectAlias.getText().toString());
                                subject.setDescription(txtSubjectDescription.getText().toString());
                                if(!txtSubjectHoursInWeek.getText().toString().equals("")) {
                                    subject.setHoursInWeek(Integer.parseInt(txtSubjectHoursInWeek.getText().toString()));
                                } else {
                                    subject.setHoursInWeek(0);
                                }
                                subject.setMainSubject(chkSubjectMainSubject.isChecked());
                                int color = 0;
                                if(lblSelectedColor.getBackground()!=null) {
                                    if(lblSelectedColor.getBackground() instanceof ColorDrawable) {
                                        color = ((ColorDrawable) lblSelectedColor.getBackground()).getColor();
                                    }
                                }
                                subject.setBackgroundColor(String.valueOf(color));
                                if(spSubjectTeachers.getSelectedItem()!=null) {
                                    if(!spSubjectTeachers.getSelectedItem().toString().trim().equals("")) {
                                        int id = Integer.parseInt(spSubjectTeachers.getSelectedItem().toString().split(":")[0]);
                                        Teacher teacher = MainActivity.globals.getSqLite().getTeachers("ID=" + id).get(0);
                                        subject.setTeacher(teacher);
                                    }
                                }
                                MainActivity.globals.getSqLite().insertOrUpdateSubject(subject);

                                currentID = 0;
                                navigation.getMenu().getItem(1).setEnabled(false);
                                navigation.getMenu().getItem(2).setEnabled(false);
                                controlFields(false, false);
                                reloadSubjects();
                            }
                        }
                        return true;
                    case R.id.navTimeTableSubCancel:
                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, false);
                        return true;
                }
                return false;
            }
        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.lvSubjects = this.findViewById(R.id.lvSubjects);
        this.txtSubjectTitle = this.findViewById(R.id.txtSubjectTitle);
        this.txtSubjectTitle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, this.fillTitleAutoCompleteFields()));
        this.txtSubjectAlias = this.findViewById(R.id.txtSubjectAlias);
        List<String> aliases = Arrays.asList("M", "Mus", "D", "Eng", "NWA", "NWT", "Bio", "Ch", "Ph", "Geo", "Gk", "Rel", "Mus", "Edv", "Itg", "Inf", "Sp", "Spa", "lat");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, aliases);
        this.txtSubjectAlias.setAdapter(adapter);


        this.txtSubjectDescription = this.findViewById(R.id.txtSubjectDescription);
        this.txtSubjectHoursInWeek = this.findViewById(R.id.txtSubjectHoursInWeek);
        this.spSubjectTeachers = this.findViewById(R.id.spSubjectTeachers);
        this.chkSubjectMainSubject = this.findViewById(R.id.chkSubjectMainSubject);
        this.lblSelectedColor = this.findViewById(R.id.lblSelectedColor);
        this.colorAdapter = new ColorAdapter(this);
        this.colorAdapter.notifyDataSetChanged();
        for(String color : getResources().getStringArray(R.array.colorNames)) {
            this.colorAdapter.add(color);
        }
        this.colorBuilder = new AlertDialog.Builder(this);
        this.colorBuilder.setTitle(R.string.timetable_color);
        this.colorBuilder.setAdapter(this.colorAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String entry = colorAdapter.getItem(i);
                if (entry != null) {
                    lblSelectedColor.setBackgroundResource(colorAdapter.getSelectedColor(entry));
                    lblSelectedColor.setText(entry);
                    dialogInterface.dismiss();
                }
            }
        });
        this.colorBuilder.setNegativeButton(R.string.sys_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        this.adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spSubjectTeachers.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();

        this.subjectAdapter = new SubjectAdapter(TimeTableSubjectActivity.this, R.layout.timetable_subject_item, new ArrayList<Subject>());
        this.lvSubjects.setAdapter(this.subjectAdapter);
        this.subjectAdapter.notifyDataSetChanged();
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

    private List<String> fillTitleAutoCompleteFields() {
        List<String> list = new LinkedList<>();
        list.add(this.getString(R.string.timetable_subject_d_name));
        list.add(this.getString(R.string.timetable_subject_eng_name));
        list.add(this.getString(R.string.timetable_subject_m_name));
        list.add(this.getString(R.string.timetable_subject_nwa_name));
        list.add(this.getString(R.string.timetable_subject_nwt_name));
        list.add(this.getString(R.string.timetable_subject_bio_name));
        list.add(this.getString(R.string.timetable_subject_ch_name));
        list.add(this.getString(R.string.timetable_subject_geo_name));
        list.add(this.getString(R.string.timetable_subject_gk_name));
        list.add(this.getString(R.string.timetable_subject_ph_name));
        list.add(this.getString(R.string.timetable_subject_rel_name));
        list.add(this.getString(R.string.timetable_subject_sp_name));
        list.add(this.getString(R.string.timetable_subject_mus_name));
        list.add(this.getString(R.string.timetable_subject_itg_name));
        list.add(this.getString(R.string.timetable_subject_spa_name));
        list.add(this.getString(R.string.timetable_subject_lat_name));
        return  list;
    }

    private void setDefaultSubjectByTitle(Editable editable) {
        String title = editable.toString();
        this.fillDefaultSubject(title, R.string.timetable_subject_d_name, R.string.timetable_subject_d_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_eng_name, R.string.timetable_subject_eng_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_m_name, R.string.timetable_subject_m_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_nwa_name, R.string.timetable_subject_nwa_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_nwt_name, R.string.timetable_subject_nwt_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_bio_name, R.string.timetable_subject_bio_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_ch_name, R.string.timetable_subject_ch_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_geo_name, R.string.timetable_subject_geo_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_gk_name, R.string.timetable_subject_gk_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_ph_name, R.string.timetable_subject_ph_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_rel_name, R.string.timetable_subject_rel_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_sp_name, R.string.timetable_subject_sp_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_mus_name, R.string.timetable_subject_mus_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_itg_name, R.string.timetable_subject_itg_color, false);
        this.fillDefaultSubject(title, R.string.timetable_subject_spa_name, R.string.timetable_subject_spa_color, true);
        this.fillDefaultSubject(title, R.string.timetable_subject_lat_name, R.string.timetable_subject_lat_color, true);
    }

    private void fillDefaultSubject(String title, int name, int color, boolean main) {
        if(title.equals(this.getString(name))) {
            this.setDefaultValues(color, main);
        }
    }

    private void setDefaultSubjectByAlias(Editable editable) {
        String alias = editable.toString().toLowerCase();
        switch (alias) {
            case "d":
                this.setDefaultValues(R.string.timetable_subject_d_name, R.string.timetable_subject_d_color, true);
                break;
            case "eng":
                this.setDefaultValues(R.string.timetable_subject_eng_name, R.string.timetable_subject_eng_color, true);
                break;
            case "m":
                this.setDefaultValues(R.string.timetable_subject_m_name, R.string.timetable_subject_m_color, true);
                break;
            case "nwa":
                this.setDefaultValues(R.string.timetable_subject_nwa_name, R.string.timetable_subject_nwa_color, true);
                break;
            case "nwt":
                this.setDefaultValues(R.string.timetable_subject_nwt_name, R.string.timetable_subject_nwt_color, true);
                break;
            case "bio":
                this.setDefaultValues(R.string.timetable_subject_bio_name, R.string.timetable_subject_bio_color, false);
                break;
            case "ch":
                this.setDefaultValues(R.string.timetable_subject_ch_name, R.string.timetable_subject_ch_color, false);
                break;
            case "geo":
                this.setDefaultValues(R.string.timetable_subject_geo_name, R.string.timetable_subject_geo_color, false);
                break;
            case "gk":
                this.setDefaultValues(R.string.timetable_subject_gk_name, R.string.timetable_subject_gk_color, false);
                break;
            case "ph":
                this.setDefaultValues(R.string.timetable_subject_ph_name, R.string.timetable_subject_ph_color, false);
                break;
            case "rel":
                this.setDefaultValues(R.string.timetable_subject_rel_name, R.string.timetable_subject_rel_color, false);
                break;
            case "sp":
                this.setDefaultValues(R.string.timetable_subject_sp_name, R.string.timetable_subject_sp_color, false);
                break;
            case "mus":
                this.setDefaultValues(R.string.timetable_subject_mus_name, R.string.timetable_subject_mus_color, false);
                break;
            case "itg":
            case "edv":
            case "inf":
                this.setDefaultValues(R.string.timetable_subject_itg_name, R.string.timetable_subject_itg_color, false);
                break;
            case "spa":
                this.setDefaultValues(R.string.timetable_subject_spa_name, R.string.timetable_subject_spa_color, true);
                break;
            case "lat":
                this.setDefaultValues(R.string.timetable_subject_lat_name, R.string.timetable_subject_lat_color, true);
                break;
            default:
        }
    }

    private void setDefaultValues(int name, int color_name, boolean main) {
        txtSubjectTitle.setText(this.getString(name));
        chkSubjectMainSubject.setChecked(main);
        if(main) {
            txtSubjectHoursInWeek.setText(String.valueOf(4));
        } else {
            txtSubjectHoursInWeek.setText(String.valueOf(2));
        }
        int color = colorAdapter.getSelectedColor(this.getString(color_name));
        lblSelectedColor.setBackgroundColor(this.getResources().getColor(color));
        lblSelectedColor.setText(this.getString(color_name));
    }

    private void setDefaultValues(int color_name, boolean main) {
        chkSubjectMainSubject.setChecked(main);
        if(main) {
            txtSubjectHoursInWeek.setText(String.valueOf(4));
        } else {
            txtSubjectHoursInWeek.setText(String.valueOf(2));
        }
        int color = colorAdapter.getSelectedColor(this.getString(color_name));
        lblSelectedColor.setBackgroundColor(this.getResources().getColor(color));
        lblSelectedColor.setText(this.getString(color_name));
    }

    @Override
    protected void initValidator() {
        this.validator =  new Validator(getApplicationContext());
        this.validator.addLengthValidator(txtSubjectTitle, 1, 500);
        this.validator.addLengthValidator(txtSubjectAlias, 1, 3);
    }

    private boolean isDuplicated() {
        boolean duplicated = false;
        for(int i = 0; i<=this.subjectAdapter.getCount()-1; i++) {
            Subject subject = this.subjectAdapter.getItem(i);
            if(subject!=null) {
                if(txtSubjectAlias.getText().toString().toLowerCase().trim().equals(subject.getAlias().toLowerCase())) {
                    duplicated = true;
                    break;
                }
                if(txtSubjectTitle.getText().toString().toLowerCase().trim().equals(subject.getTitle().toLowerCase())) {
                    duplicated = true;
                    break;
                }
            }
        }
        return duplicated;
    }

    private void controlFields(boolean editMode, boolean reset) {
        this.txtSubjectTitle.setEnabled(editMode);
        this.txtSubjectAlias.setEnabled(editMode);
        this.txtSubjectDescription.setEnabled(editMode);
        this.txtSubjectHoursInWeek.setEnabled(editMode);
        this.spSubjectTeachers.setEnabled(editMode);
        this.chkSubjectMainSubject.setEnabled(editMode);
        this.lvSubjects.setEnabled(!editMode);

        Helper.showMenuControls(editMode, this.navigation);

        if(reset) {
            this.txtSubjectTitle.setText("");
            this.txtSubjectAlias.setText("");
            this.txtSubjectDescription.setText("");
            this.txtSubjectHoursInWeek.setText("");
            this.chkSubjectMainSubject.setChecked(false);
            this.lvSubjects.setSelection(-1);
            this.spSubjectTeachers.setSelection(this.adapter.getPosition(""));
            int color = colorAdapter.getSelectedColor(this.getString(R.string.timetable_subject_rel_color));
            this.lblSelectedColor.setBackgroundColor(this.getResources().getColor(color));
            this.lblSelectedColor.setText(this.getString(R.string.timetable_subject_rel_color));
        }
    }

    private void reloadTeachers() {
        this.adapter.clear();
        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
            this.adapter.add(String.format("%s: %s %s", teacher.getID(), teacher.getFirstName(), teacher.getLastName()).trim());
        }
        this.adapter.add("");
        this.spSubjectTeachers.setSelection(this.adapter.getPosition(""));
    }

    private void reloadSubjects() {
        int wholeHours = 0;
        this.subjectAdapter.clear();
        for(Subject subject : MainActivity.globals.getSqLite().getSubjects("")) {
            wholeHours += subject.getHoursInWeek();
            this.subjectAdapter.add(subject);
        }
        this.setTitle(getString(R.string.timetable_lesson) + " (" + wholeHours + "h)");
    }

    private String getSelectedName(int color) {
        String nameToUse = "Black";
        String[] colorNames = getResources().getStringArray(R.array.colorNames);
        TypedArray ta = getResources().obtainTypedArray(R.array.colors);
        for(int i=0; i<colorNames.length; i++) {
            int colorToUse = ta.getColor(i, 0);
            if(colorToUse==color) {
                nameToUse = colorNames[i];
                break;
            }
        }
        ta.recycle();
        return nameToUse;
    }
}
