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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;

/**
 * Activity For the Mark-Year-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class MarkYearActivity extends AbstractActivity {
    private int currentID;
    private EditText txtYearTitle, txtYearDescription;
    private SwipeRefreshDeleteList lvYear;

    private BottomNavigationView navigation;

    public MarkYearActivity() {
        super(R.layout.mark_year_activity, MainActivity.globals.getSqLite().getSetting("background"));
    }

    @Override
    protected void initActions() {
        this.reloadYears();

        this.lvYear.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                Year year = (Year) listObject;
                if(year!=null) {
                    currentID = year.getID();
                    txtYearTitle.setText(year.getTitle());
                    txtYearDescription.setText(year.getDescription());
                    controlFields(false, false, true);
                }
            }
        });

        this.lvYear.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadYears();
            }
        });

        this.lvYear.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("years", "ID", listObject.getID(), "");
                controlFields(false, true, false);
                reloadYears();
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
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        int parent = this.getIntent().getIntExtra("parent", 0);
        Intent i = null;
        switch (parent) {
            case R.layout.mark_activity:
                i = new Intent(this, MarkActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case R.layout.timetable_activity:
                i = new Intent(this, TimeTableActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            default:
        }
        return i;
    }

    private void reloadYears() {
        this.lvYear.getAdapter().clear();
        for(Year year : MainActivity.globals.getSqLite().getYears("")) {
            this.lvYear.getAdapter().add(year);
        }
    }

    private void controlFields(boolean editMode, boolean reset, boolean selected) {
        this.txtYearTitle.setEnabled(editMode);
        this.txtYearDescription.setEnabled(editMode);
        this.navigation.getMenu().getItem(0).setEnabled(!editMode);
        this.navigation.getMenu().getItem(1).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(2).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(3).setEnabled(editMode);
        this.navigation.getMenu().getItem(4).setEnabled(editMode);

        if(reset) {
            currentID = 0;
            this.txtYearTitle.setText("");
            this.txtYearDescription.setText("");
        }
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
            switch (Helper.checkMenuID(item)) {
                case R.id.navTimeTableSubAdd:
                    controlFields(true, true, false);
                    break;
                case R.id.navTimeTableSubEdit:
                    controlFields(true, false, false);
                    break;
                case R.id.navTimeTableSubDelete:
                    MainActivity.globals.getSqLite().deleteEntry("years", "ID", currentID, "");
                    controlFields(false, true, false);
                    reloadYears();
                    break;
                case R.id.navTimeTableSubCancel:
                    controlFields(false, true, false);
                    break;
                case R.id.navTimeTableSubSave:
                    Year year = new Year();
                    year.setID(currentID);
                    year.setTitle(txtYearTitle.getText().toString());
                    year.setDescription(txtYearDescription.getText().toString());
                    MainActivity.globals.getSqLite().insertOrUpdateYear(year);
                    controlFields(false, true, false);
                    reloadYears();
                    break;
                default:
            }
            return false;
        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init other controls
        this.txtYearTitle = this.findViewById(R.id.txtYearTitle);
        this.txtYearDescription = this.findViewById(R.id.txtYearDescription);
        this.lvYear = this.findViewById(R.id.lvYear);
        this.controlFields(false, true, false);
    }
}
