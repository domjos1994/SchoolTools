/*
 * Copyright (C) 2017-2022  Dominic Joas
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
import android.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.AssistantHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.widgets.TimeTableWidget;

/**
 * Activity For the TimeTable-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableActivity extends AbstractActivity {
    private FloatingActionButton cmdTimeTableAdd;
    private SwipeRefreshDeleteList lvTimeTable;
    private AppCompatImageButton cmdTimeTableAssistant;

    public TimeTableActivity() {
        super(R.layout.timetable_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.reloadTimeTables();
        this.openDescription();

        this.cmdTimeTableAdd.setOnClickListener(v -> {
            if(MainActivity.globals.getUserSettings().useAssistant()) {
                AssistantHelper assistantHelper = new AssistantHelper(TimeTableActivity.this);
                assistantHelper.showTimeTableAssistant(this::reloadTimeTables);
            } else {
                boolean hours = MainActivity.globals.getSqLite().getHours("").isEmpty();
                boolean teachers = MainActivity.globals.getSqLite().getTeachers("").isEmpty();
                boolean subjects = MainActivity.globals.getSqLite().getSubjects("").isEmpty();
                boolean years = MainActivity.globals.getSqLite().getYears("").isEmpty();
                boolean schoolClasses = MainActivity.globals.getSqLite().getClasses("").isEmpty();

                String missingFields = "";
                if (hours) {
                    missingFields += getString(R.string.timetable_hour) + ", ";
                }
                if (teachers) {
                    missingFields += getString(R.string.timetable_teacher) + ", ";
                }
                if (subjects) {
                    missingFields += getString(R.string.timetable_lesson) + ", ";
                }
                if (years) {
                    missingFields += getString(R.string.timetable_year) + ", ";
                }
                if (schoolClasses) {
                    missingFields += getString(R.string.timetable_class) + ", ";
                }
                if (!missingFields.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableActivity.this);
                    builder.setTitle(R.string.message_timetable_warning_header);
                    builder.setMessage(String.format(getString(R.string.message_timetable_warning_content), (missingFields + ")").replace(", )", "")));
                    builder.setPositiveButton(R.string.message_marklist_important_message_accept, (dialog, which) -> {
                        Intent intent = new Intent(getApplicationContext(), TimeTableEntryActivity.class);
                        intent.putExtra("id", 0);
                        startActivityForResult(intent, 99);
                    });
                    builder.setNegativeButton(R.string.sys_cancel, null);
                    builder.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TimeTableEntryActivity.class);
                    intent.putExtra("id", 0);
                    startActivityForResult(intent, 99);
                }
            }
        });

        this.cmdTimeTableAssistant.setOnClickListener(v -> {
            AssistantHelper assistantHelper = new AssistantHelper(TimeTableActivity.this);
            assistantHelper.showTimeTableAssistant(this::reloadTimeTables);
        });

        this.lvTimeTable.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Intent intent = new Intent(getApplicationContext(), TimeTableEntryActivity.class);
            intent.putExtra("id", listObject.getId());
            startActivityForResult(intent, 99);
        });

        this.lvTimeTable.setOnDeleteListener(listObject -> {
            MainActivity.globals.getSqLite().deleteEntry("plans", "ID", listObject.getId(), "");
            MainActivity.globals.getSqLite().deleteEntry("timeTable", "plan", listObject.getId(), "");
            reloadTimeTables();
        });

        this.lvTimeTable.setOnReloadListener(this::reloadTimeTables);
    }

    private void openDescription() {
        Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
        intent.putExtra(WhatsNewActivity.PARENT_CLASS, TimeTable.class.getName());
        intent.putExtra(WhatsNewActivity.TITLE_PARAM, "main_nav_timetable");
        intent.putExtra(WhatsNewActivity.CONTENT_PARAM, "timetable_content");
        intent.putExtra(WhatsNewActivity.INFO_PARAM, "");
        intent.putExtra(WhatsNewActivity.isWhatsNew, false);
        intent.putExtra(WhatsNewActivity.isShownAlways, false);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_timetable"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            if(resultCode==RESULT_OK) {
                if(requestCode==99) {
                    this.reloadTimeTables();
                    Helper.sendBroadCast(TimeTableActivity.this, TimeTableWidget.class);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimeTableActivity.this);
        }
    }

    @Override
    protected void initControls() {
        try {
            // init BottomNavigation
            BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimeTableLesson:
                        Intent intent = new Intent(getApplicationContext(), TimeTableSubjectActivity.class);
                        intent.putExtra("parent", R.layout.timetable_activity);
                        startActivity(intent);
                        return true;
                    case R.id.navTimeTableClass:
                        startActivity(new Intent(getApplicationContext(), TimeTableClassActivity.class));
                        break;
                    case R.id.navTimeTableTeacher:
                        startActivity(new Intent(getApplicationContext(), TimeTableTeacherActivity.class));
                        return true;
                    case R.id.navTimeTableYear:
                        Intent tmp = new Intent(getApplicationContext(), MarkYearActivity.class);
                        tmp.putExtra("parent", R.layout.timetable_activity);
                        startActivity(tmp);
                        return true;
                    case R.id.navTimeTableTimes:
                        startActivity(new Intent(getApplicationContext(), TimeTableHourActivity.class));
                        return true;
                }
                return false;
            };
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(navListener);
            navigation.setSelected(false);

            // init other controls
            this.cmdTimeTableAdd = this.findViewById(R.id.cmdTimeTableAdd);
            this.cmdTimeTableAssistant = this.findViewById(R.id.cmdTimeTableAssistant);
            this.cmdTimeTableAssistant.setVisibility(MainActivity.globals.getUserSettings().useAssistant() ? View.GONE : View.VISIBLE);

            this.lvTimeTable = this.findViewById(R.id.lvTimeTable);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimeTableActivity.this);
        }
    }

    private void reloadTimeTables() {
        try {
            this.lvTimeTable.getAdapter().clear();
            List<TimeTable> timeTableList = MainActivity.globals.getSqLite().getTimeTables("");
            for(TimeTable timeTable : timeTableList) {
                this.lvTimeTable.getAdapter().add(timeTable);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TimeTableActivity.this);
        }
    }
}
