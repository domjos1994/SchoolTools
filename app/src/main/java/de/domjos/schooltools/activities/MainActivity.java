
/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.EventAdapter;
import de.domjos.schooltools.adapter.MarkListAdapter;
import de.domjos.schooltools.adapter.NoteAdapter;
import de.domjos.schooltools.adapter.SearchAdapter;
import de.domjos.schooltools.core.SearchItem;
import de.domjos.schooltools.core.marklist.de.GermanListWithCrease;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.core.model.mark.SchoolYear;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Log4JHelper;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.schooltools.services.MemoryService;
import de.domjos.schooltools.settings.GeneralSettings;
import de.domjos.schooltools.settings.Globals;
import de.domjos.schooltools.settings.MarkListSettings;
import de.domjos.schooltools.settings.UserSettings;

/**
 * Activity For the Main-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public static Globals globals = new Globals();
    public static UserSettings settings;
    static GeneralSettings generals;

    private TableRow trMarkList, trMark, trTimeTable, trNotes, trTimer, trTodo, trExport, trSettings, trHelp;
    private RelativeLayout llToday, llCurrentNotes, llSavedMarkList;
    private ImageButton cmdRefresh;
    private SearchView cmdSearch;
    private ListView lvCurrentNotes, lvSearchResults;
    private Spinner cmbSavedMarkList;
    private TableLayout tblButtons;
    private EventAdapter eventAdapter;
    private NoteAdapter noteAdapter;
    private SearchAdapter searchAdapter;
    private MarkListAdapter markListAdapter;
    private ArrayAdapter<String> savedMarkListAdapter;
    private boolean firstUse, versionChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        Log4JHelper.configure(MainActivity.this);
        MainActivity.settings = new UserSettings(this.getApplicationContext());
        MainActivity.generals = new GeneralSettings(this.getApplicationContext());
        this.addGeneralSettings(this.getString(R.string.appPhase), Float.parseFloat(this.getString(R.string.appVersion)));
        this.initControls();
        this.initDatabase();
        this.initServices();
        this.hideButtons();
        this.openStartModule();
        this.hideWidgets();
        this.addEvents();
        this.addNotes();
        this.addMarkLists();
        this.deleteMemoriesFromPast();
        this.openWhatsNew();



        this.trMarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MarkListActivity.class);
                startActivity(intent);
            }
        });

        this.trMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MarkActivity.class);
                startActivity(intent);
            }
        });

        this.trTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
                startActivity(intent);
            }
        });

        this.trNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(intent);
            }
        });

        this.trTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(intent);
            }
        });

        this.trTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToDoActivity.class);
                startActivity(intent);
            }
        });

        this.trExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApiActivity.class);
                startActivity(intent);
            }
        });

        this.trSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        this.trHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        this.lvCurrentNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteAdapter.getItem(position);
                if(note!=null) {
                    Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                    intent.putExtra("ID", note.getID());
                    startActivity(intent);
                }
            }
        });

        this.cmbSavedMarkList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MarkListSettings settings = MainActivity.globals.getSqLite().getMarkList(savedMarkListAdapter.getItem(position));
                calculateSelectedMarkList(settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        this.cmdRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvents();
                addNotes();
                addMarkLists();
                Helper.createToast(getApplicationContext(), getString(R.string.main_refreshSuccessFully));
            }
        });

        this.cmdSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                lvSearchResults.setVisibility(View.GONE);
                return false;
            }
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
        if (requestCode == 98) {
            this.hideButtons();
            this.hideMenuItems();
            this.openStartModule();
            this.hideWidgets();
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
        this.hideMenuItems();
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
            case R.id.menMainWhatsNew:
                intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
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
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.navMainMarkList:
                intent = new Intent(this.getApplicationContext(), MarkListActivity.class);
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
        if(MainActivity.settings.isNotificationsShown()) {
            // init Memory Service
            Intent intent = new Intent(this.getApplicationContext(), MemoryService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this.getApplicationContext(),  0, intent, 0);

            // init frequently
            AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            long frequency= 8 * 60 * 60 * 1000;
            assert alarmManager != null;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), frequency, pendingIntent);
        }
    }

    private void initControls() {
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
        this.tblButtons = this.findViewById(R.id.tblButtons);
        this.trMarkList = this.findViewById(R.id.trMarkList);
        this.trMark = this.findViewById(R.id.trMark);
        this.trTimeTable = this.findViewById(R.id.trTimeTable);
        this.trNotes = this.findViewById(R.id.trNote);
        this.trTimer = this.findViewById(R.id.trTimer);
        this.trTodo = this.findViewById(R.id.trToDo);
        this.trExport = this.findViewById(R.id.trExport);
        this.trSettings = this.findViewById(R.id.trSettings);
        this.trHelp = this.findViewById(R.id.trHelp);

        this.llToday = this.findViewById(R.id.llToday);
        ListView lvEvents = this.findViewById(R.id.lvEvents);
        this.eventAdapter = new EventAdapter(this.getApplicationContext(), R.layout.main_today_event, new ArrayList<Map.Entry<String, String>>());
        lvEvents.setAdapter(this.eventAdapter);
        this.eventAdapter.notifyDataSetChanged();

        this.llCurrentNotes = this.findViewById(R.id.llCurrentNotes);
        this.lvCurrentNotes = this.findViewById(R.id.lvNotes);
        this.noteAdapter = new NoteAdapter(this.getApplicationContext(), R.layout.note_item, new ArrayList<Note>());
        this.lvCurrentNotes.setAdapter(this.noteAdapter);
        this.noteAdapter.notifyDataSetChanged();

        this.llSavedMarkList = this.findViewById(R.id.llSavedMarklist);

        this.cmbSavedMarkList = this.findViewById(R.id.cmbSavedMarklist);
        this.savedMarkListAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.cmbSavedMarkList.setAdapter(this.savedMarkListAdapter);
        this.savedMarkListAdapter.notifyDataSetChanged();

        this.lvSearchResults = this.findViewById(R.id.lvSearchResults);
        this.searchAdapter = new SearchAdapter(this.getApplicationContext());
        this.lvSearchResults.setAdapter(this.searchAdapter);
        this.searchAdapter.notifyDataSetChanged();

        ListView lvMarkList = this.findViewById(R.id.lvMarklist);
        this.markListAdapter = new MarkListAdapter(this.getApplicationContext(), R.layout.marklist_item, new ArrayList<Map.Entry<Double, Double>>());
        lvMarkList.setAdapter(this.markListAdapter);
        this.markListAdapter.notifyDataSetChanged();

        lvMarkList.setOnTouchListener(this.addOnTouchListenerForScrolling());
        lvCurrentNotes.setOnTouchListener(this.addOnTouchListenerForScrolling());
        lvEvents.setOnTouchListener(this.addOnTouchListenerForScrolling());
    }



    private View.OnTouchListener addOnTouchListenerForScrolling() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        };
    }

    private void initDatabase() {
        SQLite sqLite = new SQLite(this.getApplicationContext(), "schoolTools.db", 1);
        MainActivity.globals.setSqLite(sqLite);
    }

    private void hideButtons() {
        this.trMarkList.setVisibility(View.GONE);
        this.trMark.setVisibility(View.GONE);
        this.trTimeTable.setVisibility(View.GONE);
        this.trNotes.setVisibility(View.GONE);
        this.trTimer.setVisibility(View.GONE);
        this.trTodo.setVisibility(View.GONE);

        Set<String> modules = settings.getShownModule(this.getApplicationContext());
        for(String content : modules) {
            this.hideButton(content, R.string.main_nav_mark_list, trMarkList);
            this.hideButton(content, R.string.main_nav_mark, trMark);
            this.hideButton(content, R.string.main_nav_timetable, trTimeTable);
            this.hideButton(content, R.string.main_nav_timer, trTimer);
            this.hideButton(content, R.string.main_nav_notes, trNotes);
            this.hideButton(content, R.string.main_nav_todo, trTodo);
        }
    }

    private void addEvents() {
        this.eventAdapter.clear();
        if(this.llToday.getVisibility()==View.VISIBLE) {
            List<TimerEvent> timerEvents = MainActivity.globals.getSqLite().getTimerEvents("");
            for(TimerEvent event : timerEvents) {
                if(Helper.compareDateWithCurrentDate(event.getEventDate())) {
                    this.eventAdapter.add(new AbstractMap.SimpleEntry<>(this.getString(R.string.main_nav_timer), event.getTitle()));
                }
            }

            List<Memory> memories = MainActivity.globals.getSqLite().getCurrentMemories();
            for(Memory memory : memories) {
                try {
                    if(Helper.compareDateWithCurrentDate(Converter.convertStringToDate(memory.getDate()))) {
                        this.eventAdapter.add(new AbstractMap.SimpleEntry<>("Er.(" + memory.getStringType(this.getApplicationContext()) + ")", memory.getTitle()));
                    }
                } catch (Exception ex) {
                    Helper.printException(this.getApplicationContext(), ex);
                }
            }

            if(this.eventAdapter.isEmpty()) {
                this.eventAdapter.add(new AbstractMap.SimpleEntry<>(this.getString(R.string.main_noEntry), ""));
            }
        }
    }

    private void addNotes() {
        this.noteAdapter.clear();
        if(this.llCurrentNotes.getVisibility()==View.VISIBLE) {
            List<Note> notes = MainActivity.globals.getSqLite().getNotes("");
            for(int i = 0; i<=4; i++) {
                if(notes.size()-1>=i) {
                    this.noteAdapter.add(notes.get(i));
                }
            }

            if(this.noteAdapter.isEmpty()) {
                Note note = new Note();
                note.setTitle(this.getString(R.string.main_noEntry));
                this.noteAdapter.add(note);
            }
        }
    }

    private void addMarkLists() {
        this.savedMarkListAdapter.clear();
        if(this.llSavedMarkList.getVisibility()==View.VISIBLE) {
            for(String item : MainActivity.globals.getSqLite().listMarkLists()) {
                this.savedMarkListAdapter.add(item);
            }
        }
    }

    private void addSearchItems(String search) {
        this.searchAdapter.clear();

        for(MarkListSettings settings : MainActivity.globals.getSqLite().getMarkListSearch(search)) {
            this.searchAdapter.add(new SearchItem(settings.getId(), settings.getTitle(), this.getString(R.string.main_nav_mark_list)));
        }

        for(SchoolYear schoolYear : MainActivity.globals.getSqLite().getSchoolYears("")) {
            if(schoolYear.getSubject()!=null) {
                if(schoolYear.getSubject().getTitle().toLowerCase().contains(search.toLowerCase())) {
                    this.searchAdapter.add(new SearchItem(schoolYear.getSubject().getID(), schoolYear.getSubject().getTitle(), this.getString(R.string.timetable_lesson)));
                }
            }
            for(Test test : schoolYear.getTests()) {
                if(test.getTitle().toLowerCase().contains(search.toLowerCase())) {
                    this.searchAdapter.add(new SearchItem(test.getID(), test.getTitle(), this.getString(R.string.mark_test)));
                }
            }
        }

        for(Note note : MainActivity.globals.getSqLite().getNotes("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(note.getID(), note.getTitle(), this.getString(R.string.main_nav_notes)));
        }

        for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(toDoList.getID(), toDoList.getTitle(), this.getString(R.string.todo_list)));
        }

        for(ToDo toDo : MainActivity.globals.getSqLite().getToDos("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(toDo.getID(), toDo.getTitle(), this.getString(R.string.main_nav_todo)));
        }

        for(TimeTable timeTable : MainActivity.globals.getSqLite().getTimeTables("title like '%" + search + "%'")) {
            this.searchAdapter.add(new SearchItem(timeTable.getID(), timeTable.getTitle(), this.getString(R.string.main_nav_timetable)));
        }

        for(TimerEvent timerEvent : MainActivity.globals.getSqLite().getTimerEvents("title like '%" + search + "%'")) {
            SearchItem searchItem = new SearchItem(timerEvent.getID(), timerEvent.getTitle(), this.getString(R.string.main_nav_timer));
            searchItem.setExtra(Converter.convertDateToString(timerEvent.getEventDate()));
            this.searchAdapter.add(searchItem);
        }



        if(this.searchAdapter.isEmpty()) {
            this.lvSearchResults.setVisibility(View.GONE);
        } else {
            this.lvSearchResults.setVisibility(View.VISIBLE);
        }
    }

    private void calculateSelectedMarkList(MarkListSettings settings) {
        try {
            this.markListAdapter.clear();
            GermanListWithCrease germanListWithCrease = new GermanListWithCrease(this.getApplicationContext(), settings.getMaxPoints());
            germanListWithCrease.setMarkMultiplier(0.1);
            germanListWithCrease.setPointsMultiplier(0.5);
            germanListWithCrease.setDictatMode(settings.isDictatMode());
            germanListWithCrease.setWorstMarkTo(settings.getWorstMarkTo());
            germanListWithCrease.setBestMarkAt(settings.getBestMarkAt());
            germanListWithCrease.setCustomMark(settings.getCustomMark());
            germanListWithCrease.setCustomPoints(settings.getCustomPoints());
            this.markListAdapter.setViewMode(germanListWithCrease.getViewMode());
            this.markListAdapter.setDictatMode(germanListWithCrease.isDictatMode());

            for(Map.Entry<Double, Double> entry : germanListWithCrease.calculate().entrySet()) {
                this.markListAdapter.add(entry);
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }


    private void hideButton(String content, int id, TableRow row) {
        if(content.equals(this.getString(id))) {
            row.setVisibility(View.VISIBLE);
        }
    }

    private void hideMenuItems() {
        this.navigationView.getMenu().findItem(R.id.navMainMarkList).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.navMainCalculateMark).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.navMainTimeTable).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.navMainTimer).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.navMainToDo).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.navMainNotes).setVisible(false);

        Set<String> modules = settings.getShownModule(this.getApplicationContext());
        for(String content : modules) {
            this.hideMenu(content, R.string.main_nav_mark_list, R.id.navMainMarkList, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_mark, R.id.navMainCalculateMark, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timetable, R.id.navMainTimeTable, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timer, R.id.navMainTimer, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_notes, R.id.navMainToDo, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_todo, R.id.navMainNotes, this.navigationView.getMenu());
        }
    }

    private void hideWidgets() {
        this.tblButtons.setVisibility(View.GONE);
        this.llToday.setVisibility(View.GONE);
        this.llCurrentNotes.setVisibility(View.GONE);
        this.llSavedMarkList.setVisibility(View.GONE);

        for(String item : MainActivity.settings.getStartWidgets(this.getApplicationContext())) {
            if(item.equals(this.getString(R.string.main_nav_buttons))) {
                this.tblButtons.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_today))) {
                this.llToday.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_top5Notes))) {
                this.llCurrentNotes.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_savedMarkList))) {
                this.llSavedMarkList.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideMenu(String content, int id, int menu_id, Menu menu) {
        if(content.equals(this.getString(id))) {
            menu.findItem(menu_id).setVisible(true);
        }
    }

    private void openStartModule() {
        if(!globals.isStartScreen()) {
            globals.setStartScreen(true);
            String module = MainActivity.settings.getStartModule(this.getApplicationContext());
            if(!module.equals(this.getString(R.string.main_nav_main))) {
                Intent intent = null;
                if(module.equals(this.getString(R.string.main_nav_mark_list))) {
                    intent = new Intent(this.getApplicationContext(), MarkListActivity.class);
                }
                if(module.equals(this.getString(R.string.main_nav_mark))) {
                    intent = new Intent(this.getApplicationContext(), MarkActivity.class);
                }
                if(module.equals(this.getString(R.string.main_nav_timetable))) {
                    intent = new Intent(this.getApplicationContext(), TimeTableActivity.class);
                }
                if(module.equals(this.getString(R.string.main_nav_timer))) {
                    intent = new Intent(this.getApplicationContext(), TimerActivity.class);
                }
                if(module.equals(this.getString(R.string.main_nav_todo))) {
                    intent = new Intent(this.getApplicationContext(), ToDoActivity.class);
                }
                if(module.equals(this.getString(R.string.main_nav_notes))) {
                    intent = new Intent(this.getApplicationContext(), NoteActivity.class);
                }

                if(intent!=null) {
                    startActivityForResult(intent, 99);
                }
            }
        }
    }

    private void deleteMemoriesFromPast() {
        if(MainActivity.settings.isDeleteMemories()) {
            for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
                try {
                    if (Helper.compareDateWithCurrentDate(Converter.convertStringToDate(memory.getDate()))) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    }
                } catch (Exception ex) {
                    MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getId());
                    Log4JHelper.getLogger(MainActivity.this.getPackageName()).error(ex.getMessage());
                }
            }
        }
    }

    private void addGeneralSettings(String phase, float version) {
        this.firstUse = MainActivity.generals.getCurrentInternalPhase().isEmpty();
        String oldPhase = MainActivity.generals.getCurrentInternalPhase();
        float oldVersion = MainActivity.generals.getCurrentInternalVersion();
        MainActivity.generals.setCurrentInternalPhase(phase);
        MainActivity.generals.setCurrentInternalVersion(version);
        this.versionChange = (!oldPhase.equals(phase)) || (oldVersion!=version);
    }

    private void openWhatsNew() {
        if(this.firstUse || this.versionChange) {
            Intent intent = new Intent(this.getApplicationContext(), WhatsNewActivity.class);
            this.startActivity(intent);
        }
    }
}
