/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardAdapter;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.learningCard.LearningCard;
import de.domjos.schooltoolslib.model.learningCard.LearningCardGroup;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class LearningCardGroupEntryActivity extends AbstractActivity {
    private Validator validator;
    private LearningCardGroup learningCardGroup;
    private int cardPosition = 0;

    private EditText txtLearningCardGroupTitle, txtLearningCardGroupDeadline, txtLearningCardGroupDescription;
    private AutoCompleteTextView txtLearningCardGroupCategory;
    private Spinner spLearningCardGroupSubject, spLearningCardGroupTeacher;
    private ArrayAdapter<Subject> subjectAdapter;
    private ArrayAdapter<Teacher> teacherAdapter;

    private ListView lvLearningCards;
    private List<LearningCard> learningCards;
    private SeekBar sbLearningCardPriority;
    private EditText txtLearningCardTitle, txtLearningCardQuestion, txtLearningCardNote1, txtLearningCardNote2;
    private AutoCompleteTextView txtLearningCardCategory, txtLearningCardAnswer;
    private ArrayAdapter<String> dictionaryAdapter;


    public LearningCardGroupEntryActivity() {
        super(R.layout.learning_card_group_entry_activity, MainActivity.globals.getSqLite().getSetting("background"));
    }

    @Override
    protected void initActions() {
        this.loadCardGroup();

        this.lvLearningCards.setOnItemClickListener((parent, view, position, id) -> {
            cardPosition = position;
            loadCard();
        });

        this.lvLearningCards.setOnItemLongClickListener((parent, view, position, id) -> {
            LearningCard learningCard = learningCards.get(position);
            if(learningCard!=null) {
                if(learningCard.getID()!=0) {
                    learningCards.remove(learningCard);
                    for(int i = 0; i<=learningCards.size()-1; i++) {
                        LearningCard tmp = learningCards.get(i);
                        if(tmp!=null) {
                            if(tmp.getID()==0) {
                                cardPosition = i;
                                loadCard();
                                break;
                            }
                        }
                    }
                }
            }
            return false;
        });

        this.txtLearningCardTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setTitle(s.toString());
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.txtLearningCardCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setCategory(s.toString());
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.txtLearningCardQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setQuestion(s.toString());

                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.txtLearningCardAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setAnswer(s.toString());
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.txtLearningCardNote1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setNote1(s.toString());
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.txtLearningCardNote2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setNote2(s.toString());
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
        });

        this.sbLearningCardPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    LearningCard learningCard = learningCards.get(cardPosition);
                    if(learningCard!=null) {
                        learningCard.setPriority(progress);
                        reloadList();
                    }
                } catch (Exception ex) {
                    Helper.printException(getApplicationContext(), ex);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        this.txtLearningCardAnswer.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                dictionaryAdapter.clear();
                for(String item : MainActivity.globals.getSqLite().findInDictionary(txtLearningCardQuestion.getText().toString())) {
                    dictionaryAdapter.add(item);
                }
            }
        });

        this.txtLearningCardAnswer.setOnItemClickListener((parent, view, position, id) -> {
            try {
                LearningCard learningCard = learningCards.get(cardPosition);
                if(learningCard!=null) {
                    learningCard.setAnswer(txtLearningCardAnswer.getText().toString());
                    reloadList();
                }
            } catch (Exception ex) {
                Helper.printException(getApplicationContext(), ex);
            }
        });
    }


    @Override
    protected void initControls() {
        BottomNavigationView navigation = this.findViewById(R.id.navigation);

        BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
            switch (item.getItemId()) {
                case R.id.navLearningCardGroupCancel:
                    setResult(RESULT_OK);
                    finish();
                    return true;
                case R.id.navLearningCardGroupSave:
                    try {
                        if(validator.getState()) {
                            getCardGroup();
                            MainActivity.globals.getSqLite().insertOrUpdateLearningCardGroup(learningCardGroup);
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (Exception ex) {
                        Helper.printException(getApplicationContext(), ex);
                    }
                    return true;
            }
            return false;
        };
        navigation.setOnNavigationItemSelectedListener(listener);


        TabHost host = this.findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec tabSpec1 = host.newTabSpec(this.getString(R.string.learningCard_groups));
        tabSpec1.setContent(R.id.tab1);
        tabSpec1.setIndicator(this.getString(R.string.learningCard_groups));
        host.addTab(tabSpec1);

        TabHost.TabSpec tabSpec2 = host.newTabSpec(this.getString(R.string.main_nav_learningCards));
        tabSpec2.setContent(R.id.tab2);
        tabSpec2.setIndicator(this.getString(R.string.main_nav_learningCards));
        host.addTab(tabSpec2);

        this.txtLearningCardGroupTitle = this.findViewById(R.id.txtLearningCardGroupTitle);
        this.txtLearningCardGroupDescription = this.findViewById(R.id.txtLearningCardGroupDescription);
        this.txtLearningCardGroupCategory = this.findViewById(R.id.txtLearningCardGroupCategory);
        this.txtLearningCardGroupDeadline = this.findViewById(R.id.txtLearningCardGroupDeadline);

        this.spLearningCardGroupSubject = this.findViewById(R.id.spLearningCardGroupSubject);
        this.subjectAdapter = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, MainActivity.globals.getSqLite().getSubjects(""));
        this.subjectAdapter.add(new Subject());
        this.spLearningCardGroupSubject.setAdapter(this.subjectAdapter);
        this.subjectAdapter.notifyDataSetChanged();

        this.spLearningCardGroupTeacher = this.findViewById(R.id.spLearningCardGroupTeacher);
        this.teacherAdapter = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, MainActivity.globals.getSqLite().getTeachers(""));
        this.teacherAdapter.add(new Teacher());
        this.spLearningCardGroupTeacher.setAdapter(this.teacherAdapter);
        this.teacherAdapter.notifyDataSetChanged();


        this.lvLearningCards = this.findViewById(R.id.lvLearningCards);
        this.learningCards = new LinkedList<>();
        LearningCardAdapter learningCardAdapter = new LearningCardAdapter(this.getApplicationContext(), R.layout.learning_card_item, this.learningCards);
        learningCardAdapter.add(new LearningCard());
        this.lvLearningCards.setAdapter(learningCardAdapter);
        learningCardAdapter.notifyDataSetChanged();

        TextView lblLearningCardPriority = this.findViewById(R.id.lblLearningCardPriority);
        this.sbLearningCardPriority = this.findViewById(R.id.sbLearningCardPriority);
        lblLearningCardPriority.setText(String.valueOf(this.sbLearningCardPriority.getProgress()));
        this.sbLearningCardPriority.setOnSeekBarChangeListener(Helper.getChangeListener(lblLearningCardPriority));

        this.txtLearningCardTitle = this.findViewById(R.id.txtLearningCardTitle);
        this.txtLearningCardCategory = this.findViewById(R.id.txtLearningCardCategory);
        this.txtLearningCardQuestion = this.findViewById(R.id.txtLearningCardQuestion);
        this.txtLearningCardAnswer = this.findViewById(R.id.txtLearningCardAnswer);
        this.dictionaryAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1);
        this.txtLearningCardAnswer.setAdapter(this.dictionaryAdapter);
        this.dictionaryAdapter.notifyDataSetChanged();
        this.txtLearningCardNote1 = this.findViewById(R.id.txtLearningCardNote1);
        this.txtLearningCardNote2 = this.findViewById(R.id.txtLearningCardNote2);

        List<LearningCardGroup> groups = MainActivity.globals.getSqLite().getLearningCardGroups("", true);
        List<String> categories = new LinkedList<>();
        for(LearningCardGroup group : groups) {
            if(!categories.contains(group.getCategory().trim())) {
                categories.add(group.getCategory().trim());
            }

            for(LearningCard card : group.getLearningCards()) {
                if(!categories.contains(card.getCategory().trim())) {
                    categories.add(card.getCategory().trim());
                }
            }
        }

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, categories);
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, categories);
        this.txtLearningCardGroupCategory.setAdapter(groupAdapter);
        this.txtLearningCardCategory.setAdapter(cardAdapter);
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addEmptyValidator(this.txtLearningCardGroupTitle);
        this.validator.addDateValidator(this.txtLearningCardGroupDeadline, new Date(), null);
    }

    private void loadCardGroup() {
        int id = this.getIntent().getIntExtra("ID", 0);
        if(id==0) {
            this.learningCardGroup = new LearningCardGroup();
        } else {
            List<LearningCardGroup> learningCardGroups = MainActivity.globals.getSqLite().getLearningCardGroups("ID=" + id, true);
            this.learningCardGroup = learningCardGroups.get(0);
        }

        this.txtLearningCardGroupTitle.setText(this.learningCardGroup.getTitle());
        this.txtLearningCardGroupCategory.setText(this.learningCardGroup.getCategory());
        this.txtLearningCardGroupDescription.setText(this.learningCardGroup.getDescription());
        Date deadLine = this.learningCardGroup.getDeadLine();
        if(deadLine!=null) {
            this.txtLearningCardGroupDeadline.setText(Converter.convertDateToString(deadLine));
        }

        Subject subject = this.learningCardGroup.getSubject();
        for(int i = 0; i<=this.subjectAdapter.getCount()-1; i++) {
            Subject tmp = this.subjectAdapter.getItem(i);
            if(tmp!=null) {
                if(subject!=null) {
                    if(tmp.getID()==subject.getID()) {
                        this.spLearningCardGroupSubject.setSelection(i);
                        break;
                    }
                } else {
                    if(tmp.getID()==0) {
                        this.spLearningCardGroupSubject.setSelection(i);
                        break;
                    }
                }
            }

        }

        Teacher teacher = this.learningCardGroup.getTeacher();
        if(teacher!=null) {
            this.spLearningCardGroupTeacher.setSelection(this.teacherAdapter.getPosition(teacher));
        } else {
            for(int i = 0; i<=this.teacherAdapter.getCount()-1; i++) {
                Teacher tmp = this.teacherAdapter.getItem(i);
                if(tmp!=null) {
                    if(tmp.getID()==0) {
                        this.spLearningCardGroupTeacher.setSelection(i);
                        break;
                    }
                }

            }
        }

        this.learningCards.addAll(this.learningCardGroup.getLearningCards());
    }

    private void loadCard() {
        LearningCard learningCard = this.learningCards.get(this.cardPosition);
        this.txtLearningCardTitle.setText(learningCard.getTitle());
        this.txtLearningCardCategory.setText(learningCard.getCategory());
        this.txtLearningCardQuestion.setText(learningCard.getQuestion());
        this.txtLearningCardAnswer.setText(learningCard.getAnswer());
        this.txtLearningCardNote1.setText(learningCard.getNote1());
        this.txtLearningCardNote2.setText(learningCard.getNote2());
        this.sbLearningCardPriority.setProgress(learningCard.getPriority());
    }

    private void getCardGroup() throws Exception {
        this.learningCardGroup.setTitle(this.txtLearningCardGroupTitle.getText().toString());
        this.learningCardGroup.setDescription(this.txtLearningCardGroupDescription.getText().toString());
        this.learningCardGroup.setCategory(this.txtLearningCardGroupCategory.getText().toString());

        String deadLine = this.txtLearningCardGroupDeadline.getText().toString();
        if(!deadLine.trim().equals("")) {
            this.learningCardGroup.setDeadLine(Converter.convertStringToDate(deadLine));
        }

        Teacher teacher = this.teacherAdapter.getItem(this.spLearningCardGroupTeacher.getSelectedItemPosition());
        if(teacher!=null) {
            if(teacher.getID()!=0) {
                this.learningCardGroup.setTeacher(teacher);
            } else {
                this.learningCardGroup.setTeacher(null);
            }
        } else {
            this.learningCardGroup.setTeacher(null);
        }

        Subject subject = this.subjectAdapter.getItem(this.spLearningCardGroupSubject.getSelectedItemPosition());
        if(subject!=null) {
            if(subject.getID()!=0) {
                this.learningCardGroup.setSubject(subject);
            } else {
                this.learningCardGroup.setSubject(null);
            }
        } else {
            this.learningCardGroup.setSubject(null);
        }
        this.learningCardGroup.setLearningCards(this.learningCards);
    }

    private void reloadList() {
        boolean isAvailable = false;
        for(LearningCard tmp : learningCards) {
            if(tmp.getID()==0 && tmp.getTitle().trim().equals("") && tmp.getQuestion().trim().equals("")) {
                isAvailable = true;
                break;
            }
        }
        if(!isAvailable) {
            cardPosition = 1;
            learningCards.add(0, new LearningCard());
        }
        lvLearningCards.invalidateViews();
    }
}
