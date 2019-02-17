/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.TextView;

import java.util.*;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.*;
import de.domjos.schooltools.core.SearchItem;
import de.domjos.schooltools.core.exceptions.MarkListException;
import de.domjos.schooltools.core.marklist.de.GermanListWithCrease;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.core.model.mark.SchoolYear;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.core.model.timetable.Day;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.core.model.timetable.PupilHour;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.core.model.timetable.TeacherHour;
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

    public static final Globals globals = new Globals();

    private TableRow trMarkList, trMark, trTimeTable, trNotes, trTimer, trTodo, trExport, trSettings, trHelp, trLearningCards;
    private RelativeLayout llToday, llTodayCurrentTimeTable, llCurrentNotes, llSavedMarkList, llImportantToDos, llSavedTimeTables;
    private ImageButton cmdRefresh;
    private SearchView cmdSearch;
    private ListView lvCurrentNotes;
    private ListView lvSearchResults;
    private Spinner cmbSavedMarkList, cmbSavedTimeTables;
    private TableLayout tblButtons, grdSavedTimeTables;
    private EventAdapter eventAdapter;
    private NoteAdapter noteAdapter;
    private SearchAdapter searchAdapter;
    private ToDoAdapter toDoAdapter;
    private SubjectHourAdapter timeTableEventAdapter;
    private MarkListAdapter markListAdapter;
    private ArrayAdapter<String> savedMarkListAdapter;
    private ArrayAdapter<String> savedTimeTablesAdapter;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        Log4JHelper.configure(MainActivity.this);
        MainActivity.globals.setUserSettings(new UserSettings(this.getApplicationContext()));
        MainActivity.globals.setGeneralSettings(new GeneralSettings(this.getApplicationContext()));
        this.initControls();
        this.resetDatabase();
        this.initDatabase();
        this.initServices();
        this.hideButtons();
        this.openStartModule();
        Helper.setBackgroundToActivity(this);
        Helper.setBackgroundAppBarToActivity(this.navigationView, MainActivity.this);
        this.hideWidgets();
        this.addEvents();
        this.addNotes();
        this.addToDos();
        this.addMarkLists();
        this.initSavedTimeTables();
        this.deleteMemoriesFromPast();
        this.deleteToDosFromPast();
        this.setSavedValuesForWidgets();
        this.openWhatsNew();
        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                initCurrentTimeTableEvent();
            }

            @Override
            public void onFinish() {
                countDownTimer.start();
            }
        }.start();


        this.trMarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMarkListIntent();
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

        this.trLearningCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LearningCardOverviewActivity.class);
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
                MainActivity.globals.getGeneralSettings().setWidgetMarkListSpinner(savedMarkListAdapter.getItem(position));
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
                addToDos();
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
            if(MainActivity.globals.getUserSettings().isWhatsNew()) {
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
            this.hideButtons();
            this.hideMenuItems();
            this.initCurrentTimeTableEvent();
            this.openStartModule();
            this.hideWidgets();
            Helper.setBackgroundToActivity(this);
            Helper.setBackgroundAppBarToActivity(this.navigationView, MainActivity.this);
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
                openMarkListIntent();
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
                // init Memory Service
                Intent intent = new Intent(this.getApplicationContext(), MemoryService.class);
                PendingIntent pendingIntent1 = PendingIntent.getService(this.getApplicationContext(),  0, intent, 0);

                // init frequently
                AlarmManager alarmManager1 = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                long frequency= 8 * 60 * 60 * 1000;
                assert alarmManager1 != null;
                alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), frequency, pendingIntent1);
            }
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }



    @SuppressLint("ClickableViewAccessibility")
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
        this.trLearningCards = this.findViewById(R.id.trLearningCards);

        this.llToday = this.findViewById(R.id.llToday);
        ListView lvEvents = this.findViewById(R.id.lvEvents);
        this.eventAdapter = new EventAdapter(MainActivity.this, R.layout.main_today_event, new ArrayList<Map.Entry<String, String>>());
        lvEvents.setAdapter(this.eventAdapter);
        this.eventAdapter.notifyDataSetChanged();

        this.llTodayCurrentTimeTable = this.findViewById(R.id.llTodayCurrentTimeTable);
        ListView lvTodayCurrentTimeTable = this.findViewById(R.id.lvTodayCurrentTimeTableEvents);
        this.timeTableEventAdapter = new SubjectHourAdapter(MainActivity.this, R.layout.timetable_subject_item, new ArrayList<Map.Entry<Hour, Subject>>());
        lvTodayCurrentTimeTable.setAdapter(this.timeTableEventAdapter);
        this.timeTableEventAdapter.notifyDataSetChanged();

        this.llCurrentNotes = this.findViewById(R.id.llCurrentNotes);
        this.lvCurrentNotes = this.findViewById(R.id.lvNotes);
        this.noteAdapter = new NoteAdapter(MainActivity.this, R.layout.note_item, new ArrayList<Note>());
        this.lvCurrentNotes.setAdapter(this.noteAdapter);
        this.noteAdapter.notifyDataSetChanged();

        this.llImportantToDos = this.findViewById(R.id.llImportantToDos);
        ListView lvImportantToDos = this.findViewById(R.id.lvImportantToDos);
        this.toDoAdapter = new ToDoAdapter(MainActivity.this, R.layout.todo_item, new ArrayList<ToDo>());
        lvImportantToDos.setAdapter(this.toDoAdapter);
        this.toDoAdapter.notifyDataSetChanged();

        this.llSavedMarkList = this.findViewById(R.id.llSavedMarklist);

        this.cmbSavedMarkList = this.findViewById(R.id.cmbSavedMarklist);
        this.savedMarkListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.cmbSavedMarkList.setAdapter(this.savedMarkListAdapter);
        this.savedMarkListAdapter.notifyDataSetChanged();

        this.llSavedTimeTables = this.findViewById(R.id.llSavedTimeTables);

        this.cmbSavedTimeTables = this.findViewById(R.id.cmbSavedTimeTables);
        this.savedTimeTablesAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.cmbSavedTimeTables.setAdapter(this.savedTimeTablesAdapter);
        this.savedTimeTablesAdapter.notifyDataSetChanged();
        this.grdSavedTimeTables = this.findViewById(R.id.grdSavedTimeTables);


        this.lvSearchResults = this.findViewById(R.id.lvSearchResults);
        this.searchAdapter = new SearchAdapter(MainActivity.this);
        this.lvSearchResults.setAdapter(this.searchAdapter);
        this.searchAdapter.notifyDataSetChanged();

        ListView lvMarkList = this.findViewById(R.id.lvMarklist);
        this.markListAdapter = new MarkListAdapter(MainActivity.this, R.layout.marklist_item, new ArrayList<Map.Entry<Double, Double>>());
        lvMarkList.setAdapter(this.markListAdapter);
        this.markListAdapter.notifyDataSetChanged();

        lvMarkList.setOnTouchListener(this.addOnTouchListenerForScrolling());
        this.lvCurrentNotes.setOnTouchListener(this.addOnTouchListenerForScrolling());
        lvImportantToDos.setOnTouchListener(this.addOnTouchListenerForScrolling());
        lvEvents.setOnTouchListener(this.addOnTouchListenerForScrolling());
    }

    private View.OnTouchListener addOnTouchListenerForScrolling() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return v.performClick();
            }
        };
    }

    private void initDatabase() {
        SQLite sqLite = new SQLite(this.getApplicationContext(), "schoolTools.db", MainActivity.globals.getGeneralSettings().getCurrentVersionCode(MainActivity.this));
        MainActivity.globals.setSqLite(sqLite);
    }

    private void hideButtons() {
        this.trMarkList.setVisibility(View.GONE);
        this.trMark.setVisibility(View.GONE);
        this.trTimeTable.setVisibility(View.GONE);
        this.trNotes.setVisibility(View.GONE);
        this.trTimer.setVisibility(View.GONE);
        this.trTodo.setVisibility(View.GONE);

        Set<String> modules = MainActivity.globals.getUserSettings().getShownModule(this.getApplicationContext());
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

    private void addToDos() {
        this.toDoAdapter.clear();
        if(this.llImportantToDos.getVisibility()==View.VISIBLE) {
            List<ToDo> toDos = MainActivity.globals.getSqLite().getToDos("");
            Map<ToDo, Integer> todoMap = new HashMap<>();
            for(ToDo toDo : toDos) {
                todoMap.put(toDo, toDo.getImportance());
            }

            Object[] a = todoMap.entrySet().toArray();
            if(a!=null) {
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if(o2 instanceof Map.Entry && o1 instanceof Map.Entry) {
                            Map.Entry entry1 = (Map.Entry) o1;
                            Map.Entry entry2 = (Map.Entry) o2;

                            if(entry1.getValue() instanceof Integer && entry2.getValue() instanceof Integer) {
                                return ((Integer) entry1.getValue()).compareTo(((Integer) entry2.getValue()));
                            }
                        }
                        return -1;
                    }
                });
                toDos.clear();
                for(Object obj : a) {
                    if(obj instanceof Map.Entry) {
                        Map.Entry entry = (Map.Entry) obj;
                        if(entry.getKey() instanceof ToDo) {
                            this.toDoAdapter.add((ToDo) entry.getKey());
                        }
                    }

                    if(this.toDoAdapter.getCount() % 5 == 0) {
                        break;
                    }
                }
                if(this.toDoAdapter.isEmpty()) {
                    ToDo toDo = new ToDo();
                    toDo.setTitle(this.getString(R.string.main_noEntry));
                    this.toDoAdapter.add(toDo);
                }
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


        for(Subject subject : MainActivity.globals.getSqLite().getSubjects("title like '%" + search + "'")) {
            this.searchAdapter.add(new SearchItem(subject.getID(), subject.getTitle(), this.getString(R.string.timetable_lesson)));
        }

        for(Teacher teacher : MainActivity.globals.getSqLite().getTeachers("lastName like '%" + search + "'")) {
            this.searchAdapter.add(new SearchItem(teacher.getID(), teacher.getLastName(), this.getString(R.string.timetable_teacher)));
        }

        for(SchoolClass schoolClass : MainActivity.globals.getSqLite().getClasses("title like '%" + search + "'")) {
            this.searchAdapter.add(new SearchItem(schoolClass.getID(), schoolClass.getTitle(), this.getString(R.string.timetable_class)));
        }

        for(Year year : MainActivity.globals.getSqLite().getYears("title like '%" + search + "'")) {
            this.searchAdapter.add(new SearchItem(year.getID(), year.getTitle(), this.getString(R.string.mark_year)));
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
        } catch (MarkListException ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }


    private void initSavedTimeTables() {
        MainActivity.initTimes(this.grdSavedTimeTables);
        List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("");
        this.savedTimeTablesAdapter.add(new TimeTable().toString());
        for(TimeTable timeTable : timeTables) {
            this.savedTimeTablesAdapter.add(timeTable.toString());
        }
        this.cmbSavedTimeTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String title = savedTimeTablesAdapter.getItem(position);
                MainActivity.globals.getGeneralSettings().setWidgetTimetableSpinner(title);

                List<TimeTable> tables = MainActivity.globals.getSqLite().getTimeTables("title='" + title + "'");

                if(tables!=null) {
                    if(!tables.isEmpty()) {
                        TimeTableEntryActivity.loadTimeTable(tables.get(0), grdSavedTimeTables, new LinkedHashMap<String, Integer>());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static void initTimes(TableLayout grid) {
        Map<Double, Hour> times = new TreeMap<>();
        List<Hour> hours = MainActivity.globals.getSqLite().getHours("");
        for(Hour hour : hours) {
            times.put(Double.parseDouble(hour.getStart().replace(":", ".")), hour);
        }

        List hourList = Arrays.asList(times.values().toArray());
        int max = hourList.size()-1;
        for(int i = 1; i<=grid.getChildCount()-1; i++) {
            TableRow row = (TableRow) grid.getChildAt(i);
            TextView textView = (TextView) row.getChildAt(0);
            if((i-1)<=max) {
                Hour hour = (Hour) hourList.get(i-1);
                textView.setText(String.format("%s%n%s", hour.getStart(), hour.getEnd()));
                textView.setTag(String.valueOf(hour.getID()));

                if(hour.isBreak()) {
                    textView.setTextSize(14);
                    textView.setText(textView.getText().toString().replace("\n", ":"));
                    for(int j = 1; j<=row.getChildCount()-1; j++) {
                        row.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                        row.setBackgroundResource(R.drawable.tbl_border);
                    }
                }
            } else {
                grid.getChildAt(i).setVisibility(View.GONE);
            }
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
        this.navigationView.getMenu().findItem(R.id.navMainLearningCards).setVisible(false);

        Set<String> modules = MainActivity.globals.getUserSettings().getShownModule(this.getApplicationContext());
        for(String content : modules) {
            this.hideMenu(content, R.string.main_nav_mark_list, R.id.navMainMarkList, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_mark, R.id.navMainCalculateMark, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timetable, R.id.navMainTimeTable, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_timer, R.id.navMainTimer, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_notes, R.id.navMainToDo, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_todo, R.id.navMainNotes, this.navigationView.getMenu());
            this.hideMenu(content, R.string.main_nav_learningCards, R.id.navMainLearningCards, this.navigationView.getMenu());
        }
    }

    private void hideWidgets() {
        this.tblButtons.setVisibility(View.GONE);
        this.llToday.setVisibility(View.GONE);
        this.llTodayCurrentTimeTable.setVisibility(View.GONE);
        this.llCurrentNotes.setVisibility(View.GONE);
        this.llImportantToDos.setVisibility(View.GONE);
        this.llSavedMarkList.setVisibility(View.GONE);
        this.llSavedTimeTables.setVisibility(View.GONE);

        for(String item : MainActivity.globals.getUserSettings().getStartWidgets(this.getApplicationContext())) {
            if(item.equals(this.getString(R.string.main_nav_buttons))) {
                this.tblButtons.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_today))) {
                this.llToday.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_today_timetable))) {
                this.llTodayCurrentTimeTable.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_savedTimeTables))) {
                this.llSavedTimeTables.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_top5Notes))) {
                this.llCurrentNotes.setVisibility(View.VISIBLE);
            }
            if(item.equals(this.getString(R.string.main_importantToDos))) {
                this.llImportantToDos.setVisibility(View.VISIBLE);
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
            String module = MainActivity.globals.getUserSettings().getStartModule(this.getApplicationContext());
            if(!module.equals(this.getString(R.string.main_nav_main))) {
                Intent intent = null;
                if(module.equals(this.getString(R.string.main_nav_mark_list))) {
                    openMarkListIntent();
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
                if(module.equals(this.getString(R.string.main_nav_learningCards))) {
                    intent = new Intent(this.getApplicationContext(), LearningCardOverviewActivity.class);
                }

                if(intent!=null) {
                    startActivityForResult(intent, 99);
                }
            }
        }
    }

    private void initCurrentTimeTableEvent() {
        List<TimeTable> timeTables = MainActivity.globals.getSqLite().getTimeTables("");
        if(timeTables!=null) {
            if(!timeTables.isEmpty()) {
                for(TimeTable timeTable : timeTables) {
                    if(timeTable.isCurrentTimeTable()) {
                        Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
                        calendar.setTime(new Date());
                        int position = calendar.get(Calendar.DAY_OF_WEEK);
                        position = position-1;
                        if(position==0) {
                            position = 7;
                        }

                        Day[] days = timeTable.getDays();
                        for(Day day : days) {
                            if(day!=null) {
                                int dayPos = day.getPositionInWeek();
                                if (dayPos == position) {
                                    Date date = new Date();
                                    if (day.getPupilHour() != null) {
                                        int counter = 0;
                                        for (Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                            Date start = Converter.convertStringTimeToDate(this.getApplicationContext(), entry.getKey().getStart());
                                            Date end = Converter.convertStringTimeToDate(this.getApplicationContext(), entry.getKey().getEnd());

                                            if(start != null && end != null)  {
                                                boolean isAfterStart = start.before(date);
                                                boolean isBeforeEnd = end.after(date);

                                                if(isAfterStart && isBeforeEnd) {
                                                    timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getSubject()));

                                                    if(day.getPupilHour().size()-1>counter) {
                                                        Object[] objects = day.getPupilHour().keySet().toArray();
                                                        if(objects!=null) {
                                                            Hour hour = (Hour)objects[counter+1];
                                                            if(day.getPupilHour().values().toArray()[counter] instanceof PupilHour) {
                                                                PupilHour mapEntry = (PupilHour) day.getPupilHour().values().toArray()[counter+1];
                                                                timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(hour, mapEntry.getSubject()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            counter++;
                                        }
                                    }
                                    if (day.getTeacherHour() != null) {
                                        int counter = 0;
                                        for (Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                                            Date start = Converter.convertStringTimeToDate(this.getApplicationContext(), entry.getKey().getStart());
                                            Date end = Converter.convertStringTimeToDate(this.getApplicationContext(), entry.getKey().getEnd());

                                            if(start != null && end != null)  {
                                                boolean isAfterStart = start.before(date);
                                                boolean isBeforeEnd = end.after(date);

                                                if(isAfterStart && isBeforeEnd) {
                                                    timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getSubject()));

                                                    if(day.getTeacherHour().size()-1>counter) {
                                                        Object[] objects = day.getTeacherHour().keySet().toArray();
                                                        if(objects!=null) {
                                                            Hour hour = (Hour)objects[counter+1];
                                                            if(day.getTeacherHour().values().toArray()[counter+1] instanceof TeacherHour) {
                                                                TeacherHour mapEntry = (TeacherHour) day.getTeacherHour().values().toArray()[counter+1];
                                                                timeTableEventAdapter.add(new AbstractMap.SimpleEntry<>(hour, mapEntry.getSubject()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            counter++;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void deleteMemoriesFromPast() {
        if(MainActivity.globals.getUserSettings().isDeleteMemories()) {
            for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
                try {
                    if (Helper.compareDateWithCurrentDate(Converter.convertStringToDate(memory.getDate()))) {
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getID());
                    }
                } catch (Exception ex) {
                    MainActivity.globals.getSqLite().deleteEntry("memories", "itemID=" + memory.getID());
                    Log4JHelper.getLogger(MainActivity.this.getPackageName()).error(ex.getMessage());
                }
            }
        }
    }

    private void deleteToDosFromPast() {
        if(MainActivity.globals.getUserSettings().isDeleteToDoAfterDeadline()) {
            for(ToDoList toDoList : MainActivity.globals.getSqLite().getToDoLists("")) {
                try {
                    if(Helper.compareDateWithCurrentDate(toDoList.getListDate())) {
                        MainActivity.globals.getSqLite().deleteEntry("toDoLists", "ID=" + toDoList.getID());
                    }
                } catch (Exception ex) {
                    Log4JHelper.getLogger(MainActivity.this.getPackageName()).error(ex.getMessage());
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
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void openMarkListIntent() {
        if(MainActivity.globals.getGeneralSettings().isAcceptMarkListMessage()) {
            Intent intent = new Intent(getApplicationContext(), MarkListActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setTitle(R.string.message_marklist_important_message_header);
            dialogBuilder.setMessage(R.string.message_marklist_important_message_content);
            dialogBuilder.setPositiveButton(R.string.message_marklist_important_message_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.globals.getGeneralSettings().setAcceptMarkListMessage(true);
                    Intent intent = new Intent(getApplicationContext(), MarkListActivity.class);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton(R.string.sys_cancel, null);
            dialogBuilder.show();
        }
    }

    private void setSavedValuesForWidgets() {
        String timeTable = MainActivity.globals.getGeneralSettings().getWidgetTimetableSpinner();
        String markList = MainActivity.globals.getGeneralSettings().getWidgetMarkListSpinner();

        if(markList!=null) {
            for(int i = 0; i<=savedMarkListAdapter.getCount()-1; i++) {
                if(savedMarkListAdapter.getItem(i)!=null) {
                    String currentMarkList = this.savedMarkListAdapter.getItem(i);
                    if(currentMarkList!=null) {
                        if(currentMarkList.equals(markList)) {
                            this.cmbSavedMarkList.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }

        if(timeTable!=null) {
            for(int i = 0; i<=savedTimeTablesAdapter.getCount()-1; i++) {
                if(savedTimeTablesAdapter.getItem(i)!=null) {
                    String currentTimeTable = this.savedTimeTablesAdapter.getItem(i);
                    if(currentTimeTable!=null) {
                        if(currentTimeTable.equals(timeTable)) {
                            this.cmbSavedTimeTables.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }
    }
}
