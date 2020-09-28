/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.calendar.WidgetCalendar;
import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.evenTypes.WidgetCalendarMemory;
import de.domjos.schooltools.helper.evenTypes.WidgetCalendarTimerEvent;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Timer-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimerActivity extends AbstractActivity {
    private WidgetCalendar widgetCalendar;
    private FloatingActionButton cmdTimerEventAdd;
    private final String dateFormat;

    public TimerActivity() {
        super(R.layout.timer_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
        this.dateFormat = MainActivity.globals.getUserSettings().getDateFormat();
    }

    @Override
    protected void initActions() {
        this.reloadEvents();
        this.changeDate();

        this.widgetCalendar.setOnClick(event -> {
            Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
            intent.putExtra("date", ConvertHelper.convertDateToString(this.widgetCalendar.getCurrentEvent().getCalendar().getTime(), this.dateFormat));
            if(event instanceof WidgetCalendarTimerEvent) {
                intent.putExtra("id", ((WidgetCalendarTimerEvent)event).getTimerEvent().getId());
            }
            startActivityForResult(intent, 99);
        });

        this.widgetCalendar.setOnLongClick(event -> {
            MainActivity.globals.getSqLite().deleteEntry("timerEvents", "ID=" + event.getId());
            this.reloadEvents();
        });

        this.widgetCalendar.setOnHourGroupClick(event -> {
            if(event.getColor()==android.R.color.transparent) {
                Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
                intent.putExtra("date", ConvertHelper.convertDateToString(this.widgetCalendar.getCurrentEvent().getCalendar().getTime(), this.dateFormat));
                if(event instanceof WidgetCalendarTimerEvent) {
                    intent.putExtra("id", ((WidgetCalendarTimerEvent)event).getTimerEvent().getId());
                }
                startActivityForResult(intent, 99);
            }
        });

        this.widgetCalendar.setOnHourHeaderClick(event -> {
            Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
            intent.putExtra("date",  ConvertHelper.convertDateToString(this.widgetCalendar.getCurrentEvent().getCalendar().getTime(), this.dateFormat));
            intent.putExtra("id", 0);
            startActivityForResult(intent, 99);
        });

        this.cmdTimerEventAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TimerEntryActivity.class);
            intent.putExtra("date",  ConvertHelper.convertDateToString(this.widgetCalendar.getCurrentEvent().getCalendar().getTime(), this.dateFormat));
            intent.putExtra("id", 0);
            startActivityForResult(intent, 99);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        menu.findItem(R.id.menShowCalendar).setOnMenuItemClickListener(menuItem -> {
            this.widgetCalendar.showMonth(!menuItem.isChecked());
            menuItem.setChecked(!menuItem.isChecked());
            return false;
        });
        menu.findItem(R.id.menShowDay).setOnMenuItemClickListener(menuItem -> {
            this.widgetCalendar.showDay(!menuItem.isChecked());
            menuItem.setChecked(!menuItem.isChecked());
            return false;
        });
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
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimerActivity.this);
        }
    }

    private void reloadEvents() {
        try {
            this.widgetCalendar.getEvents().clear();
            for(TimerEvent timerEvent : MainActivity.globals.getSqLite().getTimerEvents("")) {
                WidgetCalendarTimerEvent widgetCalendarTimerEvent = new WidgetCalendarTimerEvent();
                widgetCalendarTimerEvent.setTimerEvent(timerEvent);
                this.widgetCalendar.addEvent(widgetCalendarTimerEvent);
            }

            for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
                WidgetCalendarMemory widgetCalendarMemory = new WidgetCalendarMemory();
                widgetCalendarMemory.setMemory(memory);
                switch (memory.getType()) {
                    case Note:
                        widgetCalendarMemory.setIcon(R.drawable.ic_note_black_24dp);
                        break;
                    case toDo:
                        widgetCalendarMemory.setIcon(R.drawable.ic_done_all_black_24dp);
                        break;
                    case Test:
                        widgetCalendarMemory.setIcon(R.drawable.ic_check_circle_black_24dp);
                        break;
                    case timerEvent:
                        widgetCalendarMemory.setIcon(R.drawable.ic_date_range_black_24dp);
                        break;
                }

                this.widgetCalendar.addEvent(widgetCalendarMemory);
            }
            this.widgetCalendar.reload();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimerActivity.this);
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

        this.widgetCalendar = this.findViewById(R.id.widgetCalendar);
        this.widgetCalendar.addGroup(this.getString(R.string.main_nav_timer), new WidgetCalendarTimerEvent().getColor());
        this.widgetCalendar.addGroup(this.getString(R.string.sys_memory), new WidgetCalendarMemory().getColor());

        this.cmdTimerEventAdd = this.findViewById(R.id.cmdTimerEventAdd);

        SearchView searchView = this.findViewById(R.id.cmdSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    Date dt = ConvertHelper.convertStringToDate(newText, getApplicationContext());
                    if(dt!=null) {
                        widgetCalendar.setCurrentDate(dt);
                    } else {
                        widgetCalendar.setCurrentDate(new Date());
                    }
                } catch (Exception ex) {
                    widgetCalendar.setCurrentDate(new Date());
                }
                reloadEvents();
                return false;
            }
        });
    }

    private void changeDate() {
        try {
            if(this.getIntent().hasExtra("date")) {
                String dt = this.getIntent().getStringExtra("date");
                if(dt!=null) {
                    widgetCalendar.setCurrentDate(ConvertHelper.convertStringToDate(dt, this.dateFormat));
                }
                this.reloadEvents();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimerActivity.this);
        }
    }
}
