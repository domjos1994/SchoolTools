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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Calendar;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.objects.BaseDescriptionObject;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.custom.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Hour-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableHourActivity extends AbstractActivity {
    private BottomNavigationView navigation;
    private SwipeRefreshDeleteList lvHours;
    private TimePicker tpHoursStart, tpHoursEnd;
    private CheckBox chkHoursBreak;
    private int currentID;

    private int intLatestHour, intLatestMinute;

    public TimeTableHourActivity() {
        super(R.layout.timetable_hour_activity);
    }

    @Override
    protected void initActions() {
        Helper.closeSoftKeyboard(TimeTableHourActivity.this);

        this.lvHours.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                Hour hour = (Hour) listObject;
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

        this.lvHours.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadHours();
            }
        });

        this.lvHours.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("hours", "ID", listObject.getID(), "");

                currentID = 0;
                navigation.getMenu().getItem(1).setEnabled(false);
                navigation.getMenu().getItem(2).setEnabled(false);
                controlFields(false, true);
                reloadHours();
            }
        });

        this.chkHoursBreak.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                configureTimes(intLatestHour, intLatestMinute, MainActivity.globals.getUserSettings().getBreakTime());
            } else {
                configureTimes(intLatestHour, intLatestMinute, 45);
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

    @Override
    protected void initControls() {
        // init BottomNavigation
        OnNavigationItemSelectedListener navListener = (item) -> {
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
        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.lvHours = this.findViewById(R.id.lvHours);
        this.tpHoursStart = this.findViewById(R.id.tpHoursStart);
        this.tpHoursEnd = this.findViewById(R.id.tpHoursEnd);
        this.chkHoursBreak = this.findViewById(R.id.chkHoursBreak);

        this.reloadHours();
        this.controlFields(false, true);
        navigation.getMenu().getItem(1).setEnabled(false);
        navigation.getMenu().getItem(2).setEnabled(false);
    }

    private void setDefaultValuesForAdd() {
        Hour latestHour = null;
        for(int i = 0; i<=this.lvHours.getAdapter().getItemCount()-1; i++) {
            Hour hour = (Hour) this.lvHours.getAdapter().getItem(i);
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

    private void configureTimes(int hour, int minutes, int timeSpan) {
        if(this.currentID==0) {
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
    }

    private void reloadHours() {
        this.lvHours.getAdapter().clear();
        for(Hour hour : MainActivity.globals.getSqLite().getHours("")) {
            this.lvHours.getAdapter().add(hour);
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
