/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.*;
import de.domjos.schooltoolslib.SearchItem;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.mark.Test;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.schooltools.screenWidgets.QuickQueryScreenWidget;
import de.domjos.schooltools.services.AuthenticatorService;
import de.domjos.schooltools.services.MemoryService;
import de.domjos.schooltools.settings.GeneralSettings;
import de.domjos.schooltools.settings.Globals;
import de.domjos.schooltools.settings.MarkListSettings;
import de.domjos.schooltools.settings.UserSettings;
import de.domjos.schooltools.screenWidgets.ButtonScreenWidget;
import de.domjos.schooltools.screenWidgets.ImportantToDoScreenWidget;
import de.domjos.schooltools.screenWidgets.SavedMarkListsScreenWidget;
import de.domjos.schooltools.screenWidgets.SavedTimeTablesScreenWidget;
import de.domjos.schooltools.screenWidgets.TaggedBookMarksScreenWidget;
import de.domjos.schooltools.screenWidgets.TimeTableEventScreenWidget;
import de.domjos.schooltools.screenWidgets.TodayScreenWidget;
import de.domjos.schooltools.screenWidgets.Top5NotesScreenWidget;

import static android.provider.CalendarContract.AUTHORITY;

/**
 * Activity For the Main-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class MainActivity extends AbstractActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public static final Globals globals = new Globals();
    public static final String CHANNEL_ID = "SchoolTools";

    private ImageButton cmdRefresh;
    private SearchView cmdSearch;
    private ListView lvSearchResults;
    private SearchAdapter searchAdapter;
    private CountDownTimer countDownTimer;

    private TodayScreenWidget todayScreenWidget;
    private ButtonScreenWidget buttonScreenWidget;
    private Top5NotesScreenWidget top5NotesScreenWidget;
    private ImportantToDoScreenWidget importantToDoScreenWidget;
    private SavedTimeTablesScreenWidget savedTimeTablesScreenWidget;
    private SavedMarkListsScreenWidget savedMarkListsScreenWidget;
    private TimeTableEventScreenWidget timeTableEventScreenWidget;
    private TaggedBookMarksScreenWidget taggedBookMarksScreenWidget;
    private QuickQueryScreenWidget quickQueryScreenWidget;

    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    protected void initActions() {
        MainActivity.globals.setUserSettings(new UserSettings(this.getApplicationContext()));
        MainActivity.globals.setGeneralSettings(new GeneralSettings(this.getApplicationContext()));
        this.resetDatabase();
        this.initDatabase();
        this.initServices();
        Helper.setBackgroundToActivity(this);
        Helper.setBackgroundAppBarToActivity(this.navigationView, MainActivity.this);
        Helper.createChannel(this.getApplicationContext());

        this.buttonScreenWidget.init();
        this.buttonScreenWidget.loadButtons();
        this.buttonScreenWidget.hideButtons();
        this.todayScreenWidget.init();
        this.todayScreenWidget.addEvents();
        this.top5NotesScreenWidget.init();
        this.top5NotesScreenWidget.addNotes();
        this.importantToDoScreenWidget.init();
        this.importantToDoScreenWidget.addToDos();
        this.savedTimeTablesScreenWidget.init();
        this.savedTimeTablesScreenWidget.initSavedTimeTables();
        this.savedMarkListsScreenWidget.init();
        this.savedMarkListsScreenWidget.addMarkLists();
        this.timeTableEventScreenWidget.init();
        this.taggedBookMarksScreenWidget.init();
        this.quickQueryScreenWidget.init();
        this.quickQueryScreenWidget.reloadQueries();
        MainActivity.globals.getUserSettings().openStartModule(this.buttonScreenWidget, MainActivity.this);

        this.hideWidgets();

        this.deleteMemoriesFromPast();
        this.deleteToDosFromPast();
        this.setSavedValuesForWidgets();
        this.openWhatsNew();

        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTableEventScreenWidget.initCurrentTimeTableEvent();
            }

            @Override
            public void onFinish() {
                countDownTimer.start();
            }
        }.start();



        this.cmdRefresh.setOnClickListener(v -> {
            todayScreenWidget.addEvents();
            top5NotesScreenWidget.addNotes();
            importantToDoScreenWidget.addToDos();
            savedMarkListsScreenWidget.addMarkLists();
            MessageHelper.printMessage(getString(R.string.main_refreshSuccessFully), R.mipmap.ic_launcher_round, MainActivity.this);
        });

        this.cmdSearch.setOnCloseListener(() -> {
            lvSearchResults.setVisibility(View.GONE);
            return false;
        });

        this.cmdSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addSearchItems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addSearchItems(newText);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // settings
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 98) {
            if (MainActivity.globals.getUserSettings().isWhatsNew()) {
                WhatsNewActivity.resetShown("whats_new", this.getApplicationContext());
                MainActivity.globals.getUserSettings().setWhatsNew(false);

                Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
                intent.putExtra(WhatsNewActivity.isWhatsNew, true);
                intent.putExtra(WhatsNewActivity.INFO_PARAM, "whats_new_info");
                this.startActivity(intent);
            }

            this.resetDatabase();
            this.initDatabase();
            this.cmdRefresh.callOnClick();
            this.buttonScreenWidget.hideButtons();
            MainActivity.globals.getUserSettings().hideMenuItems(this.navigationView);
            this.timeTableEventScreenWidget.initCurrentTimeTableEvent();
            MainActivity.globals.getUserSettings().openStartModule(this.buttonScreenWidget, MainActivity.this);
            this.hideWidgets();
            this.initSyncServices();
            Helper.setBackgroundToActivity(this);
            Helper.setBackgroundAppBarToActivity(this.navigationView, MainActivity.this);
            this.quickQueryScreenWidget.reloadQueries();
        }
    }

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MainActivity.globals.getUserSettings().hideMenuItems(this.navigationView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.menMainExport:
                intent = new Intent(this.getApplicationContext(), ApiActivity.class);
                break;
            case R.id.menMainSettings:
                intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
                break;
            case R.id.menMainHelp:
                intent = new Intent(this.getApplicationContext(), HelpActivity.class);
                break;
            default:
                intent = null;
        }

        if(intent!=null) {
            this.startActivityForResult(intent, 98);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (Helper.checkMenuID(item)) {
            case R.id.navMainMarkList:
                intent = null;
                this.buttonScreenWidget.openMarkListIntent();
                break;
            case R.id.navMainCalculateMark:
                intent = new Intent(this.getApplicationContext(), MarkActivity.class);
                break;
            case R.id.navMainTimeTable:
                intent = new Intent(this.getApplicationContext(), TimeTableActivity.class);
                break;
            case R.id.navMainNotes:
                intent = new Intent(this.getApplicationContext(), NoteActivity.class);
                break;
            case R.id.navMainTimer:
                intent = new Intent(this.getApplicationContext(), TimerActivity.class);
                break;
            case R.id.navMainToDo:
                intent = new Intent(this.getApplicationContext(), ToDoActivity.class);
                break;
            case R.id.navMainLearningCards:
                intent = new Intent(this.getApplicationContext(), LearningCardOverviewActivity.class);
                break;
            case R.id.navMainBookMarks:
                intent = new Intent(this.getApplicationContext(), BookmarkActivity.class);
                break;
            case R.id.navMainTrafficLights:
                intent = new Intent(this.getApplicationContext(), TrafficLightActivity.class);
                break;
            default:
                intent = null;
        }

        if(intent!=null) {
            this.startActivityForResult(intent, 99);
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initServices() {
        try {
            if(MainActivity.globals.getUserSettings().isNotificationsShown()) {
                Helper.initRepeatingService(MainActivity.this, MemoryService.class,  8 * 60 * 60 * 1000);
            }
            if(MainActivity.globals.getUserSettings().isSyncCalendarTurnOn() && MainActivity.globals.getUserSettings().isSynContactsTurnOn()) {
                if(
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.READ_CALENDAR) &&
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.WRITE_CALENDAR) &&
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.WRITE_CONTACTS) &&
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                    this.initSyncServices();
                }
            } else if(MainActivity.globals.getUserSettings().isSyncCalendarTurnOn()) {
                if(
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.READ_CALENDAR) &&
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.WRITE_CALENDAR)) {
                    this.initSyncServices();
                }
            } else if(MainActivity.globals.getUserSettings().isSynContactsTurnOn()) {
                if(
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.READ_CONTACTS) &&
                        Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                    this.initSyncServices();
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR) {
                this.initSyncServices();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(!this.cmdSearch.isInEditMode()) {
            this.lvSearchResults.setVisibility(View.GONE);
        }
        Helper.setBackgroundToActivity(this);
    }

    private void initSyncServices() {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("PREF_SETUP_COMPLETE", false);

        Account account = AuthenticatorService.GetAccount(this.getApplicationContext(), "de.domjos.schooltools.account");
        AccountManager accountManager = (AccountManager) this.getApplicationContext().getSystemService(Context.ACCOUNT_SERVICE);
        if(Objects.requireNonNull(accountManager).addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            Bundle bundle = new Bundle();
            bundle.putString("name", MainActivity.globals.getUserSettings().getSyncCalendarName());
            ContentResolver.addPeriodicSync(account, AUTHORITY, bundle, 60 * 1000);
            newAccount = true;
        }
        if (newAccount || !setupComplete) {
            PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit().putBoolean("PREF_SETUP_COMPLETE", true).apply();
        }
    }



    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void initControls() {
        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // init drawer
        this.drawerLayout = this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        this.cmdRefresh = this.findViewById(R.id.cmdRefresh);
        this.cmdSearch = this.findViewById(R.id.cmdSearch);

        // init navigationView
        this.navigationView = this.findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        // init other controls
        this.buttonScreenWidget = new ButtonScreenWidget(this.findViewById(R.id.tblButtons), MainActivity.this);
        this.todayScreenWidget = new TodayScreenWidget(this.findViewById(R.id.llToday), MainActivity.this);
        this.top5NotesScreenWidget = new Top5NotesScreenWidget(this.findViewById(R.id.llCurrentNotes), MainActivity.this);
        this.importantToDoScreenWidget = new ImportantToDoScreenWidget(this.findViewById(R.id.llImportantToDos), MainActivity.this);
        this.savedTimeTablesScreenWidget = new SavedTimeTablesScreenWidget(this.findViewById(R.id.llSavedTimeTables), MainActivity.this);
        this.savedMarkListsScreenWidget = new SavedMarkListsScreenWidget(this.findViewById(R.id.llSavedMarklist), MainActivity.this);
        this.timeTableEventScreenWidget = new TimeTableEventScreenWidget(this.findViewById(R.id.llTodayCurrentTimeTable), MainActivity.this);
        this.taggedBookMarksScreenWidget = new TaggedBookMarksScreenWidget(this.findViewById(R.id.llTaggedBookMarks), MainActivity.this);
        this.quickQueryScreenWidget = new QuickQueryScreenWidget(this.findViewById(R.id.llLearningCardsQuickQuery), MainActivity.this);

        this.lvSearchResults = this.findViewById(R.id.lvSearchResults);
        this.searchAdapter = new SearchAdapter(MainActivity.this);
        this.lvSearchResults.setAdapter(this.searchAdapter);
        this.searchAdapter.notifyDataSetChanged();
    }

    private void initDatabase() {
        SQLite sqLite = new SQLite(this.getApplicationContext(), "schoolTools.db", MainActivity.globals.getGeneralSettings().getCurrentVersionCode(MainActivity.this));
        MainActivity.globals.setSqLite(sqLite);
        super.setBackground(MainActivity.globals.getSqLite().getSetting("background"));
    }

    private void addSearchItems(String search) {
        this.searchAdapter.clear();

        for(MarkListSettings settings : MainActivity.globals.getSqLite().getMarkListSearch(search)) {
            this.searchAdapter.add(new SearchItem(settings.getId(), settings.getTitle(), this.getString(R.string.main_nav_mark_list)));
        }

        for(SchoolYear schoolYear : MainActivity.globals.getSqLite().getSchoolYears("")) {
            for(Test test : schoolYear.getTests()) {
                if(test.getTitle().toLowerCase().contains(search.toLowerCase())) {
                    this.searchAdapter.add(new SearchItem(test.getId(), test.getTitle(), this.getString(R.string.mark_test)));
                }
            }
        }

        for(Note note : MainActivity.globals.getSqLite().getNotes("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(note.getId(), note.getTitle(), this.getString(R.string.main_nav_notes)));
        }

        for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(toDoList.getId(), toDoList.getTitle(), this.getString(R.string.todo_list)));
        }

        for(ToDo toDo : MainActivity.globals.getSqLite().getToDos("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(toDo.getId(), toDo.getTitle(), this.getString(R.string.main_nav_todo)));
        }

        for(TimeTable timeTable : MainActivity.globals.getSqLite().getTimeTables("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(timeTable.getId(), timeTable.getTitle(), this.getString(R.string.main_nav_timetable)));
        }

        for(TimerEvent timerEvent : MainActivity.globals.getSqLite().getTimerEvents("title like '%" + search + "%'")) {
            SearchItem searchItem = new SearchItem(timerEvent.getId(), timerEvent.getTitle(), this.getString(R.string.main_nav_timer));
            searchItem.setExtra(ConvertHelper.convertDateToString(timerEvent.getEventDate(), this.getApplicationContext()));
            this.searchAdapter.add(searchItem);
        }


        for(Subject subject : MainActivity.globals.getSqLite().getSubjects("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(subject.getId(), subject.getTitle(), this.getString(R.string.timetable_lesson)));
        }

        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("lastName like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(teacher.getId(), teacher.getLastName(), this.getString(R.string.timetable_teacher)));
        }

        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(schoolClass.getId(), schoolClass.getTitle(), this.getString(R.string.timetable_class)));
        }

        for(Year year : MainActivity.globals.getSqLite().getYears("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(year.getId(), year.getTitle(), this.getString(R.string.mark_year)));
        }


        if(this.searchAdapter.isEmpty()) {
            this.lvSearchResults.setVisibility(View.GONE);
        } else {
            this.lvSearchResults.setVisibility(View.VISIBLE);
        }
    }

    private void hideWidgets() {
        this.buttonScreenWidget.setVisibility(false);
        this.todayScreenWidget.setVisibility(false);
        this.top5NotesScreenWidget.setVisibility(false);
        this.importantToDoScreenWidget.setVisibility(false);
        this.savedTimeTablesScreenWidget.setVisibility(false);
        this.savedMarkListsScreenWidget.setVisibility(false);
        this.timeTableEventScreenWidget.setVisibility(false);
        this.taggedBookMarksScreenWidget.setVisibility(false);
        this.quickQueryScreenWidget.setVisibility(false);

        for(String item : MainActivity.globals.getUserSettings().getStartWidgets()) {
            if(item.equals(this.getString(R.string.main_nav_buttons))) {
                this.buttonScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_today))) {
                this.todayScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_today_timetable))) {
                this.timeTableEventScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_savedTimeTables))) {
                this.savedTimeTablesScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_top5Notes))) {
                this.top5NotesScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_importantToDos))) {
                this.importantToDoScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_savedMarkList))) {
                this.savedMarkListsScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_taggedBookMarks))) {
                this.taggedBookMarksScreenWidget.setVisibility(true);
            }
            if(item.equals(this.getString(R.string.main_quickQuery))) {
                this.quickQueryScreenWidget.setVisibility(true);
            }
        }
    }

    private void deleteMemoriesFromPast() {
        if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
            for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
                try {
                    if (Helper.compareDateWithCurrentDate(ConvertHelper.convertStringToDate(memory.getDate(), this.getApplicationContext()))) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    }
                } catch (Exception ex) {
                    MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
                }
            }
        }
    }

    private void deleteToDosFromPast() {
        if(MainActivity.globals.getUserSettings().isDeleteToDoAfterDeadline()) {
            for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                try {
                    if(Helper.compareDateWithCurrentDate(toDoList.getListDate())) {
                        MainActivity.globals.getSqLite().deleteEntry("toDoLists", "ID=" + toDoList.getId());
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
                }
            }
        }
    }

    private void openWhatsNew() {
        Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
        intent.putExtra(WhatsNewActivity.isWhatsNew, true);
        intent.putExtra(WhatsNewActivity.INFO_PARAM, "whats_new_info");
        this.startActivity(intent);
    }

    private void resetDatabase() {
        try {
            if(MainActivity.globals.getUserSettings().isGeneralResetDatabase()) {
                this.getApplicationContext().deleteDatabase("schoolTools.db");
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }
    }

    private void setSavedValuesForWidgets() {
        this.savedMarkListsScreenWidget.getSavedValue(MainActivity.globals.getGeneralSettings().getWidgetMarkListSpinner());
        this.savedTimeTablesScreenWidget.getSavedValues(MainActivity.globals.getGeneralSettings().getWidgetTimetableSpinner());
    }
}
