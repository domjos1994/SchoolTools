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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.spotlight.OnBoardingHelper;
import de.domjos.schooltools.widgets.ToDoWidget;

/**
 * Activity For the screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class ToDoActivity extends AbstractActivity {
    private Spinner spToDoList;
    private SwipeRefreshDeleteList lvToDos;
    private FloatingActionButton cmdToDoAdd;
    private ArrayAdapter<String> toDoListAdapter;
    private TextView lblState;
    private SeekBar sbState;

    public ToDoActivity() {
        super(R.layout.todo_activity, MainActivity.globals.getSqLite().getSetting("background"));
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

        this.lvToDos.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                if(cmdToDoAdd.getVisibility()!=View.GONE) {
                    ToDo toDo = (ToDo) listObject;
                    if(toDo!=null) {
                        Intent intent = new Intent(getApplicationContext(), ToDoEntryActivity.class);
                        intent.putExtra("id", toDo.getID());
                        intent.putExtra("list", spToDoList.getSelectedItem().toString());
                        startActivityForResult(intent, 98);
                    }
                }
            }
        });

        this.lvToDos.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadToDos();
            }
        });

        this.lvToDos.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("toDos", "ID", listObject.getID(), "");
                reloadToDoLists();
            }
        });

        this.cmdToDoAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ToDoEntryActivity.class);
            intent.putExtra("id", 0);
            intent.putExtra("list", spToDoList.getSelectedItem().toString());
            startActivityForResult(intent, 98);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menHelp) {
            return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_todo"));
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menSolveToDo) {
            int intPosition = 0;
            ToDo toDo = (ToDo) lvToDos.getAdapter().getItem(intPosition);
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
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            if(resultCode==RESULT_OK) {
                if(requestCode==99) {
                    this.reloadToDoLists();
                }
                if(requestCode==98) {
                    this.reloadToDos();
                }
                Helper.sendBroadCast(ToDoActivity.this, ToDoWidget.class);
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
        this.lvToDos.getAdapter().clear();
        if(!this.toDoListAdapter.isEmpty()) {
            if(this.spToDoList.getSelectedItem()!=null) {
                if(!this.spToDoList.getSelectedItem().toString().equals("")) {
                    for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("title='" + this.spToDoList.getSelectedItem().toString() + "'")) {
                        for(ToDo toDo : toDoList.getToDos()) {
                            this.lvToDos.getAdapter().add(toDo);
                        }
                    }
                    cmdToDoAdd.show();
                } else {
                    for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                        for(ToDo toDo : toDoList.getToDos()) {
                            this.lvToDos.getAdapter().add(toDo);
                        }
                    }
                    cmdToDoAdd.hide();
                }
            } else {
                for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                    for(ToDo toDo : toDoList.getToDos()) {
                        this.lvToDos.getAdapter().add(toDo);
                    }
                }
                cmdToDoAdd.hide();
            }
        } else {
            cmdToDoAdd.hide();
        }

        int max = 0, current = 0;
        for(int i = 0; i<=this.lvToDos.getAdapter().getItemCount()-1; i++) {
            ToDo toDo = (ToDo) this.lvToDos.getAdapter().getItem(i);
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
        OnNavigationItemSelectedListener listener = (item) -> {
            if(Helper.checkMenuID(item)==R.id.navToDoList) {
                Intent intent = new Intent(getApplicationContext(), ToDoListActivity.class);
                startActivityForResult(intent, 99);
            }
            return false;
        };
        navigation.setOnNavigationItemSelectedListener(listener);

        // init other controls
        this.cmdToDoAdd = this.findViewById(R.id.cmdToDoAdd);
        this.spToDoList = this.findViewById(R.id.spToDoList);
        this.toDoListAdapter = new ArrayAdapter<>(ToDoActivity.this, R.layout.spinner_item, new ArrayList<>());
        this.spToDoList.setAdapter(this.toDoListAdapter);
        this.toDoListAdapter.notifyDataSetChanged();

        this.lvToDos = this.findViewById(R.id.lvToDos);
        this.lvToDos.setContextMenu(R.menu.ctx_todo);

        this.lblState = this.findViewById(R.id.lblState);
        this.sbState = this.findViewById(R.id.sbState);
        this.sbState.setEnabled(false);
        this.sbState.setMax(100);
        this.sbState.setProgress(0);

        OnBoardingHelper.tutorialToDo(this, this.spToDoList, this.lvToDos);
    }
}
