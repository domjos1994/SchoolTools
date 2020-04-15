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
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;

/**
 * Activity For the ToDo-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class ToDoEntryActivity extends AbstractActivity {

    private EditText txtToDoTitle, txtToDoDescription, txtToDoCategory, txtToDoMemoryDate;
    private RatingBar rbToDoImportance;
    private CheckBox chkToDoSolved, chkToDoMemory;

    private Validator validator;

    public ToDoEntryActivity() {
        super(R.layout.todo_entry_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {

        this.chkToDoMemory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
               txtToDoMemoryDate.setVisibility(View.VISIBLE);
            } else {
                txtToDoMemoryDate.setText("");
                txtToDoMemoryDate.setVisibility(View.GONE);
            }
        });
    }

    private void fillToDo(int id) {
        List<ToDo> toDos = MainActivity.globals.getSqLite().getToDos("ID=" + id);
        if(toDos!=null) {
            if(!toDos.isEmpty()) {
                ToDo toDo = toDos.get(0);
                if(toDo!=null) {
                    txtToDoTitle.setText(toDo.getTitle());
                    txtToDoDescription.setText(toDo.getDescription());
                    txtToDoCategory.setText(toDo.getCategory());
                    chkToDoSolved.setChecked(toDo.isSolved());
                    rbToDoImportance.setRating(toDo.getImportance());
                    chkToDoMemory.setChecked(true);
                    txtToDoMemoryDate.setText(ConvertHelper.convertDateToString(toDo.getMemoryDate(), this.getApplicationContext()));
                    txtToDoMemoryDate.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initValidation() {
        this.validator = new Validator(ToDoEntryActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addLengthValidator(txtToDoTitle, 3, 500);
        this.validator.addDateValidator(txtToDoMemoryDate, false);
    }

    @Override
    protected void initControls() {
        // init extras
        final int id = this.getIntent().getIntExtra("id", 0);
        final String list = this.getIntent().getStringExtra("list");

        // init navigation_learning_card_group
        OnNavigationItemSelectedListener listener = item -> {
            try {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("toDos", "ID", id, "");
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case R.id.navTimeTableSubSave:
                        if (validator.getState()) {
                            ToDo toDo = new ToDo();
                            toDo.setId(id);
                            toDo.setTitle(txtToDoTitle.getText().toString());
                            toDo.setDescription(txtToDoDescription.getText().toString());
                            toDo.setCategory(txtToDoCategory.getText().toString());
                            toDo.setSolved(chkToDoSolved.isChecked());
                            toDo.setImportance((int) rbToDoImportance.getRating());
                            if (chkToDoMemory.isChecked()) {
                                if (!txtToDoMemoryDate.getText().toString().equals("")) {
                                    toDo.setMemoryDate(ConvertHelper.convertStringToDate(txtToDoMemoryDate.getText().toString(), getApplicationContext()));
                                }
                            }
                            MainActivity.globals.getSqLite().insertOrUpdateToDo(toDo, list);

                            setResult(RESULT_OK);
                            finish();
                        }
                        break;
                    case R.id.navTimeTableSubCancel:
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default:
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ToDoEntryActivity.this);
            }
            return false;
        };
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);
        navigation.getMenu().removeItem(R.id.navTimeTableSubAdd);
        navigation.getMenu().removeItem(R.id.navTimeTableSubEdit);
        if(id==0) {
            navigation.getMenu().removeItem(R.id.navTimeTableSubDelete);
        }

        // init other controls
        TextView lblToDoList = this.findViewById(R.id.lblToDoList);
        lblToDoList.setText(list);
        this.txtToDoTitle = this.findViewById(R.id.txtToDoTitle);
        this.txtToDoDescription = this.findViewById(R.id.txtToDoDescription);
        this.txtToDoCategory = this.findViewById(R.id.txtToDoDCategory);
        this.rbToDoImportance = this.findViewById(R.id.rbToDoImportance);
        //this.rbToDoImportance.setNumStars(5);
        this.chkToDoSolved = this.findViewById(R.id.chkToDoSolved);
        this.chkToDoMemory = this.findViewById(R.id.chkToDoMemory);
        this.txtToDoMemoryDate = this.findViewById(R.id.txtToDoMemoryDate);
        this.txtToDoMemoryDate.setVisibility(View.GONE);

        this.initValidation();
        this.fillToDo(id);
    }
}
