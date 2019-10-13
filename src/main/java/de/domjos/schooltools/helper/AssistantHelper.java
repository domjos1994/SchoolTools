package de.domjos.schooltools.helper;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.TimeTableSubjectActivity;
import de.domjos.schooltools.adapter.ColorAdapter;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.core.model.timetable.Day;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.core.model.timetable.TimeTable;

public class AssistantHelper {
    private Activity activity;
    private int currentStep, maxStep;
    private int next = EditorInfo.IME_ACTION_NEXT, done = EditorInfo.IME_ACTION_DONE;
    private int enterKey = KeyEvent.KEYCODE_ENTER;

    // general
    private TableLayout tblAssistant;
    private TextView lblAssistantTitle;
    private AppCompatImageButton cmdAssistantBack, cmdAssistantForward;

    // data for learningCards
    // step 1
    private EditText txtLearningCardAssistantGroupTitle;

    // step 2
    private EditText txtLearningCardAssistantVocabQuestion, txtLearningCardAssistantVocabAnswer;
    private AppCompatImageButton cmdLearningCardAssistantVocabBack, cmdLearningCardAssistantVocabForward;
    private AppCompatImageButton cmdLearningCardAssistantSave;

    // step 3
    private EditText txtLearningCardAssistantQueryTitle, txtLearningCardAssistantQueryNumber;

    private final LearningCardGroup group = new LearningCardGroup();
    private final LearningCardQuery query = new LearningCardQuery();
    private final List<Map.Entry<String, String>> vocabulary = new LinkedList<>();
    private int currentVocab;


    // data for timetable
    // step 1
    private EditText txtTimeTableAssistantTimeTableTitle;
    private EditText txtTimeTableAssistantTimeTableYear;

    // step 2
    private ListView lvTimeTableAssistantTimes;
    private ArrayAdapter<Hour> timeTableAssistantTimesAdapter;
    private EditText txtTimeTableAssistantTimesStart, txtTimeTableAssistantTimesEnd;
    private CheckBox chkTimeTableAssistantTimesBreak;
    private Button cmdTimeTableAssistantTimesAdd;

    // step 3
    private ListView lvTimeTableAssistantSubjects;
    private ArrayAdapter<Subject> timeTableAssistantSubjectsAdapter;
    private EditText txtTimeTableAssistantSubjectsTitle, txtTimeTableAssistantSubjectsAlias;
    private Spinner spTimeTableAssistantSubjectsColor;
    private ArrayAdapter<String> timeTableAssistantSubjectsColorAdapter;
    private Button cmdTimeTableAssistantSubjectsAdd;
    private TableLayout tblTimeTableAssistantTimeTable;

    private AppCompatImageButton cmdTimeTableAssistantTimeTableSave;

    private final TimeTable timeTable = new TimeTable();
    private final Day[] days = new Day[8];

    public AssistantHelper(Activity activity) {
        this.activity = activity;
        this.currentStep = 1;
        this.currentVocab = 1;

        this.query.setRandomVocab(true);
    }

