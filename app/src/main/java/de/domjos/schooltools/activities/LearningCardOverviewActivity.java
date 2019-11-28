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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardQueryFragmentAdapter;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQuery;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQueryResult;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQueryTraining;
import de.domjos.schooltools.helper.AssistantHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.spotlight.OnBoardingHelper;

import java.util.List;

public final class LearningCardOverviewActivity extends FragmentActivity {
    private ViewPager viewPager;
    private LearningCardQueryFragmentAdapter fragmentAdapter;
    private Button cmdLearningCardQueryStart;
    public final static String MODE = "mode", RANDOM = "random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.learning_card_overview_activity);
        this.initControls();
        Helper.setBackgroundToActivity(this);

        this.cmdLearningCardQueryStart.setOnClickListener(v -> {
            final LearningCardQueryTraining learningCardQueryTraining = new LearningCardQueryTraining();
            if(cmdLearningCardQueryStart.getText().equals(getString(R.string.learningCard_query))) {
                final Dialog dialog = new Dialog(LearningCardOverviewActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.learning_card_dialog);
                dialog.setCancelable(true);


                final Spinner spLearningCardQuery = dialog.findViewById(R.id.spLearningCardQueries);
                final ArrayAdapter<LearningCardQuery> learningCardQueries = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, MainActivity.globals.getSqLite().getLearningCardQueries(""));
                spLearningCardQuery.setAdapter(learningCardQueries);
                learningCardQueries.notifyDataSetChanged();

                final Button btnStartSop = dialog.findViewById(R.id.cmdStart);
                btnStartSop.setOnClickListener(v1 -> {
                    loadLearningCardQuery(learningCardQueries.getItem(spLearningCardQuery.getSelectedItemPosition()));
                    dialog.dismiss();
                });
                dialog.show();
            } else {
                int wrongCards = 0, rightCards = 0, firstTry = 0, secondTry = 0, thirdTry = 0;
                List<LearningCardQueryTraining> trainings = MainActivity.globals.getSqLite().getLearningCardQueryTraining("ID=" + learningCardQueryTraining.getID());
                if(this.fragmentAdapter.getTraining()!=null) {
                    if(!this.fragmentAdapter.getTraining().getResults().isEmpty()) {
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
        });
    }


    private void initControls() {
        BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
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
                    if(MainActivity.globals.getUserSettings().useAssistant()) {
                        AssistantHelper assistantHelper = new AssistantHelper(LearningCardOverviewActivity.this);
                        assistantHelper.showLearningCardAssistant();
                    } else {
                        startActivityForResult(new Intent(getApplicationContext(), LearningCardQueryActivity.class), 99);
                    }
                    break;
                case R.id.navLearningCardAssistant:
                    AssistantHelper assistantHelper = new AssistantHelper(LearningCardOverviewActivity.this);
                    assistantHelper.showLearningCardAssistant();
                    break;
            }
            return false;
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        //navigation.getMenu().findItem(R.id.navLearningCardGroups).setVisible(!MainActivity.globals.getUserSettings().useAssistant());
        //navigation.getMenu().findItem(R.id.navLearningCardQueries).setVisible(!MainActivity.globals.getUserSettings().useAssistant());
        navigation.setOnNavigationItemSelectedListener(listener);

        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.learningCard_overView);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));

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
        this.startRandomQuery();

        OnBoardingHelper.tutorialLearningCard(this, navigation, this.cmdLearningCardQueryStart);
    }

    private void startRandomQuery() {
        Intent intent = this.getIntent();
        if(intent!=null) {
            if(LearningCardOverviewActivity.RANDOM.equals(intent.getStringExtra(LearningCardOverviewActivity.MODE))) {
                LearningCardQueryTraining training = new LearningCardQueryTraining();
                LearningCardQuery learningCardQuery = new LearningCardQuery();
                learningCardQuery.setRandomVocab(true);
                learningCardQuery.setRandomVocabNumber(30);
                training.setLearningCardQuery(learningCardQuery);
                cmdLearningCardQueryStart.setText(getString(R.string.learningCard_query_end));
                fragmentAdapter.setQuery(training);
                viewPager.setAdapter(fragmentAdapter);
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
