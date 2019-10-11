package de.domjos.schooltools.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;

public class AssistantHelper {
    private Activity activity;
    private int currentStep, maxStep;
    private TextView lblLearningCardAssistantTitle;

    // data for learningCards
    // step 1
    private EditText txtLearningCardAssistantGroupTitle;

    // step 2
    private EditText txtLearningCardAssistantVocabQuestion, txtLearningCardAssistantVocabAnswer;
    private AppCompatImageButton cmdLearningCardAssistantVocabBack;

    // step 3
    private EditText txtLearningCardAssistantQueryTitle, txtLearningCardAssistantQueryNumber;

    // general
    private TableLayout tblLearningCardAssistant;
    private AppCompatImageButton cmdLearningCardAssistantBack, cmdLearningCardAssistantForward;

    private final LearningCardGroup group = new LearningCardGroup();
    private final LearningCardQuery query = new LearningCardQuery();
    private final List<Map.Entry<String, String>> vocabulary = new LinkedList<>();
    private int currentVocab;


    // data for timetable



    // general
    private TextView lblTimeTableAssistantTitle;
    private TableLayout tblTimeTableAssistant;
    private AppCompatImageButton cmdTimeTableAssistantBack, cmdTimeTableAssistantForward;

    public AssistantHelper(Activity activity) {
        this.activity = activity;
        this.currentStep = 1;
        this.currentVocab = 1;

        this.query.setRandomVocab(true);
    }

