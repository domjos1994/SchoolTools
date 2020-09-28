/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.app.Dialog;
import android.content.Intent;
import android.speech.RecognizerIntent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.schooltools.widgets.NoteWidget;

/**
 * Activity For the Note-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class NoteActivity extends AbstractActivity {
    private BottomNavigationView navigation;
    private static final int SPEECH_REQUEST_CODE = 0;

    private long currentID;
    private Validator validator;
    private SwipeRefreshDeleteList lvNotes;
    private EditText txtNoteTitle, txtNoteDescription, txtNoteMemoryDate;
    private ImageButton cmdNoteSpeak;
    private CheckBox chkNoteMemory;
    private Menu menu;

    public NoteActivity() {
        super(R.layout.note_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.reloadNotes();
        Helper.closeSoftKeyboard(NoteActivity.this);
        this.changeControls(false, true, false);
        this.getNoteFromExtra();
        this.txtNoteTitle.setError(null);

        this.lvNotes.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener)  listObject -> {
            Note note = (Note) listObject.getObject();
            if(note!=null) {
                currentID = note.getId();
                fillNote(note);
                changeControls(false, false, true);
            }
            menu.findItem(R.id.menDelete).setVisible(false);
            menu.findItem(R.id.menToDo).setVisible(false);
        });

        this.lvNotes.setOnDeleteListener(listObject -> {
            currentID = listObject.getId();
            deleteNote();
        });

        this.lvNotes.setOnReloadListener(this::reloadNotes);

        this.chkNoteMemory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                txtNoteMemoryDate.setVisibility(View.VISIBLE);
            } else {
                txtNoteMemoryDate.setText("");
                txtNoteMemoryDate.setVisibility(View.GONE);
            }
        });

        this.cmdNoteSpeak.setOnClickListener(v -> Helper.displaySpeechRecognizer(NoteActivity.this, SPEECH_REQUEST_CODE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menHelp:
                super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_notes"));
                break;
            case R.id.menDelete:
                deleteNote();
                this.menu.findItem(R.id.menDelete).setVisible(false);
                this.menu.findItem(R.id.menToDo).setVisible(false);
            case R.id.menToDo:
                final Dialog dialog = new Dialog(NoteActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.note_convert_dialog);
                dialog.setCancelable(true);


                final Spinner spNotesConvertToToDo = dialog.findViewById(R.id.spNotesConvertToToDo);
                final ArrayAdapter<ToDoList> notesConvertToToDo = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, MainActivity.globals.getSqLite().getToDoLists(""));
                spNotesConvertToToDo.setAdapter(notesConvertToToDo);
                notesConvertToToDo.notifyDataSetChanged();

                final Button btnStart = dialog.findViewById(R.id.cmdStart);
                btnStart.setOnClickListener(v -> {
                    try {
                        Note note = MainActivity.globals.getSqLite().getNotes("ID=" + currentID).get(0);
                        ToDoList toDoList = notesConvertToToDo.getItem(spNotesConvertToToDo.getSelectedItemPosition());

                        if(note!=null) {
                            if(toDoList!=null) {
                                ToDo toDo = new ToDo();
                                toDo.setTitle(note.getTitle());
                                toDo.setDescription(note.getDescription());
                                toDo.setCategory(getString(R.string.main_nav_notes));
                                toDo.setMemoryDate(note.getMemoryDate());
                                MainActivity.globals.getSqLite().insertOrUpdateToDo(toDo, toDoList.getTitle());
                                MainActivity.globals.getSqLite().deleteEntry("notes", "ID=" + currentID);
                                reloadNotes();
                                changeControls(false, true, false);
                            }
                        }
                        dialog.dismiss();
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, NoteActivity.this);
                    }
                });
                dialog.show();

                this.menu.findItem(R.id.menDelete).setVisible(false);
                this.menu.findItem(R.id.menToDo).setVisible(false);
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(results!=null) {
                Note note = Helper.getNoteFromString(this.getApplicationContext(), results.get(0));
                txtNoteDescription.setText(note.getDescription());
                txtNoteTitle.setText(note.getTitle());
            }
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
        this.lvNotes.getAdapter().clear();
        for(Note note : MainActivity.globals.getSqLite().getNotes("")) {
            this.lvNotes.getAdapter().add(note);
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
        this.navigation.getMenu().getItem(4).setEnabled(editMode);

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
        txtNoteMemoryDate.setText(ConvertHelper.convertDateToString(note.getMemoryDate(), this.getApplicationContext()));
        chkNoteMemory.setChecked(!txtNoteMemoryDate.getText().toString().trim().equals(""));
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(NoteActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addLengthValidator(txtNoteTitle, 3, 500);
        this.validator.addDateValidator(txtNoteMemoryDate, false);

    }

    @Override
    protected void initControls() {
        // init listener
        OnNavigationItemSelectedListener listener = item -> {
            switch (Helper.checkMenuID(item)) {
                case R.id.navTimeTableSubAdd:
                    changeControls(true, true, false);
                    menu.findItem(R.id.menDelete).setVisible(false);
                    menu.findItem(R.id.menToDo).setVisible(false);
                    break;
                case R.id.navTimeTableSubEdit:
                    changeControls(true, false, false);
                    menu.findItem(R.id.menDelete).setVisible(false);
                    menu.findItem(R.id.menToDo).setVisible(false);
                    break;
                case R.id.navTimeTableSubDelete:
                    deleteNote();
                    menu.findItem(R.id.menDelete).setVisible(false);
                    menu.findItem(R.id.menToDo).setVisible(false);
                    break;
                case R.id.navTimeTableSubCancel:
                    changeControls(false, true, false);
                    menu.findItem(R.id.menDelete).setVisible(false);
                    menu.findItem(R.id.menToDo).setVisible(false);
                    break;
                case R.id.navTimeTableSubSave:
                    try {
                        if(validator.getState()) {
                            Note note = new Note();
                            note.setId(currentID);
                            note.setTitle(txtNoteTitle.getText().toString());
                            note.setDescription(txtNoteDescription.getText().toString());
                            if(!txtNoteMemoryDate.getText().toString().equals("")) {
                                note.setMemoryDate(ConvertHelper.convertStringToDate(txtNoteMemoryDate.getText().toString(), this.getApplicationContext()));
                            }
                            MainActivity.globals.getSqLite().insertOrUpdateNote(note);
                            reloadNotes();
                            changeControls(false, true, false);
                            Helper.sendBroadCast(NoteActivity.this, NoteWidget.class);

                        }
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, NoteActivity.this);
                    } finally {
                        menu.findItem(R.id.menDelete).setVisible(false);
                        menu.findItem(R.id.menToDo).setVisible(false);
                    }
                    break;
                default:
            }
            return false;
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
    }

    private void deleteNote() {
        MainActivity.globals.getSqLite().deleteEntry("notes", "ID", this.currentID, "");
        MainActivity.globals.getSqLite().deleteEntry("memories", "itemID", this.currentID, "[table]='notes'");
        reloadNotes();
        changeControls(false, true, false);
        Helper.sendBroadCast(NoteActivity.this, NoteWidget.class);
    }
}
