/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.learningCard.LearningCard;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQuery;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQueryResult;
import de.domjos.schooltoolslib.model.learningCard.LearningCardQueryTraining;

public class LearningCardFragment extends Fragment {
    private LearningCardQueryResult learningCardQueryResult;
    private LearningCardQueryTraining learningCardQueryTraining;
    private LearningCardQuery learningCardQuery;
    private LearningCard learningCard;
    private Context context;
    private int currentTry = 0;

    private TextView lblLearningCardTitle, lblLearningCardQuestion, lblLearningCardNote1, lblLearningCardNote2,
                lblLearningCardCurrentTry, lblLearningCardMaxTries, lblLearningCardResult;
    private EditText txtLearningCardAnswer;
    private Button cmdCheckAnswer;

    public LearningCardFragment() {
        this.learningCardQueryResult = new LearningCardQueryResult();
    }

    public void setLearningCard(LearningCard learningCard) {
        this.learningCard = learningCard;
    }

    public void setLearningCardQueryTraining(LearningCardQueryTraining learningCardQueryTraining) {
        this.learningCardQueryTraining = learningCardQueryTraining;
        this.learningCardQuery = this.learningCardQueryTraining.getLearningCardQuery();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.learning_card_fragment, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.lblLearningCardTitle = view.findViewById(R.id.lblLearningCardTitle);
        this.lblLearningCardQuestion = view.findViewById(R.id.lblLearningCardQuestion);
        this.txtLearningCardAnswer = view.findViewById(R.id.txtLearningCardAnswer);
        this.cmdCheckAnswer = view.findViewById(R.id.cmdCheckAnswer);
        this.lblLearningCardNote1 = view.findViewById(R.id.lblLearningCardNote1);
        this.lblLearningCardNote2 = view.findViewById(R.id.lblLearningCardNote2);
        this.lblLearningCardCurrentTry = view.findViewById(R.id.lblLearningCardCurrentTry);
        this.lblLearningCardMaxTries = view.findViewById(R.id.lblLearningCardMaxTries);
        this.lblLearningCardResult = view.findViewById(R.id.lblLearningCardResult);

        this.setValues();
    }

    private boolean checkAnswer() {
        if(this.txtLearningCardAnswer!=null && this.learningCardQuery!=null) {
            if(this.learningCardQuery.isAnswerMustEqual()) {
                return this.learningCard.getAnswer().equals(this.txtLearningCardAnswer.getText().toString());
            } else {
                return this.learningCard.getAnswer().contains(this.txtLearningCardAnswer.getText().toString());
            }
        }
        return false;
    }

    private void setValues() {
        if(this.learningCard!=null && this.learningCardQuery!=null) {
            this.lblLearningCardTitle.setText(this.learningCard.getTitle());
            this.lblLearningCardQuestion.setText(this.learningCard.getQuestion());
            if(this.learningCardQuery.isShowNotesImmediately()) {
                this.lblLearningCardNote1.setText(this.learningCard.getNote1());
                this.lblLearningCardNote2.setText(this.learningCard.getNote2());
            }
            this.lblLearningCardCurrentTry.setText("0");
            this.lblLearningCardMaxTries.setText(String.valueOf(learningCardQuery.getTries()));

            this.cmdCheckAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result = checkAnswer();
                    learningCardQueryResult.setLearningCard(learningCard);
                    learningCardQueryResult.setTraining(learningCardQueryTraining);

                    if(currentTry<=learningCardQuery.getTries()) {
                        if(currentTry==0) {
                            learningCardQueryResult.setResult1(result);
                            learningCardQueryResult.setTry1(txtLearningCardAnswer.getText().toString());
                        }
                        if(currentTry==1) {
                            learningCardQueryResult.setResult2(result);
                            learningCardQueryResult.setTry2(txtLearningCardAnswer.getText().toString());
                        }
                        if(currentTry==2) {
                            learningCardQueryResult.setResult3(result);
                            learningCardQueryResult.setTry3(txtLearningCardAnswer.getText().toString());
                        }
                    }

                    if(result) {
                        lblLearningCardResult.setText(context.getString(R.string.learningCard_query_answer_right));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            lblLearningCardResult.setBackgroundColor(context.getColor(R.color.Green));
                        } else {
                            lblLearningCardResult.setBackgroundColor(getResources().getColor(R.color.Green));
                        }
                    } else {
                        lblLearningCardResult.setText(context.getString(R.string.learningCard_query_answer_wrong));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            lblLearningCardResult.setBackgroundColor(context.getColor(R.color.Red));
                        } else {
                            lblLearningCardResult.setBackgroundColor(getResources().getColor(R.color.Red));
                        }
                    }
                    lblLearningCardCurrentTry.setText(String.valueOf(++currentTry));
                    if(currentTry==learningCardQuery.getTries() || result) {
                        cmdCheckAnswer.setEnabled(false);
                        txtLearningCardAnswer.setEnabled(false);
                    } else {
                        txtLearningCardAnswer.setText("");
                    }
                    learningCardQueryResult.setID(MainActivity.globals.getSqLite().insertOrUpdateLearningCardResult(learningCardQueryResult));
                }
            });
        }
    }
}
