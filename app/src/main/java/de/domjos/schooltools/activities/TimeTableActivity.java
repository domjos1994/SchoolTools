/*
 * Copyright (C) 2017  Dominic Joas
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.TimeTableAdapter;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the TimeTable-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class TimeTableActivity extends AppCompatActivity {
    private FloatingActionButton cmdTimeTableAdd;
    private ListView lvTimeTable;
    private TimeTableAdapter timeTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);
        this.initControls();
        this.reloadTimeTables();

        this.cmdTimeTableAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimeTableEntryActivity.class);
                intent.putExtra("id", 0);
                startActivityForResult(intent, 99);
            }
        });

        this.lvTimeTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TimeTableEntryActivity.class);
                TimeTable timeTable = timeTableAdapter.getItem(position);
                if (timeTable != null) {
                    intent.putExtra("id", timeTable.getID());
                } else {
                    intent.putExtra("id", 0);
                }
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
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        try {
            if(resultCode==RESULT_OK) {
                if(requestCode==99) {
                    reloadTimeTables();
                }
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void initControls() {
        try {
            // init BottomNavigation
            BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
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
                }
            };
            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(navListener);
            navigation.setSelected(false);
            Helper.removeShiftMode(navigation);

            // init other controls
            this.cmdTimeTableAdd = this.findViewById(R.id.cmdTimeTableAdd);

            this.timeTableAdapter = new TimeTableAdapter(this.getApplicationContext(), R.layout.timetable_item, new ArrayList<TimeTable>());
            this.lvTimeTable = this.findViewById(R.id.lvTimeTable);
            this.lvTimeTable.setAdapter(this.timeTableAdapter);
            this.timeTableAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void reloadTimeTables() {
        try {
            this.timeTableAdapter.clear();
            List<TimeTable> timeTableList = MainActivity.globals.getSqLite().getTimeTables("");
            for(TimeTable timeTable : timeTableList) {
                this.timeTableAdapter.add(timeTable);
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }
}
