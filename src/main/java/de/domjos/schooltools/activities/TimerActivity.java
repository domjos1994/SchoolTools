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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.core.model.objects.BaseDescriptionObject;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.custom.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.spotlight.OnBoardingHelper;

/**
 * Activity For the Timer-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimerActivity extends AbstractActivity {
    private TextView lblTimerDate;
    private ImageView ivTimerPrevious, ivTimerNext;
    private SwipeRefreshDeleteList lvTimerEvents;
    private FloatingActionButton cmdTimerEventAdd;

    public TimerActivity() {
        super(R.layout.timer_activity);
    }

    @Override
    protected void initActions() {
        this.reloadEvents();
        this.changeDate();

        this.ivTimerPrevious.setOnClickListener(v -> {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(Objects.requireNonNull(Converter.convertStringToDate(lblTimerDate.getText().toString())));
                c.add(Calendar.DATE, -1);
                lblTimerDate.setText(Converter.convertDateToString(c.getTime()));
                reloadEvents();
            } catch (Exception ex) {
                Helper.printException(getApplicationContext(), ex);
            }
        });

        this.ivTimerNext.setOnClickListener(v -> {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(Objects.requireNonNull(Converter.convertStringToDate(lblTimerDate.getText().toString())));
                c.add(Calendar.DATE, 1);
                lblTimerDate.setText(Converter.convertDateToString(c.getTime()));
                reloadEvents();
            } catch (Exception ex) {
                Helper.printException(getApplicationContext(), ex);
            }
        });

        this.lblTimerDate.setOnClickListener(v -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Objects.requireNonNull(Converter.convertStringToDate(lblTimerDate.getText().toString())));
                DatePickerDialog dialog = new DatePickerDialog(
                    TimerActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            try {
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.YEAR, year);
                                calendar1.set(Calendar.MONTH, monthOfYear);
                                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                lblTimerDate.setText(Converter.convertDateToString(calendar1.getTime()));
                                reloadEvents();
                            } catch (Exception ex) {
                                Helper.printException(getApplicationContext(), ex);
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
        });

        this.lvTimerEvents.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                TimerEvent timerEvent = (TimerEvent) listObject;
                if(timerEvent != null) {
                    Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
                    intent.putExtra("date", lblTimerDate.getText().toString());
                    intent.putExtra("id", timerEvent.getID());
                    startActivityForResult(intent, 99);
                }
            }
        });

        this.lvTimerEvents.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("timerEvents", "ID", listObject.getID(), "");
                reloadEvents();
            }
        });

        this.lvTimerEvents.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadEvents();
            }
        });

        this.cmdTimerEventAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
            intent.putExtra("date", lblTimerDate.getText().toString());
            intent.putExtra("id", 0);
            startActivityForResult(intent, 99);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_timer"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 99) {
                    this.reloadEvents();
                }

            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void reloadEvents() {
        this.lvTimerEvents.getAdapter().clear();
        for(TimerEvent timerEvent : MainActivity.globals.getSqLite().getTimerEvents("eventDate='" + lblTimerDate.getText().toString() +"'")) {
            this.lvTimerEvents.getAdapter().add(timerEvent);
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

        BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
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
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        this.lblTimerDate = this.findViewById(R.id.lblTimerDate);
        this.ivTimerPrevious = this.findViewById(R.id.ivTimerPrevious);
        this.ivTimerNext = this.findViewById(R.id.ivTimerNext);
        this.lvTimerEvents = this.findViewById(R.id.lvTimerEvents);
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

        OnBoardingHelper.tutorialTimer(this, cmdTimerEventAdd, lvTimerEvents, ivTimerPrevious, ivTimerNext);
    }

    private void changeDate() {
        if(this.getIntent().hasExtra("date")) {
            lblTimerDate.setText(this.getIntent().getStringExtra("date"));
            this.reloadEvents();
        }
    }
}
