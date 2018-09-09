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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.TestAdapter;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.mark.SchoolYear;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Mark-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class MarkActivity extends AppCompatActivity {
    private String subject = "", year = "";
    private FloatingActionButton cmdTestAdd;

    private ListView lvTest;
    private TextView lblMark;
    private Spinner spMarkYear, spMarkSubject;
    private ArrayAdapter<String> yearAdapter, subjectAdapter;
    private TestAdapter testAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mark_activity);
        this.initControls();
        this.reloadSubject();
        this.reloadYear();
        this.reloadTests();
        int id = this.getIntent().getIntExtra("id", 0);
        if(id!=0) {
            for(int i = 0; i<=subjectAdapter.getCount()-1;i++) {
                List<Subject> subjectList = MainActivity.globals.getSqLite().getSubjects("TITLE_PARAM='" + subjectAdapter.getItem(i) + "'");
                if(subjectList!=null) {
                    if(!subjectList.isEmpty()) {
                        if(subjectList.get(0).getID()==id) {
                            subject = subjectList.get(0).getTitle();
                            spMarkSubject.setSelection(subjectAdapter.getPosition(subject));
                            reloadTests();
                            break;
                        }
                    }
                }
            }
        }

        this.cmdTestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MarkEntryActivity.class);
                intent.putExtra("id", 0);
                intent.putExtra("subject", subject);
                intent.putExtra("year", year);
                startActivityForResult(intent, 98);
            }
        });

        this.lvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!subject.equals("") && !year.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MarkEntryActivity.class);
                    Test test = testAdapter.getItem(position);
                    if(test!=null) {
                        intent.putExtra("id", test.getID());
                    }
                    intent.putExtra("subject", subject);
                    intent.putExtra("year", year);
                    startActivityForResult(intent, 98);
                }
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        try {
            if(requestCode==99) {
                this.subject = "";
                this.year = "";
                this.reloadSubject();
                this.reloadYear();
            }

            if(resultCode==RESULT_OK) {
                if(requestCode==98) {
                    this.reloadTests();
                }
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
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

        this.testAdapter.clear();
        double mark = 0.0;
        int countMark = 0;
        for(SchoolYear schoolYear : MainActivity.globals.getSqLite().getSchoolYears(subject, year)) {
            double curMark = schoolYear.calculateAverage();
            mark += curMark;
            for(Test test : schoolYear.getTests()) {
                this.testAdapter.add(test);
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

    private void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        // init other controls
        this.cmdTestAdd = this.findViewById(R.id.cmdTestAdd);
        this.lblMark = this.findViewById(R.id.lblMark);

        this.lvTest = this.findViewById(R.id.lvTest);
        this.testAdapter = new TestAdapter(MarkActivity.this, R.layout.mark_item, new ArrayList<Test>());
        this.lvTest.setAdapter(this.testAdapter);
        this.testAdapter.notifyDataSetChanged();

        this.spMarkSubject = this.findViewById(R.id.spMarkSubject);
        this.subjectAdapter = new ArrayAdapter<>(MarkActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spMarkSubject.setAdapter(this.subjectAdapter);
        this.subjectAdapter.notifyDataSetChanged();

        this.spMarkYear = this.findViewById(R.id.spMarkYear);
        this.yearAdapter = new ArrayAdapter<>(MarkActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spMarkYear.setAdapter(this.yearAdapter);
        this.yearAdapter.notifyDataSetChanged();
    }
}
