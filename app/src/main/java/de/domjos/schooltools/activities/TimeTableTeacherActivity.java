/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Teacher-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class TimeTableTeacherActivity extends AbstractActivity {

    private SwipeRefreshDeleteList lvTeachers;
    private EditText txtTeacherFirstName, txtTeacherLastName, txtTeacherDescription;
    private BottomNavigationView navigation;
    private int currentID;

    private Validator validator;

    public TimeTableTeacherActivity() {
        super(R.layout.timetable_teacher_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        Helper.closeSoftKeyboard(TimeTableTeacherActivity.this);

        this.lvTeachers.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                Teacher teacher = (Teacher) listObject;
                if(teacher!=null) {
                    currentID = teacher.getID();
                    txtTeacherFirstName.setText(teacher.getFirstName());
                    txtTeacherLastName.setText(teacher.getLastName());
                    txtTeacherDescription.setText(teacher.getDescription());
                    navigation.getMenu().getItem(1).setEnabled(true);
                    navigation.getMenu().getItem(2).setEnabled(true);
                }
            }
        });

        this.lvTeachers.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadTeachers();
            }
        });

        this.lvTeachers.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                MainActivity.globals.getSqLite().deleteEntry("teachers", "ID", listObject.getID(), "");

                currentID = 0;
                navigation.getMenu().getItem(1).setEnabled(false);
                navigation.getMenu().getItem(2).setEnabled(false);
                controlFields(false, true);
                reloadTeachers();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_timetable"));
    }

    @Override
    protected void initControls() {
        // init BottomNavigation
        BottomNavigationView.OnNavigationItemSelectedListener navListener
                = item -> {
                    switch (Helper.checkMenuID(item)) {
                        case R.id.navTimeTableSubAdd:
                            currentID = 0;
                            controlFields(true, true);
                            return true;
                        case R.id.navTimeTableSubEdit:
                            controlFields(true, false);
                            return true;
                        case R.id.navTimeTableSubDelete:
                            MainActivity.globals.getSqLite().deleteEntry("teachers", "ID", currentID, "");

                            currentID = 0;
                            navigation.getMenu().getItem(1).setEnabled(false);
                            navigation.getMenu().getItem(2).setEnabled(false);
                            controlFields(false, true);
                            reloadTeachers();
                            return true;
                        case R.id.navTimeTableSubSave:
                            if(validator.getState()) {
                                Teacher teacher = new Teacher();
                                teacher.setID(currentID);
                                teacher.setFirstName(txtTeacherFirstName.getText().toString());
                                teacher.setLastName(txtTeacherLastName.getText().toString());
                                teacher.setDescription(txtTeacherDescription.getText().toString());

                                if(validateName(teacher) || currentID!=0) {
                                    MainActivity.globals.getSqLite().insertOrUpdateTeacher(teacher);

                                    currentID = 0;
                                    navigation.getMenu().getItem(1).setEnabled(false);
                                    navigation.getMenu().getItem(2).setEnabled(false);
                                    controlFields(false, true);
                                    reloadTeachers();
                                } else {
                                    MessageHelper.printMessage(getString(R.string.message_validator_teachers), R.mipmap.ic_launcher_round, TimeTableTeacherActivity.this);
                                }
                            }
                            return true;
                        case R.id.navTimeTableSubCancel:
                            currentID = 0;
                            navigation.getMenu().getItem(1).setEnabled(false);
                            navigation.getMenu().getItem(2).setEnabled(false);
                            controlFields(false, true);
                            return true;
                    }
                    return false;
                };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(navListener);

        // init controls
        this.txtTeacherFirstName = this.findViewById(R.id.txtTeacherFirstName);
        this.txtTeacherLastName = this.findViewById(R.id.txtTeacherLastName);
        this.txtTeacherDescription = this.findViewById(R.id.txtTeacherDescription);
        this.lvTeachers = this.findViewById(R.id.lvTeacher);
        this.controlFields(false, false);
        this.reloadTeachers();
        this.navigation.getMenu().getItem(1).setEnabled(false);
        this.navigation.getMenu().getItem(2).setEnabled(false);
    }

    private boolean validateName(Teacher teacher) {
        List<Teacher> teacherList = MainActivity.globals.getSqLite().getTeachers("");
        for(Teacher currentTeacher : teacherList) {
            if(!teacher.getFirstName().isEmpty()) {
                if(
                    teacher.getLastName().equals(currentTeacher.getLastName()) &&
                    teacher.getFirstName().equals(currentTeacher.getFirstName())) {

                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtTeacherLastName, 2, 500);
    }

    private void controlFields(boolean editMode, boolean reset) {
        this.txtTeacherFirstName.setEnabled(editMode);
        this.txtTeacherLastName.setEnabled(editMode);
        this.txtTeacherDescription.setEnabled(editMode);
        this.lvTeachers.setEnabled(!editMode);

        Helper.showMenuControls(editMode, this.navigation);

        if(reset) {
            this.txtTeacherFirstName.setText("");
            this.txtTeacherLastName.setText("");
            this.txtTeacherDescription.setText("");
        }
    }

    private void reloadTeachers() {
        this.lvTeachers.getAdapter().clear();
        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("")) {
            this.lvTeachers.getAdapter().add(teacher);
        }
    }
}
