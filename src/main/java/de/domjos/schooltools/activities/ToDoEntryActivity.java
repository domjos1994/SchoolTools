/*
 * Copyright (C) 2017-2019  Dominic Joas
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
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the ToDo-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDoEntryActivity extends AppCompatActivity {

    private EditText txtToDoTitle, txtToDoDescription, txtToDoCategory, txtToDoMemoryDate;
    private RatingBar rbToDoImportance;
    private CheckBox chkToDoSolved, chkToDoMemory;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_entry_activity);
        this.initControls();

        this.chkToDoMemory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                   txtToDoMemoryDate.setVisibility(View.VISIBLE);
                } else {
                    txtToDoMemoryDate.setText("");
                    txtToDoMemoryDate.setVisibility(View.GONE);
                }
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
                    txtToDoMemoryDate.setText(Converter.convertDateToString(toDo.getMemoryDate()));
                    txtToDoMemoryDate.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initValidation() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtToDoTitle, 3, 500);
        this.validator.addDateValidator(txtToDoMemoryDate);
    }

    private void initControls() {
        // init extras
        final int id = this.getIntent().getIntExtra("id", 0);
        final String list = this.getIntent().getStringExtra("list");

        // init navigation_learning_card_group
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                                toDo.setID(id);
                                toDo.setTitle(txtToDoTitle.getText().toString());
                                toDo.setDescription(txtToDoDescription.getText().toString());
                                toDo.setCategory(txtToDoCategory.getText().toString());
                                toDo.setSolved(chkToDoSolved.isChecked());
                                toDo.setImportance((int) rbToDoImportance.getRating());
                                if (chkToDoMemory.isChecked()) {
                                    if (!txtToDoMemoryDate.getText().toString().equals("")) {
                                        toDo.setMemoryDate(Converter.convertStringToDate(txtToDoMemoryDate.getText().toString()));
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
                    Helper.printException(getApplicationContext(), ex);
                }
                return false;
            }
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
        this.rbToDoImportance.setNumStars(5);
        this.chkToDoSolved = this.findViewById(R.id.chkToDoSolved);
        this.chkToDoMemory = this.findViewById(R.id.chkToDoMemory);
        this.txtToDoMemoryDate = this.findViewById(R.id.txtToDoMemoryDate);
        this.txtToDoMemoryDate.setVisibility(View.GONE);

        this.initValidation();
        this.fillToDo(id);
    }
}
