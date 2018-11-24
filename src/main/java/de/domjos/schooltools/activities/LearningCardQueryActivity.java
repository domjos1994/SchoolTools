/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardQueryAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class LearningCardQueryActivity extends AppCompatActivity {
    private Validator validator;
    private LearningCardQuery learningCardQuery;
    private BottomNavigationView navigation;

    private ListView lvLearningCardQueries;
    private EditText txtLearningCardQueryTitle, txtLearningCardQueryDescription, txtLearningCardQueryPeriod, txtLearningCardQueryTries;
    private TextView lblLearningCardQueryPriority;
    private Spinner spLearningCardQueryCategory, spLearningCardQueryGroup, spLearningCardQueryWrong;
    private SeekBar sbLearningCardQueryPriority;
    private CheckBox chkLearningCardQueryUntilDeadline, chkLearningCardQueryShowNotes, chkLearningCardQueryShowNotesImmediately, chkLearningCardQueryMustEqual;

    private ArrayAdapter<LearningCardGroup> groupAdapter;
    private ArrayAdapter<LearningCardQuery> queryAdapter;
    private ArrayAdapter<String> categoryAdapter;
    private LearningCardQueryAdapter learningCardQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_card_query_activity);
        this.initControls();
        this.initValidation();

        this.lvLearningCardQueries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                learningCardQuery = learningCardQueryAdapter.getItem(position);
                loadFields();
                controlElements(false, false);
            }
        });
    }


    private void initControls() {
        this.navigation = this.findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navTimeTableSubAdd:
                        learningCardQuery = null;
                        controlElements(true, true);
                        break;
                    case R.id.navTimeTableSubEdit:
                        controlElements(true, false);
                        break;
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("learningCardQueries", "ID=" + learningCardQuery.getID());
                        learningCardQuery = null;
                        controlElements(false, true);
                        reloadList();
                        break;
                    case R.id.navTimeTableSubCancel:
                        learningCardQuery = null;
                        controlElements(false, true);
                        break;
                    case R.id.navTimeTableSubSave:
                        if(validator.getState()) {
                            getFields();
                            MainActivity.globals.getSqLite().insertOrUpdateLearningCardQuery(learningCardQuery);
                            learningCardQuery = null;
                            controlElements(false, true);
                            reloadList();
                        }
                        break;
                }
                return false;
            }
        };
        this.navigation.setOnNavigationItemSelectedListener(listener);

        this.lvLearningCardQueries = this.findViewById(R.id.lvLearningCardQueries);
        this.learningCardQueryAdapter = new LearningCardQueryAdapter(this.getApplicationContext(), R.layout.learning_card_query_item, new ArrayList<LearningCardQuery>());
        this.lvLearningCardQueries.setAdapter(this.learningCardQueryAdapter);
        this.learningCardQueryAdapter.notifyDataSetChanged();

        this.txtLearningCardQueryTitle = this.findViewById(R.id.txtLearningCardQueryTitle);
        this.txtLearningCardQueryDescription = this.findViewById(R.id.txtLearningCardQueryDescription);
        this.txtLearningCardQueryPeriod = this.findViewById(R.id.txtLearningCardQueryPeriod);
        this.txtLearningCardQueryTries = this.findViewById(R.id.txtLearningCardQueryTries);
        this.lblLearningCardQueryPriority = this.findViewById(R.id.lblLearningCardQueryPriority);

        this.spLearningCardQueryCategory = this.findViewById(R.id.spLearningCardQueryCategory);
        List<String> categories = MainActivity.globals.getSqLite().getColumns("learningCards", "category", "GROUP BY category");
        this.categoryAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        this.spLearningCardQueryCategory.setAdapter(this.categoryAdapter);
        this.categoryAdapter.notifyDataSetChanged();

        this.spLearningCardQueryGroup = this.findViewById(R.id.spLearningCardQueryGroup);
        List<LearningCardGroup> groups = MainActivity.globals.getSqLite().getLearningCardGroups("", false);
        groups.add(0, new LearningCardGroup());
        this.groupAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, groups);
        this.spLearningCardQueryGroup.setAdapter(this.groupAdapter);
        this.groupAdapter.notifyDataSetChanged();

        this.spLearningCardQueryWrong = this.findViewById(R.id.spLearningCardQueryWrong);
        this.queryAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new LinkedList<LearningCardQuery>());
        this.spLearningCardQueryWrong.setAdapter(this.queryAdapter);
        this.queryAdapter.notifyDataSetChanged();
        this.reloadList();

        this.sbLearningCardQueryPriority = this.findViewById(R.id.sbLearningCardQueryPriority);

        this.chkLearningCardQueryUntilDeadline = this.findViewById(R.id.chkLearningCardQueryUntilDeadline);
        this.chkLearningCardQueryShowNotes = this.findViewById(R.id.chkLearningCardQueryShowNotes);
        this.chkLearningCardQueryShowNotesImmediately = this.findViewById(R.id.chkLearningCardQueryShowNotesImmediately);
        this.chkLearningCardQueryMustEqual = this.findViewById(R.id.chkLearningCardQueryMustEqual);

        this.sbLearningCardQueryPriority.setOnSeekBarChangeListener(Helper.getChangeListener(lblLearningCardQueryPriority));
        this.sbLearningCardQueryPriority.setProgress(0);

        this.controlElements(false, true);
    }

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addEmptyValidator(this.txtLearningCardQueryTitle);
    }

    private void controlElements(boolean editMode, boolean reset) {
        this.navigation.getMenu().findItem(R.id.navTimeTableSubSave).setEnabled(editMode);
        this.navigation.getMenu().findItem(R.id.navTimeTableSubCancel).setEnabled(editMode);
        this.navigation.getMenu().findItem(R.id.navTimeTableSubAdd).setEnabled(!editMode);
        this.navigation.getMenu().findItem(R.id.navTimeTableSubEdit).setEnabled(!editMode && this.learningCardQuery!=null);
        this.navigation.getMenu().findItem(R.id.navTimeTableSubDelete).setEnabled(!editMode && this.learningCardQuery!=null);

        this.lvLearningCardQueries.setEnabled(!editMode);
        this.txtLearningCardQueryTitle.setEnabled(editMode);
        this.txtLearningCardQueryDescription.setEnabled(editMode);
        this.txtLearningCardQueryPeriod.setEnabled(editMode);
        this.txtLearningCardQueryTries.setEnabled(editMode);
        this.spLearningCardQueryCategory.setEnabled(editMode);
        this.spLearningCardQueryGroup.setEnabled(editMode);
        this.spLearningCardQueryWrong.setEnabled(editMode);
        this.sbLearningCardQueryPriority.setEnabled(editMode);
        this.chkLearningCardQueryUntilDeadline.setEnabled(editMode);
        this.chkLearningCardQueryShowNotes.setEnabled(editMode);
        this.chkLearningCardQueryShowNotesImmediately.setEnabled(editMode);
        this.chkLearningCardQueryMustEqual.setEnabled(editMode);

        if(reset) {
            this.txtLearningCardQueryTitle.setText("");
            this.txtLearningCardQueryDescription.setText("");
            this.txtLearningCardQueryTries.setText("1");
            this.txtLearningCardQueryPeriod.setText("0");

            this.sbLearningCardQueryPriority.setProgress(0);
            this.spLearningCardQueryCategory.setSelected(false);
            this.spLearningCardQueryWrong.setSelected(false);
            this.spLearningCardQueryGroup.setSelected(false);

            this.chkLearningCardQueryShowNotesImmediately.setChecked(false);
            this.chkLearningCardQueryShowNotes.setChecked(false);
            this.chkLearningCardQueryUntilDeadline.setChecked(false);
            this.chkLearningCardQueryMustEqual.setChecked(false);
        }
    }

    private void loadFields() {
        if(this.learningCardQuery!=null) {
            this.txtLearningCardQueryTitle.setText(this.learningCardQuery.getTitle());
            this.txtLearningCardQueryDescription.setText(this.learningCardQuery.getDescription());
            this.txtLearningCardQueryTries.setText(String.valueOf(this.learningCardQuery.getTries()));
            if(this.learningCardQuery.isPeriodic()) {
                this.txtLearningCardQueryPeriod.setText(String.valueOf(this.learningCardQuery.getPeriod()));
            } else {
                this.txtLearningCardQueryPeriod.setText("0");
            }
            this.sbLearningCardQueryPriority.setProgress(this.learningCardQuery.getPriority());

            LearningCardGroup group = this.learningCardQuery.getLearningCardGroup();
            if(group!=null) {
               this.spLearningCardQueryGroup.setSelection(this.groupAdapter.getPosition(group));
            } else {
                this.spLearningCardQueryGroup.setSelected(false);
            }

            LearningCardQuery query = this.learningCardQuery.getWrongCardsOfQuery();
            if(query!=null) {
                this.spLearningCardQueryWrong.setSelection(this.queryAdapter.getPosition(query));
            } else {
                this.spLearningCardQueryWrong.setSelected(false);
            }

            String category = this.learningCardQuery.getCategory();
            if(category!=null) {
                if(!category.trim().equals("")) {
                    this.spLearningCardQueryCategory.setSelection(this.categoryAdapter.getPosition(category));
                } else {
                    this.spLearningCardQueryCategory.setSelected(false);
                }
            } else {
                this.spLearningCardQueryCategory.setSelected(false);
            }

            this.chkLearningCardQueryMustEqual.setChecked(this.learningCardQuery.isAnswerMustEqual());
            this.chkLearningCardQueryUntilDeadline.setChecked(this.learningCardQuery.isUntilDeadLine());
            this.chkLearningCardQueryShowNotes.setChecked(this.learningCardQuery.isShowNotes());
            this.chkLearningCardQueryShowNotesImmediately.setChecked(this.learningCardQuery.isShowNotesImmediately());
        }
    }

    private void getFields() {
        if(this.learningCardQuery==null) {
            this.learningCardQuery = new LearningCardQuery();
        }
        this.learningCardQuery.setTitle(this.txtLearningCardQueryTitle.getText().toString());
        this.learningCardQuery.setDescription(this.txtLearningCardQueryDescription.getText().toString());
        this.learningCardQuery.setTries(Integer.parseInt(this.txtLearningCardQueryTries.getText().toString()));
        this.learningCardQuery.setPeriod(Integer.parseInt(this.txtLearningCardQueryPeriod.getText().toString()));
        this.learningCardQuery.setPeriodic(this.txtLearningCardQueryPeriod.getText().toString().equals("0"));

        if(this.spLearningCardQueryCategory.getSelectedItem()!=null) {
            this.learningCardQuery.setCategory(this.spLearningCardQueryCategory.getSelectedItem().toString());
        }
        if(this.spLearningCardQueryWrong.getSelectedItem()!=null) {
            this.learningCardQuery.setWrongCardsOfQuery(this.queryAdapter.getItem(this.spLearningCardQueryWrong.getSelectedItemPosition()));
        }
        if(this.spLearningCardQueryGroup.getSelectedItem()!=null) {
            this.learningCardQuery.setLearningCardGroup(this.groupAdapter.getItem(this.spLearningCardQueryGroup.getSelectedItemPosition()));
        }
        this.learningCardQuery.setPriority(this.sbLearningCardQueryPriority.getProgress());

        this.learningCardQuery.setAnswerMustEqual(this.chkLearningCardQueryMustEqual.isChecked());
        this.learningCardQuery.setUntilDeadLine(this.chkLearningCardQueryUntilDeadline.isChecked());
        this.learningCardQuery.setShowNotes(this.chkLearningCardQueryShowNotes.isChecked());
        this.learningCardQuery.setShowNotesImmediately(this.chkLearningCardQueryShowNotesImmediately.isChecked());
    }

    private void reloadList() {
        this.learningCardQueryAdapter.clear();
        this.queryAdapter.clear();
        this.queryAdapter.add(new LearningCardQuery());
        List<LearningCardQuery> learningCardQueries = MainActivity.globals.getSqLite().getLearningCardQueries("");
        for(LearningCardQuery learningCardQuery : learningCardQueries) {
            this.learningCardQueryAdapter.add(learningCardQuery);
            this.queryAdapter.add(learningCardQuery);
        }
    }
}
