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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.HourAdapter;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Hour-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableHourActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private ListView lvHours;
    private TimePicker tpHoursStart, tpHoursEnd;
    private CheckBox chkHoursBreak;
    private int currentID;

    private HourAdapter hourAdapter;
    private int intLatestHour, intLatestMinute;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_hour_activity);
        this.initControls();
        Helper.closeSoftKeyboard(TimeTableHourActivity.this);

        this.lvHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hour hour = hourAdapter.getItem(position);
                if(hour!=null) {
                    currentID = hour.getID();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tpHoursStart.setHour(Integer.parseInt(hour.getStart().split(":")[0]));
                        tpHoursStart.setMinute(Integer.parseInt(hour.getStart().split(":")[1]));
                        tpHoursEnd.setHour(Integer.parseInt(hour.getEnd().split(":")[0]));
                        tpHoursEnd.setMinute(Integer.parseInt(hour.getEnd().split(":")[1]));
                    } else {
                        tpHoursStart.setCurrentHour(Integer.parseInt(hour.getStart().split(":")[0]));
                        tpHoursStart.setCurrentMinute(Integer.parseInt(hour.getStart().split(":")[1]));
                        tpHoursEnd.setCurrentHour(Integer.parseInt(hour.getEnd().split(":")[0]));
                        tpHoursEnd.setCurrentMinute(Integer.parseInt(hour.getEnd().split(":")[1]));
                    }
                    chkHoursBreak.setChecked(hour.isBreak());
                    navigation.getMenu().getItem(1).setEnabled(true);
                    navigation.getMenu().getItem(2).setEnabled(true);
                }
            }
        });

        this.chkHoursBreak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    configureTimes(intLatestHour, intLatestMinute, MainActivity.globals.getUserSettings().getBreakTime());
                } else {
                    configureTimes(intLatestHour, intLatestMinute, 45);
                }
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

    @SuppressWarnings("deprecation")
    private void initControls() {
        // init BottomNavigation
        OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimeTableSubAdd:
                        currentID = 0;
                        controlFields(true, true);
                        setDefaultValuesForAdd();
                        return true;
                    case R.id.navTimeTableSubEdit:
                        controlFields(true, false);
                        return true;
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("hours", "ID", currentID, "");

                        currentID = 0;
                        navigation.getMenu().getItem(1).setEnabled(false);
                        navigation.getMenu().getItem(2).setEnabled(false);
                        controlFields(false, true);
                        reloadHours();
                        return true;
                    case R.id.navTimeTableSubSave:
                        Hour hour = new Hour();
                        hour.setID(currentID);
                        DecimalFormat decimalFormat = new DecimalFormat("00");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour.setStart(decimalFormat.format(tpHoursStart.getHour()) + ":" + decimalFormat.format(tpHoursStart.getMinute()));
                            hour.setEnd(decimalFormat.format(tpHoursEnd.getHour()) + ":" + decimalFormat.format(tpHoursEnd.getMinute()));
                        } else {
                            hour.setStart(decimalFormat.format(tpHoursStart.getCurrentHour()) + ":" + decimalFormat.format(tpHoursStart.getCurrentMinute()));
                            hour.setEnd(decimalFormat.format(tpHoursEnd.getCurrentHour()) + ":" + decimalFormat.format(tpHoursEnd.getCurrentMinute()));
                        }
                        hour.setBreak(chkHoursBreak.isChecked());
                        if(checkHourIsValid(hour)) {
                            MainActivity.globals.getSqLite().insertOrUpdateHour(hour);

                            currentID = 0;
                            navigation.getMenu().getItem(1).setEnabled(false);
                            navigation.getMenu().getItem(2).setEnabled(false);
                            controlFields(false, false);
                            reloadHours();
                        } else {
                            Helper.createToast(getApplicationContext(), getString(R.string.message_validator_times));
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
        this.lvHours = this.findViewById(R.id.lvHours);
        this.tpHoursStart = this.findViewById(R.id.tpHoursStart);
        this.tpHoursEnd = this.findViewById(R.id.tpHoursEnd);
        this.chkHoursBreak = this.findViewById(R.id.chkHoursBreak);

        this.hourAdapter = new HourAdapter(TimeTableHourActivity.this, R.layout.timetable_hour_item, new ArrayList<Hour>());
        this.lvHours.setAdapter(this.hourAdapter);
        this.hourAdapter.notifyDataSetChanged();

        this.reloadHours();
        this.controlFields(false, true);
        navigation.getMenu().getItem(1).setEnabled(false);
        navigation.getMenu().getItem(2).setEnabled(false);
    }

    @SuppressWarnings("deprecation")
    private void setDefaultValuesForAdd() {
        Hour latestHour = null;
        for(int i = 0; i<=this.hourAdapter.getCount()-1; i++) {
            Hour hour = this.hourAdapter.getItem(i);
            if(hour!=null) {
                if(latestHour!=null) {
                    String[] splLatestHour = latestHour.getEnd().split(":");
                    String[] splHour = hour.getEnd().split(":");
                    int intLatestHour = Integer.parseInt(splLatestHour[0].trim());
                    int intLatestMinute = Integer.parseInt(splLatestHour[1].trim());
                    int intHour = Integer.parseInt(splHour[0].trim());
                    int intMinute = Integer.parseInt(splHour[1].trim());

                    if(intLatestHour<intHour) {
                        latestHour = hour;
                    } else if(intLatestHour==intHour && intLatestMinute<intMinute) {
                        latestHour = hour;
                    }
                } else {
                    latestHour = hour;
                }
            }
        }

        if(latestHour!=null) {
            String[] splLatestHour = latestHour.getEnd().trim().split(":");
            this.intLatestHour = Integer.parseInt(splLatestHour[0].trim());
            this.intLatestMinute = Integer.parseInt(splLatestHour[1].trim());
            this.configureTimes(this.intLatestHour, this.intLatestMinute, 45);
        }
    }

    @SuppressWarnings("deprecation")
    private void controlFields(boolean editMode, boolean reset) {
        this.tpHoursStart.setEnabled(editMode);
        this.tpHoursEnd.setEnabled(editMode);
        this.chkHoursBreak.setEnabled(editMode);
        this.lvHours.setEnabled(!editMode);

        Helper.showMenuControls(editMode, navigation);

        if(reset) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.tpHoursStart.setHour(Calendar.getInstance().get(Calendar.HOUR));
                this.tpHoursStart.setMinute(Calendar.getInstance().get(Calendar.MINUTE));
                this.tpHoursEnd.setHour(this.tpHoursStart.getHour());
                this.tpHoursEnd.setMinute(this.tpHoursStart.getMinute());
            } else {
                this.tpHoursStart.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR));
                this.tpHoursStart.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
                this.tpHoursEnd.setCurrentHour(this.tpHoursStart.getCurrentHour());
                this.tpHoursEnd.setCurrentMinute(this.tpHoursStart.getCurrentMinute());
            }
            this.chkHoursBreak.setChecked(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void configureTimes(int hour, int minutes, int timeSpan) {
        int intLatestEndMinute = minutes + timeSpan;
        int intLatestEndHour = hour;
        if(intLatestEndMinute>=60) {
            intLatestEndHour++;
            intLatestEndMinute = intLatestEndMinute % 60;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.tpHoursStart.setHour(hour);
            this.tpHoursStart.setMinute(minutes);
            this.tpHoursEnd.setHour(intLatestEndHour);
            this.tpHoursEnd.setMinute(intLatestEndMinute);
        } else {
            this.tpHoursStart.setCurrentHour(hour);
            this.tpHoursStart.setCurrentMinute(minutes);
            this.tpHoursEnd.setCurrentHour(intLatestEndHour);
            this.tpHoursEnd.setCurrentMinute(intLatestEndMinute);
        }
    }

    private void reloadHours() {
        this.hourAdapter.clear();
        for(Hour hour : MainActivity.globals.getSqLite().getHours("")) {
            this.hourAdapter.add(hour);
        }
    }

    private boolean checkHourIsValid(Hour hour) {
        Time start = Converter.convertStringToTime(this.getApplicationContext(), hour.getStart());
        Time end = Converter.convertStringToTime(this.getApplicationContext(), hour.getEnd());
        if(start==null || end==null) {
            return false;
        }

        for(Hour currentHour : MainActivity.globals.getSqLite().getHours("")) {
            Time currentStart = Converter.convertStringToTime(this.getApplicationContext(), currentHour.getStart());
            Time currentEnd = Converter.convertStringToTime(this.getApplicationContext(), currentHour.getEnd());

            if(start.before(currentStart)) {
                if(end.after(currentStart)) {
                    return false;
                }
            }
            if(end.after(currentEnd)) {
                if(start.before(currentEnd)) {
                    return false;
                }
            }
        }

        return true;
    }
}