    public void showLearningCardAssistant() {
        this.maxStep = 3;
        final Dialog dialog = this.buildDialog(R.layout.learning_card_assistant_dialog);
        this.initLearningCardControls(dialog);

        this.txtLearningCardAssistantGroupTitle.setOnEditorActionListener((v, actionId, event) -> {
            if ((event!=null && (event.getKeyCode()==this.enterKey)) || (actionId==this.done)) {
                cmdAssistantForward.callOnClick();
                txtLearningCardAssistantVocabQuestion.requestFocus();
            }
            return false;
        });

        this.txtLearningCardAssistantVocabAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if ((event!=null && (event.getKeyCode()==this.enterKey)) || (actionId==this.done || actionId==this.next)) {
                cmdLearningCardAssistantVocabForward.callOnClick();
            }
            return false;
        });

        this.txtLearningCardAssistantQueryNumber.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode()==this.enterKey)) || (actionId==this.done || actionId==this.next)) {
                cmdLearningCardAssistantSave.callOnClick();
            }
            return false;
        });

        this.cmdLearningCardAssistantVocabBack.setOnClickListener(v -> {
            currentVocab -= 1;
            showVocab();
            setLearningCard();
            txtLearningCardAssistantVocabQuestion.requestFocus();
        });

        this.cmdLearningCardAssistantVocabForward.setOnClickListener(v -> {
            if(
                !this.txtLearningCardAssistantVocabAnswer.getText().toString().isEmpty() &&
                !this.txtLearningCardAssistantVocabQuestion.getText().toString().isEmpty()) {

                setVocab();
                currentVocab += 1;
                showVocab();
                setLearningCard();
                this.txtLearningCardAssistantVocabQuestion.requestFocus();
            }
        });

        this.cmdAssistantBack.setOnClickListener(v -> {
            currentStep -= 1;
            showSteps();
            changeLearningCardStepTitle();

            switch (currentStep) {
                case 1:
                    this.txtLearningCardAssistantGroupTitle.requestFocus();
                    break;
                case 2:
                    this.txtLearningCardAssistantVocabQuestion.requestFocus();
                    break;
            }
        });

        this.cmdAssistantForward.setOnClickListener(v -> {
            currentStep += 1;
            showSteps();
            changeLearningCardStepTitle();

            switch (currentStep) {
                case 2:
                    txtLearningCardAssistantVocabQuestion.requestFocus();
                    break;
                case 3:
                    txtLearningCardAssistantQueryTitle.requestFocus();
                    break;
            }
        });

        this.cmdLearningCardAssistantSave.setOnClickListener(v -> {
            saveLearningCardData();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showTimeTableAssistant(Runnable runnable) {
        this.maxStep = 4;
        final Dialog dialog = this.buildDialog(R.layout.timetable_assistant_dialog);
        this.initTimeTableControls(dialog);

        this.cmdTimeTableAssistantTimesAdd.setOnClickListener(v -> {
            Hour hour = new Hour();
            hour.setStart(this.txtTimeTableAssistantTimesStart.getText().toString().trim());
            hour.setEnd(this.txtTimeTableAssistantTimesEnd.getText().toString().trim());
            hour.setBreak(this.chkTimeTableAssistantTimesBreak.isChecked());

            boolean contains = false;
            for(int i = 0; i <= this.timeTableAssistantTimesAdapter.getCount() - 1; i++) {
                Hour current = this.timeTableAssistantTimesAdapter.getItem(i);

                if(current!=null) {
                    if(hour.getStart().equals(current.getStart().trim()) && hour.getEnd().equals(current.getEnd().trim())) {
                        contains = true;
                        break;
                    }
                }
            }

            if(!contains) {
                hour.setID(MainActivity.globals.getSqLite().insertOrUpdateHour(hour));
                this.timeTableAssistantTimesAdapter.add(hour);
            }
        });

        this.lvTimeTableAssistantTimes.setOnItemLongClickListener((parent, view, position, id) -> {
            Hour hour = this.timeTableAssistantTimesAdapter.getItem(position);
            if(hour != null) {
                MainActivity.globals.getSqLite().deleteEntry("hours", "ID=" + hour.getID());
                this.timeTableAssistantTimesAdapter.remove(hour);
            }
            return true;
        });

        this.lvTimeTableAssistantTimes.setOnItemClickListener((parent, view, position, id) -> {
            Hour hour = this.timeTableAssistantTimesAdapter.getItem(position);

            if(hour != null) {
                this.txtTimeTableAssistantTimesStart.setText(hour.getStart());
                this.txtTimeTableAssistantTimesEnd.setText(hour.getEnd());
                this.chkTimeTableAssistantTimesBreak.setChecked(hour.isBreak());
            }
        });

        this.cmdTimeTableAssistantSubjectsAdd.setOnClickListener((v) -> {
            Subject subject = new Subject();
            subject.setTitle(this.txtTimeTableAssistantSubjectsTitle.getText().toString().trim());
            subject.setAlias(this.txtTimeTableAssistantSubjectsAlias.getText().toString().trim());
            if(this.spTimeTableAssistantSubjectsColor.getSelectedItem()!=null) {
                subject.setBackgroundColor(
                    String.valueOf(
                        ((ColorDrawable) this.spTimeTableAssistantSubjectsColor.getBackground()).getColor()
                    )
                );
            }


            boolean contains = false;
            for(int i = 0; i <= this.timeTableAssistantSubjectsAdapter.getCount() - 1; i++) {
                Subject current = this.timeTableAssistantSubjectsAdapter.getItem(i);

                if(current!=null) {
                    if(current.getTitle().equals(subject.getTitle())) {
                        contains = true;
                        break;
                    }
                }
            }

            if(!contains) {
                subject.setID(MainActivity.globals.getSqLite().insertOrUpdateSubject(subject));
                this.timeTableAssistantSubjectsAdapter.add(subject);
            }
        });

        this.lvTimeTableAssistantSubjects.setOnItemLongClickListener((parent, view, position, id) -> {
            Subject subject = this.timeTableAssistantSubjectsAdapter.getItem(position);
            if(subject != null) {
                this.timeTableAssistantSubjectsAdapter.remove(subject);
                MainActivity.globals.getSqLite().deleteEntry("subjects", "ID=" + subject.getID());
            }
            return true;
        });

        this.lvTimeTableAssistantSubjects.setOnItemClickListener((parent, view, position, id) -> {
            Subject subject = this.timeTableAssistantSubjectsAdapter.getItem(position);

            if(subject != null) {
                this.txtTimeTableAssistantSubjectsTitle.setText(subject.getTitle());
                this.txtTimeTableAssistantSubjectsAlias.setText(subject.getAlias());

                try {
                    int color = Integer.parseInt(subject.getBackgroundColor());
                    this.spTimeTableAssistantSubjectsColor.setSelection(
                        this.timeTableAssistantSubjectsColorAdapter.getPosition(
                            TimeTableSubjectActivity.getSelectedName(this.activity, color)
                        )
                    );
                } catch (Exception ignored) {}
            }
        });

        this.spTimeTableAssistantSubjectsColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( spTimeTableAssistantSubjectsColor.getSelectedItem() != null) {
                    spTimeTableAssistantSubjectsColor.setBackgroundColor(
                        activity.getResources().getColor(
                            ColorAdapter.getSelectedColor(activity, spTimeTableAssistantSubjectsColor.getSelectedItem().toString())
                        )
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        this.cmdTimeTableAssistantTimeTableSave.setOnClickListener((v) -> {
            saveTimeTable();
            dialog.dismiss();
            runnable.run();
        });

        this.cmdAssistantBack.setOnClickListener(v -> {
            currentStep -= 1;
            showSteps();
            changeTimeTableStepTitle();
        });

        this.cmdAssistantForward.setOnClickListener(v -> {
            currentStep += 1;
            showSteps();
            changeTimeTableStepTitle();
        });

        dialog.show();
    }

    private void changeLearningCardStepTitle() {
        switch (this.currentStep) {
            case 1:
                this.lblAssistantTitle.setText(R.string.learningCard_assistant_step1);
                break;
            case 2:
                this.lblAssistantTitle.setText(R.string.learningCard_assistant_step2);
                break;
            case 3:
                this.lblAssistantTitle.setText(R.string.learningCard_assistant_step3);
                break;
        }

        this.setLearningCard();
    }

    private void changeTimeTableStepTitle() {
        switch (this.currentStep) {
            case 1:
                this.lblAssistantTitle.setText(R.string.timetable_assistant_step1);
                break;
            case 2:
                this.lblAssistantTitle.setText(R.string.timetable_assistant_step2);
                break;
            case 3:
                this.lblAssistantTitle.setText(R.string.timetable_assistant_step3);
                break;
            case 4:
                this.buildTableLayout();
                this.lblAssistantTitle.setText(R.string.timetable_assistant_step4);
                break;
        }
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

    private void saveLearningCardData() {
        setLearningCard();

        List<LearningCard> learningCards = new LinkedList<>();
        for(LearningCard card : this.group.getLearningCards()) {
            boolean contains = false;
            for(LearningCard current : learningCards) {
                if(current.getQuestion().equals(card.getQuestion())) {
                    if(current.getQuestion().equals(card.getQuestion())) {
                        contains = true;
                    }
                }
            }

            if(!contains) {
                learningCards.add(card);
            }
        }
        this.group.setLearningCards(learningCards);
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

    private void buildTableLayout() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        layoutParams.width = 0;
        layoutParams.weight = 1;

        TableRow tableRow = new TableRow(this.activity);

        TextView emptyView = new TextView(this.activity);
        emptyView.setLayoutParams(layoutParams);
        tableRow.addView(emptyView);

        for(int i = 0; i<= DayOfWeek.values().length - 3; i++) {
            Day currentDay = new Day();
            currentDay.setPositionInWeek(i);
            this.days[i] = currentDay;

            DayOfWeek dayOfWeek = DayOfWeek.values()[i];
            TextView day = new TextView(this.activity);
            day.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                day.setText(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.GERMAN));
            } else {
                day.setText(String.valueOf(i+1));
            }
            tableRow.addView(day);
        }

        this.tblTimeTableAssistantTimeTable.addView(tableRow);

        for(int i = 0; i<=this.timeTableAssistantTimesAdapter.getCount()-1; i++) {
            Hour hour = this.timeTableAssistantTimesAdapter.getItem(i);

            if(hour!=null) {
                TableRow hourRow = new TableRow(this.activity);

                TextView hourView = new TextView(this.activity);
                hourView.setLayoutParams(layoutParams);
                hourView.setGravity(Gravity.CENTER);

                if(!hour.isBreak()) {
                    hourView.setText(String.valueOf(i + 1));
                }
                hourRow.addView(hourView);
                this.tblTimeTableAssistantTimeTable.addView(hourRow);

                for(int j = 0; j<=DayOfWeek.values().length - 3; j++) {
                    AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this.activity);
                    ArrayAdapter<String> aliasAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_list_item_1);
                    for(int sub = 0; sub <=this.timeTableAssistantSubjectsAdapter.getCount() - 1; sub++) {
                        Subject subject = this.timeTableAssistantSubjectsAdapter.getItem(sub);

                        if(subject!=null) {
                            aliasAdapter.add(subject.getAlias());
                        }
                    }
                    autoCompleteTextView.setAdapter(aliasAdapter);
                    autoCompleteTextView.setLayoutParams(layoutParams);

                    autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
                        if(hasFocus) {
                            autoCompleteTextView.showDropDown();
                        }
                    });

                    autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                        @Override
                        public void afterTextChanged(Editable s) {
                            convertTableViewToDays();
                        }
                    });

                    hourRow.addView(autoCompleteTextView);
                }
            }
        }
    }

    private void saveTimeTable() {
        de.domjos.schooltools.core.model.mark.Year year = new Year();
        year.setTitle(this.txtTimeTableAssistantTimeTableYear.getText().toString().trim());
        for(de.domjos.schooltools.core.model.mark.Year currentYear : MainActivity.globals.getSqLite().getYears("")) {
            if(currentYear.getTitle().equals(year.getTitle())) {
                year.setID(currentYear.getID());
                break;
            }
        }
        year.setID(MainActivity.globals.getSqLite().insertOrUpdateYear(year));

        this.timeTable.setYear(year);
        this.timeTable.setTitle(this.txtTimeTableAssistantTimeTableTitle.getText().toString().trim());

        for(Day currentDay : this.days) {
            if(currentDay!=null) {
                this.timeTable.addDay(currentDay);
            }
        }
        MainActivity.globals.getSqLite().insertOrUpdateTimeTable(this.timeTable);
    }

    private void convertTableViewToDays() {
        for(int i = 0; i <= days.length - 1; i++) {
            if(days[i]!=null) {
                days[i].getPupilHour().clear();
                days[i].getTeacherHour().clear();
            }
        }

        for(int j = 1; j <= 6; j++) {
            for(int i = 1; i <= this.tblTimeTableAssistantTimeTable.getChildCount() - 1; i++) {
                TableRow currentRow = (TableRow) this.tblTimeTableAssistantTimeTable.getChildAt(i);
                if(currentRow.getChildAt(j) instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = ((AutoCompleteTextView)currentRow.getChildAt(j));

                    if(autoCompleteTextView != null) {
                        String alias = autoCompleteTextView.getText().toString().trim();

                        Subject currentSubject = null;
                        for(int k = 0; k <= this.timeTableAssistantSubjectsAdapter.getCount() - 1; k++) {
                            Subject subject = this.timeTableAssistantSubjectsAdapter.getItem(k);

                            if(subject != null) {
                                if (subject.getAlias().equals(alias)) {
                                    currentSubject = subject;
                                    break;
                                }
                            }
                        }

                        days[j-1].addTeacherHour(this.timeTableAssistantTimesAdapter.getItem( i - 1), currentSubject, null, "");
                        days[j-1].addPupilHour(this.timeTableAssistantTimesAdapter.getItem( i - 1), currentSubject, null, "");
                    }
                }
            }
        }
    }

    private void showSteps() {
        this.cmdAssistantForward.setEnabled(this.currentStep != this.maxStep);
        this.cmdAssistantBack.setEnabled(this.currentStep != 1);

        for(int i = 0; i<=this.tblAssistant.getChildCount()-1; i++) {
            View view = this.tblAssistant.getChildAt(i);
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


    /**
     * initialize the controls of the learning-cards
     * @param dialog the current Dialog
     */
    private void initLearningCardControls(Dialog dialog) {
        // general
        this.tblAssistant = dialog.findViewById(R.id.tblLearningCardAssistant);
        this.lblAssistantTitle = dialog.findViewById(R.id.lblLearningCardAssistantTitle);
        this.cmdLearningCardAssistantSave = dialog.findViewById(R.id.cmdLearningCardAssistantSave);
        this.cmdAssistantBack = dialog.findViewById(R.id.cmdLearningCardAssistantBack);
        this.cmdAssistantForward = dialog.findViewById(R.id.cmdLearningCardAssistantForward);
        this.cmdAssistantBack.setEnabled(false);
        showSteps();

        // step 1
        this.txtLearningCardAssistantGroupTitle = dialog.findViewById(R.id.txtLearningCardAssistantGroupTitle);

        // step 2
        this.txtLearningCardAssistantVocabQuestion = dialog.findViewById(R.id.txtLearningCardAssistantVocabQuestion);
        this.txtLearningCardAssistantVocabAnswer = dialog.findViewById(R.id.txtLearningCardAssistantVocabAnswer);
        this.cmdLearningCardAssistantVocabBack = dialog.findViewById(R.id.cmdLearningCardAssistantVocabBack);
        this.cmdLearningCardAssistantVocabForward = dialog.findViewById(R.id.cmdLearningCardAssistantVocabForward);
        this.showVocab();

        // step 3
        this.txtLearningCardAssistantQueryTitle = dialog.findViewById(R.id.txtLearningCardAssistantQueryTitle);
        this.txtLearningCardAssistantQueryNumber = dialog.findViewById(R.id.txtLearningCardAssistantQueryNumber);
    }

    /**
     * initialize the controls of the time-table
     * @param dialog the current Dialog
     */
    private void initTimeTableControls(Dialog dialog) {
        // general
        this.tblAssistant = dialog.findViewById(R.id.tblTimeTableAssistant);
        this.lblAssistantTitle = dialog.findViewById(R.id.lblTimeTableAssistantTitle);
        this.cmdAssistantBack = dialog.findViewById(R.id.cmdTimeTableAssistantBack);
        this.cmdAssistantForward = dialog.findViewById(R.id.cmdTimeTableAssistantForward);
        this.cmdTimeTableAssistantTimeTableSave = dialog.findViewById(R.id.cmdTimeTableAssistantSave);
        this.cmdAssistantBack.setEnabled(false);
        showSteps();

        // step 1
        this.txtTimeTableAssistantTimeTableTitle = dialog.findViewById(R.id.txtTimeTableAssistantTimeTableTitle);
        this.txtTimeTableAssistantTimeTableYear = dialog.findViewById(R.id.txtTimeTableAssistantTimeTableYear);

        // step 2
        this.lvTimeTableAssistantTimes = dialog.findViewById(R.id.lvTimeTableAssistantTimes);
        this.timeTableAssistantTimesAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_list_item_1, MainActivity.globals.getSqLite().getHours(""));
        this.lvTimeTableAssistantTimes.setAdapter(this.timeTableAssistantTimesAdapter);
        this.timeTableAssistantTimesAdapter.notifyDataSetChanged();

        this.txtTimeTableAssistantTimesStart = dialog.findViewById(R.id.txtTimeTableAssistantTimesStart);
        this.txtTimeTableAssistantTimesEnd = dialog.findViewById(R.id.txtTimeTableAssistantTimesEnd);
        this.chkTimeTableAssistantTimesBreak = dialog.findViewById(R.id.chkTimeTableAssistantTimesBreak);
        this.cmdTimeTableAssistantTimesAdd = dialog.findViewById(R.id.cmdTimeTableAssistantTimesAdd);

        // step 3
        this.lvTimeTableAssistantSubjects = dialog.findViewById(R.id.lvTimeTableAssistantSubjects);
        this.timeTableAssistantSubjectsAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_list_item_1, MainActivity.globals.getSqLite().getSubjects(""));
        this.lvTimeTableAssistantSubjects.setAdapter(this.timeTableAssistantSubjectsAdapter);
        this.timeTableAssistantSubjectsAdapter.notifyDataSetChanged();

        this.txtTimeTableAssistantSubjectsTitle = dialog.findViewById(R.id.txtTimeTableAssistantSubjectsTitle);
        this.txtTimeTableAssistantSubjectsAlias = dialog.findViewById(R.id.txtTimeTableAssistantSubjectsAlias);
        this.spTimeTableAssistantSubjectsColor = dialog.findViewById(R.id.spTimeTableAssistantSubjectsColor);
        this.timeTableAssistantSubjectsColorAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_spinner_item);
        for(String color : this.activity.getResources().getStringArray(R.array.colorNames)) {
            this.timeTableAssistantSubjectsColorAdapter.add(color);
        }
        this.spTimeTableAssistantSubjectsColor.setAdapter(this.timeTableAssistantSubjectsColorAdapter);
        this.timeTableAssistantSubjectsColorAdapter.notifyDataSetChanged();
        this.cmdTimeTableAssistantSubjectsAdd = dialog.findViewById(R.id.cmdTimeTableAssistantSubjectsAdd);

        // step 4
        this.tblTimeTableAssistantTimeTable = dialog.findViewById(R.id.tblTimeTableAssistantTimeTable);
    }
}
