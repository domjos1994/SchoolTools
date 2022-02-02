/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.TimeTableEntryActivity;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.customwidgets.model.ScreenWidget;

public final class SavedTimeTablesScreenWidget extends ScreenWidget {
    private Spinner cmbSavedTimeTables;
    private TableLayout grdSavedTimeTables;
    private ArrayAdapter<String> savedTimeTablesAdapter;

    public SavedTimeTablesScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public void init() {
        this.cmbSavedTimeTables = super.view.findViewById(R.id.cmbSavedTimeTables);
        this.savedTimeTablesAdapter = new ArrayAdapter<>(super.activity, R.layout.spinner_item, new ArrayList<>());
        this.cmbSavedTimeTables.setAdapter(this.savedTimeTablesAdapter);
        this.savedTimeTablesAdapter.notifyDataSetChanged();
        this.grdSavedTimeTables = super.view.findViewById(R.id.grdSavedTimeTables);
    }

    public void getSavedValues(String timeTable) {
        if(timeTable!=null) {
            for(int i = 0; i<=savedTimeTablesAdapter.getCount()-1; i++) {
                if(savedTimeTablesAdapter.getItem(i)!=null) {
                    String currentTimeTable = this.savedTimeTablesAdapter.getItem(i);
                    if(currentTimeTable!=null) {
                        if(currentTimeTable.equals(timeTable)) {
                            this.cmbSavedTimeTables.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void initSavedTimeTables() {
        this.initTimes(this.grdSavedTimeTables);
        List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("");
        this.savedTimeTablesAdapter.add(new TimeTable().toString());
        for(TimeTable timeTable : timeTables) {
            this.savedTimeTablesAdapter.add(timeTable.toString());
        }
        this.cmbSavedTimeTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String title = savedTimeTablesAdapter.getItem(position);
                MainActivity.globals.getGeneralSettings().setWidgetTimetableSpinner(title);

                List<TimeTable> tables = MainActivity.globals.getSqLite().getTimeTables("title='" + title + "'");

                if(tables!=null) {
                    if(!tables.isEmpty()) {
                        initTimes(grdSavedTimeTables);
                        TimeTableEntryActivity.loadTimeTable(tables.get(0), grdSavedTimeTables, new LinkedHashMap<>());
                    } else {
                        for(int i = 0; i<=grdSavedTimeTables.getChildCount()-1; i++) {
                            TableRow tableRow = (TableRow) grdSavedTimeTables.getChildAt(i);
                            for(int j = 0; j<=tableRow.getChildCount()-1; j++) {
                                TextView textView = (TextView) tableRow.getChildAt(j);
                                textView.setText("");
                            }
                        }
                    }
                } else {
                    for(int i = 0; i<=grdSavedTimeTables.getChildCount()-1; i++) {
                        TableRow tableRow = (TableRow) grdSavedTimeTables.getChildAt(i);
                        for(int j = 0; j<=tableRow.getChildCount()-1; j++) {
                            TextView textView = (TextView) tableRow.getChildAt(j);
                            textView.setText("");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initTimes(TableLayout grid) {
        Map<Double, Hour> times = new TreeMap<>();
        List<Hour> hours = MainActivity.globals.getSqLite().getHours("");
        for(Hour hour : hours) {
            times.put(Double.parseDouble(hour.getStart().replace(":", ".")), hour);
        }

        List hourList = Arrays.asList(times.values().toArray());
        int max = hourList.size()-1;
        for(int i = 1; i<=grid.getChildCount()-1; i++) {
            TableRow row = (TableRow) grid.getChildAt(i);
            TextView textView = (TextView) row.getChildAt(0);
            if((i-1)<=max) {
                Hour hour = (Hour) hourList.get(i-1);
                textView.setText(String.format("%s%n%s", hour.getStart(), hour.getEnd()));
                textView.setTag(String.valueOf(hour.getId()));

                if(hour.isBreak()) {
                    textView.setTextSize(14);
                    textView.setText(textView.getText().toString().replace("\n", ":"));
                    for(int j = 1; j<=row.getChildCount()-1; j++) {
                        row.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                        row.setBackgroundResource(R.drawable.tbl_border);
                    }
                }
            } else {
                grid.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }
}
