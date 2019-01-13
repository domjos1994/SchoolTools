/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardQueryFragmentAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.core.model.learningCard.LearningCardQueryResult;
import de.domjos.schooltools.core.model.learningCard.LearningCardQueryTraining;
import de.domjos.schooltools.helper.Helper;

import java.util.List;

public class LearningCardOverviewActivity extends FragmentActivity {
    private ViewPager viewPager;
    private LearningCardQueryFragmentAdapter fragmentAdapter;
    private Button cmdLearningCardQueryStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.learning_card_overview_activity);
        this.initControls();
        Helper.setBackgroundToActivity(this);

        this.cmdLearningCardQueryStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LearningCardQueryTraining learningCardQueryTraining = new LearningCardQueryTraining();
                if(cmdLearningCardQueryStart.getText().equals(getString(R.string.learningCard_query))) {
                    final Dialog dialog = new Dialog(LearningCardOverviewActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.learning_card_dialog);
                    dialog.setCancelable(true);


                    final Spinner spLearningCardQuery = dialog.findViewById(R.id.spLearningCardQueries);
                    final ArrayAdapter<LearningCardQuery> learningCardQueries = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, MainActivity.globals.getSqLite().getLearningCardQueries(""));
                    spLearningCardQuery.setAdapter(learningCardQueries);
                    learningCardQueries.notifyDataSetChanged();

                    final Button btnStartSop = dialog.findViewById(R.id.cmdStart);
                    btnStartSop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadLearningCardQuery(learningCardQueries.getItem(spLearningCardQuery.getSelectedItemPosition()));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    int wrongCards = 0, rightCards = 0, firstTry = 0, secondTry = 0, thirdTry = 0;
                    List<LearningCardQueryTraining> trainings = MainActivity.globals.getSqLite().getLearningCardQueryTraining("ID=" + learningCardQueryTraining.getID());
                    if(trainings!=null) {
                        if(!trainings.isEmpty()) {
                            LearningCardQueryTraining reloadedTraining = trainings.get(0);
                            for(LearningCardQueryResult result : reloadedTraining.getResults()) {
                                if(result.isResult1() || result.isResult2() || result.isResult3()) {
                                    rightCards++;
                                    if(result.isResult1()) {
                                        firstTry++;
                                    } else if(result.isResult2()) {
                                        secondTry++;
                                    } else {
                                        thirdTry++;
                                    }
                                } else {
                                    wrongCards++;
                                }
                            }
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LearningCardOverviewActivity.this);
                            alertDialogBuilder.setTitle(R.string.learningCard_result);
                            String content =
                                    String.format(
                                            "%s %s%n%s %s%n%s %s%n%s %s%n%s %s%n",
                                            getString(R.string.learningCard_result_right), rightCards,
                                            getString(R.string.learningCard_result_wrong), wrongCards,
                                            getString(R.string.learningCard_result_firstTry), firstTry,
                                            getString(R.string.learningCard_result_secondTry), secondTry,
                                            getString(R.string.learningCard_result_thirdTry), thirdTry
                                    );
                            alertDialogBuilder.setMessage(content);
                            alertDialogBuilder.create().show();
                        }
                    }

                    cmdLearningCardQueryStart.setText(getString(R.string.learningCard_query));
                    fragmentAdapter.setQuery(null);
                    viewPager.setAdapter(fragmentAdapter);
                }
            }
        });
    }


    private void initControls() {
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navLearningCardTeacher:
                        startActivity(new Intent(getApplicationContext(), TimeTableTeacherActivity.class));
                        break;
                    case R.id.navLearningCardLesson:
                        Intent intent = new Intent(getApplicationContext(), TimeTableSubjectActivity.class);
                        intent.putExtra("parent", R.layout.learning_card_overview_activity);
                        startActivity(intent);
                        break;
                    case R.id.navLearningCardGroups:
                        startActivityForResult(new Intent(getApplicationContext(), LearningCardGroupActivity.class), 99);
                        break;
                    case R.id.navLearningCardQueries:
                        startActivityForResult(new Intent(getApplicationContext(), LearningCardQueryActivity.class), 99);
                        break;
                }
                return false;
            }
        };

        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);

        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.learningCard_overView);
        toolbar.setNavigationIcon(R.drawable.home_as_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        this.cmdLearningCardQueryStart = this.findViewById(R.id.cmdLearningCardQueryStart);

        this.fragmentAdapter = new LearningCardQueryFragmentAdapter(this.getSupportFragmentManager(), this.getApplicationContext(), null);
        this.viewPager = this.findViewById(R.id.pager);
        this.viewPager.setAdapter(this.fragmentAdapter);

        int id = this.getIntent().getIntExtra("queryID", 0);
        if(id!=0) {
            List<LearningCardQuery> learningCardQueries = MainActivity.globals.getSqLite().getLearningCardQueries("ID=" + id);
            if(learningCardQueries!=null) {
                if(!learningCardQueries.isEmpty()) {
                    this.loadLearningCardQuery(learningCardQueries.get(0));
                }
            }
        }
    }

    private void loadLearningCardQuery(LearningCardQuery learningCardQuery) {
        LearningCardQueryTraining training = new LearningCardQueryTraining();
        training.setLearningCardQuery(learningCardQuery);
        training.setID(MainActivity.globals.getSqLite().insertOrUpdateLearningCardQueryTraining(training));
        cmdLearningCardQueryStart.setText(getString(R.string.learningCard_query_end));
        fragmentAdapter.setQuery(training);
        viewPager.setAdapter(fragmentAdapter);
    }
}
