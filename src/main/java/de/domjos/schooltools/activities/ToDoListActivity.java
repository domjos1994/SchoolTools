/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.ToDoListsAdapter;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the List-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDoListActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    private ListView lvToDoLists;
    private EditText txtToDoListTitle, txtToDoListDescription, txtToDoListDate;

    private int currentID;
    private ToDoListsAdapter toDoListsAdapter;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list_activity);
        this.initControls();
        this.initValidator();
        this.reloadItems();
        this.changeControls(false, true, false);
        this.getListFromExtra();

        this.lvToDoLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setValues(toDoListsAdapter.getItem(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeControls(boolean editMode, boolean reset, boolean selected) {
        this.lvToDoLists.setEnabled(!editMode);
        this.txtToDoListTitle.setEnabled(editMode);
        this.txtToDoListDescription.setEnabled(editMode);
        this.txtToDoListDate.setEnabled(editMode);
        this.navigation.getMenu().getItem(0).setEnabled(!editMode);
        this.navigation.getMenu().getItem(1).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(2).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(3).setEnabled(editMode);
        this.navigation.getMenu().getItem(4).setEnabled(editMode);

        if(reset) {
            this.currentID = 0;
            this.txtToDoListTitle.setText("");
            this.txtToDoListDescription.setText("");
            this.txtToDoListDate.setText("");
        }
    }

    private void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(this.txtToDoListTitle, 3, 500);
        this.validator.addDateValidator(this.txtToDoListDate);
    }

    private void reloadItems() {
        this.toDoListsAdapter.clear();
        for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
            this.toDoListsAdapter.add(toDoList);
        }
    }

    private void setValues(ToDoList toDoList) {
        if(toDoList!=null) {
            Log.v("test", toDoList.getTitle());
            currentID = toDoList.getID();
            txtToDoListTitle.setText(toDoList.getTitle());
            txtToDoListDescription.setText(toDoList.getDescription());
            if(toDoList.getListDate()!=null) {
                txtToDoListDate.setText(Converter.convertDateToString(toDoList.getListDate()));
            }
            changeControls(false, false, true);
        }
    }

    private void getListFromExtra() {
        int id = this.getIntent().getIntExtra("id", 0);
        Log.v("test", String.valueOf(id));
        List<ToDoList> toDoLists = MainActivity.globals.getSqLite().getToDoLists("id=" + id);
        if(toDoLists!=null) {
            if(!toDoLists.isEmpty()) {
                this.setValues(toDoLists.get(0));
            }
        }
    }

    private void initControls() {
        // init navigation
        this.navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navTimeTableSubAdd:
                        changeControls(true, true, false);
                        break;
                    case R.id.navTimeTableSubEdit:
                        changeControls(true, false, false);
                        break;
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("toDoLists", "ID", currentID, "");
                        reloadItems();
                        changeControls(false, true, false);
                        break;
                    case R.id.navTimeTableSubCancel:
                        changeControls(false, true, false);
                        break;
                    case R.id.navTimeTableSubSave:
                        try {
                            if(validator.getState()) {
                                ToDoList toDoList = new ToDoList();
                                toDoList.setID(currentID);
                                toDoList.setTitle(txtToDoListTitle.getText().toString());
                                toDoList.setDescription(txtToDoListDescription.getText().toString());
                                if(!txtToDoListDate.getText().toString().equals("")) {
                                    toDoList.setListDate(Converter.convertStringToDate(txtToDoListDate.getText().toString()));
                                }
                                MainActivity.globals.getSqLite().insertOrUpdateToDoList(toDoList);
                                reloadItems();
                                changeControls(false, true, false);
                            }
                        } catch (Exception ex) {
                            Helper.printException(getApplicationContext(), ex);
                        }
                        break;
                    default:
                }
                return false;
            }
        };
        this.navigation.setOnNavigationItemSelectedListener(listener);

        // init other controls
        this.lvToDoLists = this.findViewById(R.id.lvToDoLists);
        this.toDoListsAdapter = new ToDoListsAdapter(this.getApplicationContext(), R.layout.todo_list_item, new ArrayList<ToDoList>());
        this.lvToDoLists.setAdapter(this.toDoListsAdapter);
        this.toDoListsAdapter.notifyDataSetChanged();

        this.txtToDoListTitle = this.findViewById(R.id.txtToDoListTitle);
        this.txtToDoListDescription = this.findViewById(R.id.txtToDoListDescription);
        this.txtToDoListDate = this.findViewById(R.id.txtToDoListDate);
    }
}