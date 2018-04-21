
/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.NoteAdapter;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Validator;

/**
 * Activity For the Note-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public class NoteActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private static final int SPEECH_REQUEST_CODE = 0;


    private int currentID;
    private Validator validator;
    private ListView lvNotes;
    private NoteAdapter noteAdapter;
    private EditText txtNoteTitle, txtNoteDescription, txtNoteMemoryDate;
    private ImageButton cmdNoteSpeak;
    private CheckBox chkNoteMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.note_activity);
        this.initControls();
        this.initValidator();
        this.reloadNotes();
        Helper.closeSoftKeyboard(NoteActivity.this);
        this.changeControls(false, true, false);
        this.getNoteFromExtra();
        this.txtNoteTitle.setError(null);

        this.lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteAdapter.getItem(position);
                if(note!=null) {
                    currentID = note.getID();
                    fillNote(note);
                    changeControls(false, false, true);
                }
            }
        });

        this.chkNoteMemory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    txtNoteMemoryDate.setVisibility(View.VISIBLE);
                } else {
                    txtNoteMemoryDate.setText("");
                    txtNoteMemoryDate.setVisibility(View.GONE);
                }
            }
        });

        this.cmdNoteSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.displaySpeechRecognizer(NoteActivity.this, SPEECH_REQUEST_CODE);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Note note = Helper.getNoteFromString(this.getApplicationContext(), results.get(0));
            txtNoteDescription.setText(note.getDescription());
            txtNoteTitle.setText(note.getTitle());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getNoteFromExtra() {
        if(this.getIntent().hasExtra("id")) {
            currentID = this.getIntent().getIntExtra("id", 0);
        }
        List<Note> notes = MainActivity.globals.getSqLite().getNotes("ID=" + currentID);
        if(!notes.isEmpty()) {
            this.fillNote(notes.get(0));
            this.changeControls(false, false, true);
        }
    }

    private void reloadNotes() {
        this.noteAdapter.clear();
        for(Note note : MainActivity.globals.getSqLite().getNotes("")) {
            this.noteAdapter.add(note);
        }
    }

    private void changeControls(boolean editMode, boolean reset, boolean selected) {
        this.txtNoteTitle.setEnabled(editMode);
        this.txtNoteDescription.setEnabled(editMode);
        this.txtNoteMemoryDate.setEnabled(editMode);
        this.chkNoteMemory.setEnabled(editMode);
        this.lvNotes.setEnabled(!editMode);
        this.cmdNoteSpeak.setEnabled(editMode);
        this.navigation.getMenu().getItem(0).setEnabled(!editMode);
        this.navigation.getMenu().getItem(1).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(2).setEnabled(!editMode && selected);
        this.navigation.getMenu().getItem(3).setEnabled(editMode);
        this.navigation.getMenu().getItem(4).setEnabled(!editMode);

        if(reset) {
            currentID = 0;
            this.txtNoteTitle.setText("");
            this.txtNoteDescription.setText("");
            this.txtNoteMemoryDate.setText("");
            this.chkNoteMemory.setChecked(false);
        }
    }

    private void fillNote(Note note) {
        txtNoteTitle.setText(note.getTitle());
        txtNoteDescription.setText(note.getDescription());
        if(note.getMemoryDate()!=null) {
            chkNoteMemory.setChecked(true);
            txtNoteMemoryDate.setText(Converter.convertDateToString(note.getMemoryDate()));
        } else {
            chkNoteMemory.setChecked(false);
            txtNoteMemoryDate.setText("");
        }
    }

    private void initValidator() {
        this.validator = new Validator(this.getApplicationContext());
        this.validator.addLengthValidator(txtNoteTitle, 3, 500);
        this.validator.addDateValidator(txtNoteMemoryDate);

    }

    private void initControls() {
        // init listener
        OnNavigationItemSelectedListener listener = new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navTimeTableSubAdd:
                        changeControls(true, true, false);
                        break;
                    case R.id.navTimeTableSubEdit:
                        changeControls(true, false, false);
                        break;
                    case R.id.navTimeTableSubDelete:
                        MainActivity.globals.getSqLite().deleteEntry("notes", "ID", currentID, "");
                        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID", currentID, "[table]='notes'");
                        reloadNotes();
                        changeControls(false, true, false);
                        break;
                    case R.id.navTimeTableSubCancel:
                        changeControls(false, true, false);
                        break;
                    case R.id.navTimeTableSubSave:
                        try {
                            if(validator.getState()) {
                                Note note = new Note();
                                note.setID(currentID);
                                note.setTitle(txtNoteTitle.getText().toString());
                                note.setDescription(txtNoteDescription.getText().toString());
                                if(!txtNoteMemoryDate.getText().toString().equals("")) {
                                    note.setMemoryDate(Converter.convertStringToDate(txtNoteMemoryDate.getText().toString()));
                                }
                                MainActivity.globals.getSqLite().insertOrUpdateNote(note);
                                reloadNotes();
                                changeControls(false, true, false);
                            }
                        } catch (Exception ex) {
                            Helper.printException(getApplicationContext(), ex);
                        }
                        break;
                }
                return false;
            }

        };
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(listener);

        // init other controls
        this.txtNoteTitle = this.findViewById(R.id.txtNoteTitle);
        this.txtNoteDescription = this.findViewById(R.id.txtNoteDescription);
        this.txtNoteMemoryDate = this.findViewById(R.id.txtNoteMemoryDate);
        this.cmdNoteSpeak = this.findViewById(R.id.cmdNoteSpeak);
        this.chkNoteMemory = this.findViewById(R.id.chkNoteMemory);
        this.txtNoteMemoryDate.setVisibility(View.GONE);

        this.lvNotes = this.findViewById(R.id.lvNotes);
        this.noteAdapter = new NoteAdapter(this.getApplicationContext(), R.layout.note_item, new ArrayList<Note>());
        this.lvNotes.setAdapter(this.noteAdapter);
        this.noteAdapter.notifyDataSetChanged();
    }
}