    public void showLearningCardAssistant() {
        this.maxStep = 3;
        final Dialog dialog = this.buildDialog(R.layout.learning_card_assistant_dialog);

        // general
        this.tblLearningCardAssistant = dialog.findViewById(R.id.tblLearningCardAssistant);
        this.lblLearningCardAssistantTitle = dialog.findViewById(R.id.lblLearningCardAssistantTitle);
        final AppCompatImageButton cmdLearningCardAssistantSave = dialog.findViewById(R.id.cmdLearningCardAssistantSave);
        this.cmdLearningCardAssistantBack = dialog.findViewById(R.id.cmdLearningCardAssistantBack);
        this.cmdLearningCardAssistantForward = dialog.findViewById(R.id.cmdLearningCardAssistantForward);
        this.cmdLearningCardAssistantBack.setEnabled(false);
        showLearningCardSteps();

        // step 1
        this.txtLearningCardAssistantGroupTitle = dialog.findViewById(R.id.txtLearningCardAssistantGroupTitle);

        // step 2
        this.txtLearningCardAssistantVocabQuestion = dialog.findViewById(R.id.txtLearningCardAssistantVocabQuestion);
        this.txtLearningCardAssistantVocabAnswer = dialog.findViewById(R.id.txtLearningCardAssistantVocabAnswer);
        this.cmdLearningCardAssistantVocabBack = dialog.findViewById(R.id.cmdLearningCardAssistantVocabBack);
        final AppCompatImageButton cmdLearningCardAssistantVocabForward = dialog.findViewById(R.id.cmdLearningCardAssistantVocabForward);
        this.showVocab();

        // step 3
        this.txtLearningCardAssistantQueryTitle = dialog.findViewById(R.id.txtLearningCardAssistantQueryTitle);
        this.txtLearningCardAssistantQueryNumber = dialog.findViewById(R.id.txtLearningCardAssistantQueryNumber);

        this.txtLearningCardAssistantGroupTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    cmdLearningCardAssistantForward.callOnClick();
                    txtLearningCardAssistantVocabQuestion.requestFocus();
                }
                return false;
            }
        });

        this.txtLearningCardAssistantVocabAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT)) {
                    cmdLearningCardAssistantVocabForward.callOnClick();
                }
                return false;
            }
        });

        this.txtLearningCardAssistantQueryNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT)) {
                    cmdLearningCardAssistantSave.callOnClick();
                }
                return false;
            }
        });

        this.cmdLearningCardAssistantVocabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVocab -= 1;
                showVocab();
                setLearningCard();
                txtLearningCardAssistantVocabQuestion.requestFocus();
            }
        });

        cmdLearningCardAssistantVocabForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                        !txtLearningCardAssistantVocabAnswer.getText().toString().isEmpty() &&
                        !txtLearningCardAssistantVocabQuestion.getText().toString().isEmpty()) {

                    setVocab();
                    currentVocab += 1;
                    showVocab();
                    setLearningCard();
                    txtLearningCardAssistantVocabQuestion.requestFocus();
                }
            }
        });



        this.cmdLearningCardAssistantBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStep -= 1;
                showLearningCardSteps();
                changeLearningCardStepTitle();

                switch (currentStep) {
                    case 1:
                        txtLearningCardAssistantGroupTitle.requestFocus();
                        break;
                    case 2:
                        txtLearningCardAssistantVocabQuestion.requestFocus();
                        break;
                }
            }
        });

        this.cmdLearningCardAssistantForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStep += 1;
                showLearningCardSteps();
                changeLearningCardStepTitle();

                switch (currentStep) {
                    case 2:
                        txtLearningCardAssistantVocabQuestion.requestFocus();
                        break;
                    case 3:
                        txtLearningCardAssistantQueryTitle.requestFocus();
                        break;
                }
            }
        });

        cmdLearningCardAssistantSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLearningCardData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showTimeTableAssistant() {
        this.maxStep = 4;
        final Dialog dialog = this.buildDialog(R.layout.timetable_assistant_dialog);

        // general
        this.tblTimeTableAssistant = dialog.findViewById(R.id.tblTimeTableAssistant);
        this.lblTimeTableAssistantTitle = dialog.findViewById(R.id.lblTimeTableAssistantTitle);
        final AppCompatImageButton cmdTimeTableAssistantSave = dialog.findViewById(R.id.cmdTimeTableAssistantSave);
        this.cmdTimeTableAssistantBack = dialog.findViewById(R.id.cmdTimeTableAssistantBack);
        this.cmdTimeTableAssistantForward = dialog.findViewById(R.id.cmdTimeTableAssistantForward);
        this.cmdTimeTableAssistantBack.setEnabled(false);
        showTimeTableSteps();


        this.cmdTimeTableAssistantBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStep -= 1;
                showTimeTableSteps();
                changeTimeTableStepTitle();
            }
        });

        this.cmdTimeTableAssistantForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStep += 1;
                showTimeTableSteps();
                changeTimeTableStepTitle();
            }
        });

        cmdTimeTableAssistantSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTimeTableData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changeLearningCardStepTitle() {
        switch (this.currentStep) {
            case 1:
                this.lblLearningCardAssistantTitle.setText(R.string.learningCard_assistant_step1);
                break;
            case 2:
                this.lblLearningCardAssistantTitle.setText(R.string.learningCard_assistant_step2);
                break;
            case 3:
                this.lblLearningCardAssistantTitle.setText(R.string.learningCard_assistant_step3);
                break;
        }

        this.setLearningCard();
    }

    private void changeTimeTableStepTitle() {
        switch (this.currentStep) {
            case 1:
                this.lblTimeTableAssistantTitle.setText(R.string.timetable_assistant_step1);
                break;
            case 2:
                this.lblTimeTableAssistantTitle.setText(R.string.timetable_assistant_step2);
                break;
            case 3:
                this.lblTimeTableAssistantTitle.setText(R.string.timetable_assistant_step3);
                break;
            case 4:
                this.lblTimeTableAssistantTitle.setText(R.string.timetable_assistant_step4);
                break;
        }

        this.setTimeTable();
    }

    private void setLearningCard() {
        if(!this.txtLearningCardAssistantGroupTitle.getText().toString().isEmpty()) {
            String title = this.txtLearningCardAssistantGroupTitle.getText().toString().trim();
            this.group.setTitle(title);

            for(Subject subject : MainActivity.globals.getSqLite().getSubjects("")) {
                if(title.toLowerCase().contains(subject.getTitle().toLowerCase().trim())) {
                    this.group.setSubject(subject);
                    break;
                }
            }
        }

        for(Map.Entry<String, String> entry : this.vocabulary) {
            if(!entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                LearningCard learningCard = new LearningCard();
                learningCard.setTitle(entry.getKey());
                learningCard.setQuestion(entry.getKey());
                learningCard.setAnswer(entry.getValue());
                this.group.getLearningCards().add(learningCard);
            }
        }

        if(!this.txtLearningCardAssistantQueryTitle.getText().toString().trim().isEmpty()) {
            this.query.setTitle(this.txtLearningCardAssistantQueryTitle.getText().toString().trim());
        }
        if(!this.txtLearningCardAssistantQueryNumber.getText().toString().trim().isEmpty()) {
            this.query.setRandomVocabNumber(Integer.parseInt(this.txtLearningCardAssistantQueryNumber.getText().toString().trim()));
        }
    }

    private void setTimeTable() {

    }

    private void saveTimeTableData() {

    }

    private void saveLearningCardData() {
        setLearningCard();

        this.group.setID(MainActivity.globals.getSqLite().insertOrUpdateLearningCardGroup(this.group));

        this.query.setLearningCardGroup(this.group);
        MainActivity.globals.getSqLite().insertOrUpdateLearningCardQuery(this.query);
    }

    private void showVocab() {
        this.cmdLearningCardAssistantVocabBack.setEnabled(this.currentVocab != 1);

        if(this.currentVocab > this.vocabulary.size()) {
            this.vocabulary.add(new AbstractMap.SimpleEntry<>("", ""));
        }

        this.txtLearningCardAssistantVocabQuestion.setText(this.vocabulary.get(this.currentVocab-1).getKey());
        this.txtLearningCardAssistantVocabAnswer.setText(this.vocabulary.get(this.currentVocab-1).getValue());
    }

    private void setVocab() {
        this.vocabulary.set(this.currentVocab-1,
                new AbstractMap.SimpleEntry<>(
                        this.txtLearningCardAssistantVocabQuestion.getText().toString().trim(),
                        this.txtLearningCardAssistantVocabAnswer.getText().toString().trim()
                )
        );
    }

    private void showTimeTableSteps() {
        this.cmdTimeTableAssistantForward.setEnabled(this.currentStep != this.maxStep);
        this.cmdTimeTableAssistantBack.setEnabled(this.currentStep != 1);

        for(int i = 0; i<=this.tblTimeTableAssistant.getChildCount()-1; i++) {
            View view = this.tblTimeTableAssistant.getChildAt(i);
            String tag = view.getTag().toString();
            if(tag.equals("step" + this.currentStep)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    private void showLearningCardSteps() {
        this.cmdLearningCardAssistantForward.setEnabled(this.currentStep != this.maxStep);
        this.cmdLearningCardAssistantBack.setEnabled(this.currentStep != 1);

        for(int i = 0; i<=this.tblLearningCardAssistant.getChildCount()-1; i++) {
            View view = this.tblLearningCardAssistant.getChildAt(i);
            String tag = view.getTag().toString();
            if(tag.equals("step" + this.currentStep)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    private Dialog buildDialog(int resource) {
        final Dialog dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(resource);
        Window window = dialog.getWindow();
        if(window!=null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        return dialog;
    }
}
