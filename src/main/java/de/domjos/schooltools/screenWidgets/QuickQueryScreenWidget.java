package de.domjos.schooltools.screenWidgets;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.custom.ScreenWidget;

public final class QuickQueryScreenWidget extends ScreenWidget {
    private TextView lblQuestion, lblResult;
    private EditText txtAnswer;
    private Button cmdResult;
    private ArrayAdapter<String> queryAdapter;
    private LearningCardQuery query = null;
    private List<LearningCard> cards;
    private int current = 0, wrong, write;

    public QuickQueryScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public void init() {
        Spinner cmbQueries = super.view.findViewById(R.id.cmbQueries);
        this.queryAdapter = new ArrayAdapter<>(this.activity.getApplicationContext(), R.layout.spinner_item);
        cmbQueries.setAdapter(this.queryAdapter);
        this.queryAdapter.notifyDataSetChanged();

        this.lblQuestion = super.view.findViewById(R.id.lblQuestion);
        this.lblResult = super.view.findViewById(R.id.lblResult);
        this.txtAnswer = super.view.findViewById(R.id.txtAnswer);
        this.cmdResult = super.view.findViewById(R.id.cmdResult);

        cmbQueries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = queryAdapter.getItem(position);
                if(item!=null) {
                    if(!item.trim().equals("")) {
                        query = MainActivity.globals.getSqLite().getLearningCardQueries("title='" + item +"'").get(0);
                        reloadCurrent();
                        return;
                    }
                }
                query = null;
                reloadCurrent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.cmdResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cmdResult.getText().toString().equals(activity.getString(R.string.learningCard_result))) {
                    if(checkAnswer(cards.get(current), txtAnswer.getText().toString())) {
                        write++;
                        fillColor(R.color.Green);
                        lblResult.setText(activity.getString(R.string.learningCard_query_answer_right));
                    } else {
                        wrong++;
                        fillColor(R.color.Red);
                        lblResult.setText(activity.getString(R.string.learningCard_query_answer_wrong));
                    }
                    cmdResult.setText(">>");
                } else {
                    reloadCurrent();
                    cmdResult.setText(activity.getString(R.string.learningCard_result));
                }
            }
        });
    }

    private void fillColor(Integer color) {
        if(color!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lblResult.setBackgroundColor(super.activity.getColor(color));
            } else {
                lblResult.setBackgroundColor(super.activity.getResources().getColor(color));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lblResult.setBackgroundColor(super.activity.getColor(android.R.color.transparent));
            } else {
                lblResult.setBackgroundColor(super.activity.getResources().getColor(android.R.color.transparent));
            }
        }
    }

    private boolean checkAnswer(LearningCard card, String answer) {
        if(this.query.isAnswerMustEqual()) {
            return card.getAnswer().equals(answer);
        } else {
            return card.getAnswer().trim().toLowerCase().contains(answer);
        }
    }

    public void reloadQueries() {
        this.queryAdapter.clear();
        this.queryAdapter.add("");
        for(LearningCardQuery query : MainActivity.globals.getSqLite().getLearningCardQueries("")) {
            this.queryAdapter.add(query.getTitle());
        }
    }

    private void reloadCurrent() {
        if(this.query == null) {
            lblResult.setText("");
            txtAnswer.setText("");
            lblQuestion.setText("");
            cmdResult.setText(R.string.learningCard_result);
            this.current = 0;
            this.cards = null;
            fillColor(null);
        } else {
            if(this.cards==null) {
                this.current = 0;
                this.cards = this.query.loadLearningCards(this.activity.getApplicationContext());
                Collections.shuffle(this.cards);
            } else {
                this.current++;
            }
            if(this.cards.size()-1>=this.current) {
                lblQuestion.setText(this.cards.get(current).getQuestion());
                txtAnswer.setText("");
            } else {
                String result = write + " : " + wrong;
                lblResult.setText(result);
                fillColor(null);
            }
        }
    }
}
