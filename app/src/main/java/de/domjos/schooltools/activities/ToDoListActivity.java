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
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;

/**
 * Activity For the List-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class ToDoListActivity extends AbstractActivity {
    private BottomNavigationView navigation;

    private SwipeRefreshDeleteList lvToDoLists;
    private EditText txtToDoListTitle, txtToDoListDescription, txtToDoListDate;

    private int currentID;
    private Validator validator;

    public ToDoListActivity() {
        super(R.layout.todo_list_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.reloadItems();
        this.changeControls(false, true, false);
        this.getListFromExtra();

        this.lvToDoLists.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                setValues((ToDoList) listObject);
            }
        });

        this.lvToDoLists.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadItems();
            }
        });

        this.lvToDoLists.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("toDoLists", "ID", listObject.getID(), "");
                reloadItems();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
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

    @Override
    protected void initValidator() {
        this.validator = new Validator(this.getApplicationContext(), R.mipmap.ic_launcher_round);
        this.validator.addLengthValidator(this.txtToDoListTitle, 3, 500);
        this.validator.addDateValidator(this.txtToDoListDate);
    }

    private void reloadItems() {
        this.lvToDoLists.getAdapter().clear();
        for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
            this.lvToDoLists.getAdapter().add(toDoList);
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

    @Override
    protected void initControls() {
        // init navigation_learning_card_group
        this.navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener listener = (item) -> {
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
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ToDoListActivity.this);
                    }
                    break;
                default:
            }
            return false;
        };
        this.navigation.setOnNavigationItemSelectedListener(listener);

        // init other controls
        this.lvToDoLists = this.findViewById(R.id.lvToDoLists);

        this.txtToDoListTitle = this.findViewById(R.id.txtToDoListTitle);
        this.txtToDoListDescription = this.findViewById(R.id.txtToDoListDescription);
        this.txtToDoListDate = this.findViewById(R.id.txtToDoListDate);
    }
}
