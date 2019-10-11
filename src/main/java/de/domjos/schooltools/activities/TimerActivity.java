/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.TimerAdapter;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Timer-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimerActivity extends AbstractActivity {
    private TextView lblTimerDate;
    private ImageView ivTimerPrevious, ivTimerNext;
    private ListView lvTimerEvents;
    private FloatingActionButton cmdTimerEventAdd;

    private TimerAdapter adapter;

    public TimerActivity() {
        super(R.layout.timer_activity);
    }

    @Override
    protected void initActions() {
        this.reloadEvents();
        this.changeDate();

        this.ivTimerPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(Converter.convertStringToDate(lblTimerDate.getText().toString()));
                    c.add(Calendar.DATE, -1);
                    lblTimerDate.setText(Converter.convertDateToString(c.getTime()));
                    reloadEvents();
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.ivTimerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(Converter.convertStringToDate(lblTimerDate.getText().toString()));
                    c.add(Calendar.DATE, 1);
                    lblTimerDate.setText(Converter.convertDateToString(c.getTime()));
                    reloadEvents();
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.lblTimerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Converter.convertStringToDate(lblTimerDate.getText().toString()));
                    DatePickerDialog dialog = new DatePickerDialog(
                        TimerActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    try {
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, monthOfYear);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        lblTimerDate.setText(Converter.convertDateToString(calendar.getTime()));
                                        reloadEvents();
                                    } catch (Exception ex) {
                                        Helper.printException(getApplicationContext(), ex);
                                    }
                                }
                            },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.show();
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.lvTimerEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimerEvent timerEvent = adapter.getItem(position);
                if(timerEvent!=null) {
                    Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
                    intent.putExtra("date", lblTimerDate.getText().toString());
                    intent.putExtra("id", timerEvent.getID());
                    startActivityForResult(intent, 99);
                }
            }
        });

        this.cmdTimerEventAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
                intent.putExtra("date", lblTimerDate.getText().toString());
                intent.putExtra("id", 0);
                startActivityForResult(intent, 99);
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
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_timer"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        try {
            if(resultCode==RESULT_OK) {
                if(requestCode==99) {
                    this.reloadEvents();
                }

            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void reloadEvents() {
        this.adapter.clear();
        for(TimerEvent timerEvent : MainActivity.globals.getSqLite().getTimerEvents("eventDate='" + lblTimerDate.getText().toString() +"'")) {
            this.adapter.add(timerEvent);
        }
    }

    @Override
    protected void initControls() {
        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimerLesson:
                        Intent intent = new Intent(getApplicationContext(), TimeTableSubjectActivity.class);
                        intent.putExtra("parent", R.layout.timetable_activity);
                        startActivity(intent);
                        return true;
                    case R.id.navTimerClass:
                        startActivity(new Intent(getApplicationContext(), TimeTableClassActivity.class));
                        break;
                    case R.id.navTimerTeacher:
                        startActivity(new Intent(getApplicationContext(), TimeTableTeacherActivity.class));
                        return true;
                }
                return false;
            }
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        this.lblTimerDate = this.findViewById(R.id.lblTimerDate);
        this.ivTimerPrevious = this.findViewById(R.id.ivTimerPrevious);
        this.ivTimerNext = this.findViewById(R.id.ivTimerNext);
        this.lvTimerEvents = this.findViewById(R.id.lvTimerEvents);
        this.adapter = new TimerAdapter(TimerActivity.this, R.layout.timer_item, new ArrayList<TimerEvent>());
        this.lvTimerEvents.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        this.cmdTimerEventAdd = this.findViewById(R.id.cmdTimerEventAdd);
        this.lblTimerDate.setText(Converter.convertDateToString(new Date()));

        SearchView searchView = this.findViewById(R.id.cmdSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    Date dt = Converter.convertStringToDate(newText);
                    if(dt!=null) {
                        lblTimerDate.setText(Converter.convertDateToString(dt));
                    } else {
                        lblTimerDate.setText(Converter.convertDateToString(new Date()));
                    }
                } catch (Exception ex) {
                    lblTimerDate.setText(Converter.convertDateToString(new Date()));
                }
                reloadEvents();
                return false;
            }
        });
    }

    private void changeDate() {
        if(this.getIntent().hasExtra("date")) {
            lblTimerDate.setText(this.getIntent().getStringExtra("date"));
            this.reloadEvents();
        }
    }
}
