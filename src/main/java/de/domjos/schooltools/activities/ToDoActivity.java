/*
 * Copyright (C) 2017-2019  Dominic Joas
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
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.ToDoAdapter;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.widgets.ToDoWidget;

/**
 * Activity For the ToDo-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class ToDoActivity extends AbstractActivity {
    private Spinner spToDoList;
    private ListView lvToDos;
    private FloatingActionButton cmdToDoAdd;
    private ToDoAdapter toDoAdapter;
    private ArrayAdapter<String> toDoListAdapter;
    private TextView lblState;
    private SeekBar sbState;
    private MenuItem menSolveToDo;
    private int intPosition = 0;

    public ToDoActivity() {
        super(R.layout.todo_activity);
    }

    @Override
    protected void initActions() {
        this.reloadToDoLists();
        this.reloadToDos();

        this.spToDoList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reloadToDos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                reloadToDos();
            }
        });

        this.lvToDos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(cmdToDoAdd.getVisibility()!=View.GONE) {
                    ToDo toDo = toDoAdapter.getItem(position);
                    if(toDo!=null) {
                        Intent intent = new Intent(getApplicationContext(), ToDoEntryActivity.class);
                        intent.putExtra("id", toDo.getID());
                        intent.putExtra("list", spToDoList.getSelectedItem().toString());
                        startActivityForResult(intent, 98);
                    }
                }
            }
        });

        this.lvToDos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menSolveToDo.setVisible(true);
                intPosition = position;
                return true;
            }
        });

        this.cmdToDoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToDoEntryActivity.class);
                intent.putExtra("id", 0);
                intent.putExtra("list", spToDoList.getSelectedItem().toString());
                startActivityForResult(intent, 98);
            }
        });

        this.sbState.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String percent = (100 / seekBar.getMax()) * seekBar.getProgress() + "%";
                lblState.setText(percent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        this.menSolveToDo = menu.findItem(R.id.menSolveToDo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_todo"));
                break;
            case R.id.menSolveToDo:
                ToDo toDo = toDoAdapter.getItem(intPosition);
                if(toDo!=null) {
                    toDo.setSolved(!toDo.isSolved());

                    String list = "";
                    for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                        for(ToDo tmp : toDoList.getToDos()) {
                            if(tmp.getID() == toDo.getID()) {
                                list = toDoList.getTitle();
                                break;
                            }
                        }
                    }
                    MainActivity.globals.getSqLite().insertOrUpdateToDo(toDo, list);
                    reloadToDos();
                }
                this.menSolveToDo.setVisible(false);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        try {
            if(resultCode==RESULT_OK) {
                if(requestCode==99) {
                    this.reloadToDoLists();
                }
                if(requestCode==98) {
                    this.reloadToDos();
                }
                Helper.sendBroadCast(ToDoActivity.this, ToDoWidget.class);
                this.menSolveToDo.setVisible(false);
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void reloadToDoLists() {
        this.toDoListAdapter.clear();
        this.toDoListAdapter.add("");
        for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
            this.toDoListAdapter.add(toDoList.getTitle());
        }
    }

    private void reloadToDos() {
        this.toDoAdapter.clear();
        if(!this.toDoListAdapter.isEmpty()) {
            if(this.spToDoList.getSelectedItem()!=null) {
                if(!this.spToDoList.getSelectedItem().toString().equals("")) {
                    for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("title='" + this.spToDoList.getSelectedItem().toString() + "'")) {
                        for(ToDo toDo : toDoList.getToDos()) {
                            this.toDoAdapter.add(toDo);
                        }
                    }
                    cmdToDoAdd.setVisibility(View.VISIBLE);
                } else {
                    for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                        for(ToDo toDo : toDoList.getToDos()) {
                            this.toDoAdapter.add(toDo);
                        }
                    }
                    cmdToDoAdd.setVisibility(View.GONE);
                }
            } else {
                for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                    for(ToDo toDo : toDoList.getToDos()) {
                        this.toDoAdapter.add(toDo);
                    }
                }
                cmdToDoAdd.setVisibility(View.GONE);
            }
        } else {
            cmdToDoAdd.setVisibility(View.GONE);
        }

        int max = 0, current = 0;
        for(int i = 0; i<=this.toDoAdapter.getCount()-1; i++) {
            ToDo toDo = this.toDoAdapter.getItem(i);
            if(toDo!=null) {
                max += toDo.getImportance();
                current += (toDo.isSolved() ? toDo.getImportance() : 0);
            }
        }
        sbState.setMax(max);
        sbState.setProgress(current);
    }

    @Override
    protected void initControls() {
        // init navigation_learning_card_group
        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (Helper.checkMenuID(item)) {
                    case R.id.navToDoList:
                        Intent intent = new Intent(getApplicationContext(), ToDoListActivity.class);
                        startActivityForResult(intent, 99);
                        break;
                    default:
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(listener);

        // init other controls
        this.cmdToDoAdd = this.findViewById(R.id.cmdToDoAdd);
        this.spToDoList = this.findViewById(R.id.spToDoList);
        this.toDoListAdapter = new ArrayAdapter<>(ToDoActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.spToDoList.setAdapter(this.toDoListAdapter);
        this.toDoListAdapter.notifyDataSetChanged();

        this.lvToDos = this.findViewById(R.id.lvToDos);
        this.toDoAdapter = new ToDoAdapter(ToDoActivity.this, R.layout.todo_item, new ArrayList<ToDo>());
        this.lvToDos.setAdapter(this.toDoAdapter);
        this.toDoAdapter.notifyDataSetChanged();

        this.lblState = this.findViewById(R.id.lblState);
        this.sbState = this.findViewById(R.id.sbState);
        this.sbState.setEnabled(false);
        this.sbState.setMax(100);
        this.sbState.setProgress(0);
    }
}
