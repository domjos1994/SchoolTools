/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.mark.Test;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Mark-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class MarkActivity extends AbstractActivity {
    private String subject = "", year = "";
    private FloatingActionButton cmdTestAdd;

    private SwipeRefreshDeleteList lvTest;
    private TextView lblMark;
    private Spinner spMarkYear, spMarkSubject;
    private ArrayAdapter<String> yearAdapter, subjectAdapter;

    public MarkActivity() {
        super(R.layout.mark_activity);
    }

    @Override
    protected void initActions() {
        Helper.setBackgroundToActivity(this);
        this.reloadSubject();
        this.reloadYear();
        this.reloadTests();
        int id = this.getIntent().getIntExtra("id", 0);
        if(id!=0) {
            for(int i = 0; i<=subjectAdapter.getCount()-1;i++) {
                List<Subject> subjectList = MainActivity.globals.getSqLite().getSubjects("title='" + subjectAdapter.getItem(i) + "'");
                if(subjectList!=null) {
                    if(!subjectList.isEmpty()) {
                        if(subjectList.get(0).getId()==id) {
                            subject = subjectList.get(0).getTitle();
                            spMarkSubject.setSelection(subjectAdapter.getPosition(subject));
                            reloadTests();
                            break;
                        }
                    }
                }
            }
        }
        Helper.setBackgroundToActivity(this);

        this.cmdTestAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MarkEntryActivity.class);
            intent.putExtra("id", 0);
            intent.putExtra("subject", subject);
            intent.putExtra("year", year);
            startActivityForResult(intent, 98);
        });

        this.lvTest.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Test test = (Test) listObject;

            Intent intent = new Intent(getApplicationContext(), MarkEntryActivity.class);
            if(test!=null) {
                intent.putExtra("id", test.getId());
            }
            intent.putExtra("subject", subject);
            intent.putExtra("year", year);
            startActivityForResult(intent, 98);
        });

        this.lvTest.setOnReloadListener(this::reloadTests);

        this.lvTest.setOnDeleteListener(listObject -> {
            MainActivity.globals.getSqLite().deleteEntry("tests", "ID", listObject.getId(), "");
            MainActivity.globals.getSqLite().deleteEntry("schoolYears", "test=" + listObject.getId());
            reloadTests();
        });

        this.spMarkYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = yearAdapter.getItem(position);
                reloadTests();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                year = "";
            }
        });

        this.spMarkSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = subjectAdapter.getItem(position);
                reloadTests();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subject = "";
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
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_calculate_mark"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            if (requestCode == 99) {
                this.subject = "";
                this.year = "";
                this.reloadSubject();
                this.reloadYear();
            }

            if (resultCode == RESULT_OK) {
                if (requestCode == 98) {
                    this.reloadTests();
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkActivity.this);
        }
    }

    private void reloadYear() {
        this.yearAdapter.clear();
        for(Year year : MainActivity.globals.getSqLite().getYears("")) {
            this.yearAdapter.add(year.getTitle());
        }
    }

    private void reloadSubject() {
        this.subjectAdapter.clear();
        this.subjectAdapter.add("");
        for(Subject subject : MainActivity.globals.getSqLite().getSubjects("")) {
            this.subjectAdapter.add(subject.getTitle());
        }
    }

    @SuppressLint("RestrictedApi")
    private void reloadTests() {
        this.cmdTestAdd.setVisibility(View.VISIBLE);
        if(this.yearAdapter.isEmpty()) {
            this.cmdTestAdd.setVisibility(View.GONE);
            return;
        }
        if(this.subjectAdapter.isEmpty() || this.spMarkSubject.getSelectedItem()==null) {
            this.cmdTestAdd.setVisibility(View.GONE);
        } else if(this.spMarkSubject.getSelectedItem().equals("")) {
            this.cmdTestAdd.setVisibility(View.GONE);
        }

        this.lvTest.getAdapter().clear();
        double mark = 0.0;
        int countMark = 0;
        for(SchoolYear schoolYear : MainActivity.globals.getSqLite().getSchoolYears(subject, year)) {
            double curMark = schoolYear.calculateAverage();
            mark += curMark;
            for(Test test : schoolYear.getTests()) {
                test.setDescription(this.getString(R.string.mark_mark) + ": " + test.getMark() + ", " + this.getString(R.string.mark_average) + ": " + test.getAverage() + ", " + this.getString(R.string.mark_weight) + ": " + test.getWeight());
                this.lvTest.getAdapter().add(test);
            }
            if(curMark!=0.0) {
                countMark++;
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        if(countMark!=0) {
            this.lblMark.setText(df.format(mark / countMark));
        } else {
            this.lblMark.setText(df.format(0.0));
        }
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
            Intent intent;
            switch (Helper.checkMenuID(item)) {
                case R.id.navMarkLesson:
                    intent = new Intent(getApplicationContext(), TimeTableSubjectActivity.class);
                    intent.putExtra("parent", R.layout.mark_activity);
                    startActivityForResult(intent, 99);
                    break;
                case R.id.navMarkYear:
                    intent = new Intent(getApplicationContext(), MarkYearActivity.class);
                    intent.putExtra("parent", R.layout.mark_activity);
                    startActivityForResult(intent, 99);
                    break;
                default:
            }
            return false;
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        // init other controls
        this.cmdTestAdd = this.findViewById(R.id.cmdTestAdd);
        this.lblMark = this.findViewById(R.id.lblMark);

        this.lvTest = this.findViewById(R.id.lvTest);

        this.spMarkSubject = this.findViewById(R.id.spMarkSubject);
        this.subjectAdapter = new ArrayAdapter<>(MarkActivity.this, R.layout.spinner_item, new ArrayList<>());
        this.spMarkSubject.setAdapter(this.subjectAdapter);
        this.subjectAdapter.notifyDataSetChanged();

        this.spMarkYear = this.findViewById(R.id.spMarkYear);
        this.yearAdapter = new ArrayAdapter<>(MarkActivity.this, R.layout.spinner_item, new ArrayList<>());
        this.spMarkYear.setAdapter(this.yearAdapter);
        this.yearAdapter.notifyDataSetChanged();
    }
}
