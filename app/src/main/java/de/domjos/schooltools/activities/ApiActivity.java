/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.learningCard.LearningCardGroup;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.schooltoolslib.utils.fileUtils.PDFBuilder;
import de.domjos.schooltools.helper.ApiHelper;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.EventHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.schooltools.settings.MarkListSettings;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Activity For the Export-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class ApiActivity extends AbstractActivity {
    private SQLite sqLite;
    private Spinner spApiChoice, spApiType, spApiEntryType, spApiEntry, spApiFormat;
    private ArrayAdapter<String> apiChoice, apiType, apiEntryType, apiFormat;
    private ArrayAdapter<BaseDescriptionObject> apiEntry;
    private TextView lblApiPath;
    private Button cmdApiPath;
    private ImageButton cmdApiSave;
    private FilePickerDialog dialog;
    private ApiHelper apiHelper;

    public ApiActivity() {
        super(R.layout.api_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.apiHelper = new ApiHelper(this.getApplicationContext());
        this.loadTypes();


        this.spApiChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spApiType.setEnabled(true);
                loadFormat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spApiType.setEnabled(false);
            }
        });

        this.spApiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isMemory = spApiType.getSelectedItem().toString().equals(getString(R.string.sys_memory));
                spApiFormat.setEnabled(!isMemory);
                if(isMemory) {
                    addIfNotExists(getString(R.string.api_format_calendar), apiFormat);
                    spApiFormat.setSelection(apiFormat.getPosition(getString(R.string.api_format_calendar)));
                } else {
                    apiFormat.remove(getString(R.string.api_format_calendar));
                }

                spApiEntryType.setEnabled(true);
                if(spApiEntryType.getSelectedItemPosition()==1) {
                    loadEntries();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spApiEntryType.setEnabled(false);
            }
        });

        this.spApiEntryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = apiEntryType.getItem(position);

                if(item!=null) {
                    if(item.equals(getResources().getStringArray(R.array.api_entry_type).clone()[0])) {
                        spApiEntry.setEnabled(false);
                        apiEntry.clear();
                    } else {
                        spApiEntry.setEnabled(true);
                        loadEntries();
                    }
                } else {
                    spApiEntry.setEnabled(false);
                    apiEntry.clear();
                }

                loadFormat();
                spApiFormat.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spApiEntry.setEnabled(false);
                apiEntry.clear();
                spApiFormat.setEnabled(false);
            }
        });

        this.spApiFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cmdApiPath.setEnabled(true);
                cmdApiSave.setEnabled(true);
                try {
                    lblApiPath.setText(ApiHelper.findExistingFolder(ApiActivity.this));
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
                    File documentDir =new File(Environment.getExternalStorageDirectory() + String.valueOf(File.separatorChar) + "Documents");
                    if(documentDir.mkdirs()) {
                        lblApiPath.setText(documentDir.getAbsolutePath());
                    } else {
                        lblApiPath.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cmdApiPath.setEnabled(false);
                cmdApiSave.setEnabled(false);
                lblApiPath.setText("");
            }
        });


        this.cmdApiPath.setOnClickListener(v -> {
            try {
                String item = apiChoice.getItem(spApiChoice.getSelectedItemPosition());
                if(item!=null) {
                    File defaultDir = getApplicationContext().getFilesDir();
                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    File child = new File(ApiHelper.findExistingFolder(ApiActivity.this));
                    if(child.getParent()!=null) {
                        properties.root = new File(child.getParent());
                    }
                    properties.error_dir = defaultDir;
                    properties.offset = defaultDir;

                    if(item.equals(getResources().getStringArray(R.array.api_choice)[0])) {
                        if(spApiFormat.getSelectedItemPosition()==0) {
                            properties.extensions = new String[]{"csv"};
                        } else if(spApiFormat.getSelectedItemPosition()==1) {
                            properties.extensions = new String[]{"xml"};
                        } else {
                            properties.extensions = null;
                        }
                        properties.selection_type = DialogConfigs.FILE_SELECT;
                        dialog = new FilePickerDialog(ApiActivity.this, properties);
                        dialog.setTitle(getString(R.string.api_path_choose_file));
                    } else {
                        properties.extensions = null;
                        properties.selection_type = DialogConfigs.DIR_SELECT;
                        dialog = new FilePickerDialog(ApiActivity.this, properties);
                        dialog.setTitle(getString(R.string.api_path_choose_dir));
                    }

                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(dialogInterface -> lblApiPath.setText(ApiHelper.findExistingFolder(ApiActivity.this)));

                    dialog.setDialogSelectionListener(files -> {
                        if(files!=null) {
                            lblApiPath.setText(files[0]);
                        }
                    });
                    dialog.show();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
            }
        });

        this.cmdApiSave.setOnClickListener(v -> {
            try {
                if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, ApiActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(executeChoice()) {
                        MessageHelper.printMessage(String.format(getString(R.string.api_choice_successfully), apiChoice.getItem(spApiChoice.getSelectedItemPosition())), R.mipmap.ic_launcher_round, ApiActivity.this);
                    } else {
                        MessageHelper.printMessage(String.format(getString(R.string.api_choice_error), apiChoice.getItem(spApiChoice.getSelectedItemPosition())), R.mipmap.ic_launcher_round, ApiActivity.this);
                    }
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
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
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_export_import"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if (requestCode == 9999) {
                Uri uri = data.getData();
                if (uri != null) {
                    lblApiPath.setText(ConvertHelper.convertURIToStringPath(this.getApplicationContext(), uri, R.mipmap.ic_launcher_round));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT:
                    if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                        if(dialog!=null) {
                            dialog.show();
                        }
                    }
                    break;
                case Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR:
                    try {
                        EventHelper helper = new EventHelper(this.getApplicationContext());
                        helper.saveMemoriesToCalendar(ApiActivity.this);
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
                    }
                    break;
                case Helper.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    if(this.executeChoice()) {
                        MessageHelper.printMessage(String.format(getString(R.string.api_choice_successfully), apiChoice.getItem(spApiChoice.getSelectedItemPosition())), R.mipmap.ic_launcher_round, ApiActivity.this);
                    } else {
                        MessageHelper.printMessage(String.format(getString(R.string.api_choice_error), apiChoice.getItem(spApiChoice.getSelectedItemPosition())), R.mipmap.ic_launcher_round, ApiActivity.this);
                    }
                    break;
                default:
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }
    }

    private boolean importTextOrCsv(String path, String type) {
        String content = Helper.getStringFromFile(path, this.getApplicationContext());
        try {
            if(type.equals(this.getString(R.string.main_nav_mark_list))) return this.apiHelper.importMarkListFromTEXT(content);
            if(type.equals(this.getString(R.string.main_nav_calculateMark))) return this.apiHelper.importMarkFromTEXT(content);
            if(type.equals(this.getString(R.string.main_nav_timetable))) return this.apiHelper.importTimeTableFromTEXT(content);
            if(type.equals(this.getString(R.string.main_nav_notes))) return this.apiHelper.importNoteFromTEXT(content);
            if(type.equals(this.getString(R.string.main_nav_todo))) return this.apiHelper.importToDoListFromText(content);
            if(type.equals(this.getString(R.string.main_nav_learningCards))) return this.apiHelper.importLearningCardGroupFromText(content);
            if(type.equals(this.getString(R.string.main_nav_timer))) return this.apiHelper.importTimerEventFromTEXT(content);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }

        return false;
    }

    private boolean importXml(String path, String type) {
        try {
            if(type.equals(this.getString(R.string.main_nav_mark_list))) return this.apiHelper.importMarkListFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_calculateMark))) return this.apiHelper.importMarkFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_timetable))) return this.apiHelper.importTimeTableFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_notes))) return this.apiHelper.importNoteFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_todo))) return this.apiHelper.importToDoListFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_learningCards))) return this.apiHelper.importLearningCardGroupFromXML(path);
            if(type.equals(this.getString(R.string.main_nav_timer))) return this.apiHelper.importTimerEventFromXML(path);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }
        return false;
    }

    private boolean exportTextOrCsv(String path, String type, String format, String where) {
        String extension = "";
        if(format.equals(this.getString(R.string.api_format_csv))) {
            extension = "csv";
        }
        String exportPath = String.format("%s/export_%s.%s", path, type, extension);

        StringBuilder content = new StringBuilder();
        if(type.equals(this.getString(R.string.main_nav_mark_list))) {
            List<MarkListSettings> settingsList = new LinkedList<>();
            for(String name : this.sqLite.listMarkLists(where)) {
                settingsList.add(this.sqLite.getMarkList(name));
            }
            content.append(this.apiHelper.exportMarkListToTEXT(settingsList));
        } else if(type.equals(this.getString(R.string.main_nav_calculateMark))) {
            content.append(this.apiHelper.exportMarkToTEXT(this.sqLite.getSchoolYears(where)));
        } else if(type.equals(this.getString(R.string.main_nav_timetable))) {
            content.append(this.apiHelper.exportTimeTableToTEXT(this.sqLite.getTimeTables(where)));
        } else if(type.equals(this.getString(R.string.main_nav_notes))) {
            content.append(this.apiHelper.exportNoteToTEXT(this.sqLite.getNotes(where)));
        } else if(type.equals(this.getString(R.string.main_nav_todo))) {
            content.append(this.apiHelper.exportToDoListToTEXT(this.sqLite.getToDoLists(where)));
        } else if(type.equals(this.getString(R.string.main_nav_learningCards))) {
            content.append(this.apiHelper.exportLearningCardGroupToTEXT(this.sqLite.getLearningCardGroups(where, true)));
        } else if(type.equals(this.getString(R.string.main_nav_timer))) {
            content.append(this.apiHelper.exportTimerEventToTEXT(this.sqLite.getTimerEvents(where)));
        }

        return Helper.writeStringToFile(content.toString(), exportPath, this.getApplicationContext());
    }

    private boolean exportXml(String path, String type, String where) {
        try {
            String exportPath = String.format("%s/export_%s.xml", path, type);

            if(type.equals(this.getString(R.string.main_nav_mark_list))) {
                return this.apiHelper.exportMarkListToXML(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_calculateMark))) {
                return this.apiHelper.exportMarkToXML(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_timetable))) {
                return this.apiHelper.exportTimeTableToXML(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_notes))) {
                return this.apiHelper.exportNoteToXML(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_todo))) {
                return this.apiHelper.exportToDoListToXMLElement(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_learningCards))) {
                return this.apiHelper.exportLearningCardGroupToXML(where, exportPath);
            } else if(type.equals(this.getString(R.string.main_nav_timer))) {
                return this.apiHelper.exportTimerEventToXML(where, exportPath);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }
        return false;
    }

    private boolean exportPDF(String path, String type, String where) {
        try {
            File emptyPDF = new File(path);
            if(!emptyPDF.exists()) {
                if (!emptyPDF.createNewFile()) {
                    return false;
                }
            }

            PDFBuilder pdfBuilder = new PDFBuilder(emptyPDF.getAbsolutePath(), ConvertHelper.convertDrawableToByteArray(this.getApplicationContext(), R.drawable.icon), this.getApplicationContext());
            pdfBuilder.addFont("header", Font.FontFamily.HELVETICA, 32, true, true, BaseColor.BLACK);
            pdfBuilder.addFont("subHeader", Font.FontFamily.HELVETICA, 28, true, false, BaseColor.BLACK);
            pdfBuilder.addFont("CONTENT_PARAM", Font.FontFamily.HELVETICA, 16, false, false, BaseColor.BLACK);

            if(type.equals(this.getString(R.string.main_nav_mark_list))) {
                List<String> stringList = this.sqLite.listMarkLists(where);
                List<MarkListSettings> markListSettings = new LinkedList<>();
                for(String string : stringList) {
                    markListSettings.add(this.sqLite.getMarkList(string));
                }
                pdfBuilder.addTitle(this.getString(R.string.main_nav_mark_list), "header", Paragraph.ALIGN_CENTER);
                for(MarkListSettings settings : markListSettings) {
                    pdfBuilder = this.apiHelper.exportMarkListToPDF(pdfBuilder, settings);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_calculateMark))) {
                List<SchoolYear> schoolYears = this.sqLite.getSchoolYears(where);
                for(SchoolYear schoolYear : schoolYears) {
                    pdfBuilder = apiHelper.exportMarkToPDF(pdfBuilder, schoolYear);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_timetable))) {
                List<TimeTable> timeTables = this.sqLite.getTimeTables(where);
                List<String> headers =
                        Arrays.asList(
                                this.getString(R.string.timetable_times), this.getString(R.string.timetable_days_mon), this.getString(R.string.timetable_days_tue), this.getString(R.string.timetable_days_wed),
                                this.getString(R.string.timetable_days_thu), this.getString(R.string.timetable_days_fri), this.getString(R.string.timetable_days_sat), this.getString(R.string.timetable_days_sun)
                        );

                for(TimeTable timeTable : timeTables) {
                    pdfBuilder = this.apiHelper.exportTimeTableToPDF(pdfBuilder, timeTable, headers, this.sqLite);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_notes))) {
                for(Note note : this.sqLite.getNotes(where)) {
                    pdfBuilder = this.apiHelper.exportNoteToPDF(pdfBuilder, note);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_todo))) {
                for(ToDoList toDoList : this.sqLite.getToDoLists(where)) {
                    pdfBuilder = this.apiHelper.exportToDoListToPDF(pdfBuilder, toDoList);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_learningCards))) {
                for(LearningCardGroup learningCardGroup : this.sqLite.getLearningCardGroups(where, true)) {
                    pdfBuilder = this.apiHelper.exportLearningCardGroupToPDF(pdfBuilder, learningCardGroup);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(this.getString(R.string.main_nav_timer))) {
                for(TimerEvent timerEvent : this.sqLite.getTimerEvents(where)) {
                    pdfBuilder = this.apiHelper.exportTimerEventToPDF(pdfBuilder, timerEvent);
                }
                pdfBuilder.close();
                return true;
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }
        return false;
    }

    private boolean exportToCalendar(String entryType, int id) {
        try {
            if(entryType.equals(this.getString(R.string.api_entry_all))) {
                if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, ApiActivity.this, Manifest.permission.WRITE_CALENDAR)) {
                    try {
                        EventHelper helper = new EventHelper(this.getApplicationContext());
                        helper.saveMemoriesToCalendar(ApiActivity.this);
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
                    }
                }
                return true;
            } else {
                for(Memory memory : sqLite.getCurrentMemories()) {
                    if(memory.getId()==id) {
                        EventHelper helper = new EventHelper(memory, this.getApplicationContext());
                        Intent intent = helper.openCalendar();
                        if(intent!=null) {
                            startActivity(intent);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
        }
        return false;
    }

    private void loadTypes() {
        this.apiType.add(this.getString(R.string.main_nav_mark_list));
        this.apiType.add(this.getString(R.string.main_nav_calculateMark));
        this.apiType.add(this.getString(R.string.main_nav_timetable));
        this.apiType.add(this.getString(R.string.main_nav_notes));
        this.apiType.add(this.getString(R.string.main_nav_timer));
        this.apiType.add(this.getString(R.string.main_nav_todo));
        this.apiType.add(this.getString(R.string.main_nav_learningCards));
    }

    private void loadEntries() {
        SQLite sqLite = MainActivity.globals.getSqLite();
        this.apiEntry.clear();

        String selectedType = this.apiType.getItem(this.spApiType.getSelectedItemPosition());
        if(selectedType!=null) {
            if(selectedType.equals(this.getString(R.string.main_nav_mark_list))) {
                for(String name : sqLite.listMarkLists()) {
                    MarkListSettings settings = sqLite.getMarkList(name);
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(settings.getId());
                    baseDescriptionObject.setTitle(settings.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_calculateMark))) {
                for(Subject subject : sqLite.getSubjects("")) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(subject.getId());
                    baseDescriptionObject.setTitle(subject.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_timetable))) {
                for(TimeTable timeTable : sqLite.getTimeTables("")) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(timeTable.getId());
                    baseDescriptionObject.setTitle(timeTable.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_notes))) {
                for(Note note : sqLite.getNotes("")) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(note.getId());
                    baseDescriptionObject.setTitle(note.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_timer))) {
                for(TimerEvent timerEvent : sqLite.getTimerEvents("")) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(timerEvent.getId());
                    baseDescriptionObject.setTitle(timerEvent.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_todo))) {
                for(ToDoList toDoList : sqLite.getToDoLists("")) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(toDoList.getId());
                    baseDescriptionObject.setTitle(toDoList.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.main_nav_learningCards))) {
                for(LearningCardGroup group : sqLite.getLearningCardGroups("", false)) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(group.getId());
                    baseDescriptionObject.setTitle(group.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
            if(selectedType.equals(this.getString(R.string.sys_memory))) {
                for(Memory memory : sqLite.getCurrentMemories()) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(memory.getId());
                    baseDescriptionObject.setTitle(memory.getTitle());
                    this.apiEntry.add(baseDescriptionObject);
                }
            }
        }
    }

    private void loadFormat() {
        String item = this.apiChoice.getItem(this.spApiChoice.getSelectedItemPosition());

        this.apiFormat.clear();
        if(item!=null) {
            if(item.equals(this.getResources().getStringArray(R.array.api_choice).clone()[0])) {
                this.apiFormat.addAll(this.getResources().getStringArray(R.array.api_import_formats));
                this.apiType.remove(this.getString(R.string.sys_memory));
            } else {
                this.apiFormat.addAll(this.getResources().getStringArray(R.array.api_export_formats));
                this.addIfNotExists(this.getString(R.string.sys_memory), this.apiType);
            }
        }
    }

    private void addIfNotExists(String item, ArrayAdapter<String> adapter) {
        for(int i = 0; i<=adapter.getCount()-1; i++) {
            String choice = adapter.getItem(i);
            if(choice!=null) {
                if(choice.equals(item)) {
                    return;
                }
            }
        }
        adapter.add(item);
    }

    private boolean executeChoice() {
        // get selected Data from controls
        String exportPath = lblApiPath.getText().toString();

        // create where clause for queries
        String entryType = spApiEntryType.getSelectedItem().toString();
        String where = "";
        if(entryType.equals(this.getString(R.string.api_entry_single))) {
            if(!apiEntry.isEmpty()) {
                if(spApiEntry.getSelectedItemPosition()!=-1) {
                    BaseDescriptionObject selectedEntry = apiEntry.getItem(spApiEntry.getSelectedItemPosition());
                    if(selectedEntry!=null) {
                        where = "ID=" + selectedEntry.getId();
                    }
                }
            }
        }

        String choice = spApiChoice.getSelectedItem().toString();
        String format = spApiFormat.getSelectedItem().toString();
        String type = spApiType.getSelectedItem().toString();

        if(choice.equals(this.getString(R.string.api_choice_export))) {
            if(type.equals(this.getString(R.string.sys_memory))) {
                int id = -1;
                if(this.spApiEntry.getSelectedItem()!=null) {
                    if(this.spApiEntry.getSelectedItem().toString().contains(":")) {
                        id = Integer.parseInt(this.spApiEntry.getSelectedItem().toString().split(":")[0].trim());
                    }
                }
                return this.exportToCalendar(entryType, id);
            }
        }

        if(choice.equals(this.getString(R.string.api_choice_import))) {
            if(format.equals(this.getString(R.string.api_format_csv))) {
                return this.importTextOrCsv(exportPath, type);
            } else if(format.equals(this.getString(R.string.api_format_xml))) {
                return this.importXml(exportPath, type);
            }
        } else {
            if(format.equals(this.getString(R.string.api_format_csv))) {
                return this.exportTextOrCsv(exportPath, type, format, where);
            } else if(format.equals(this.getString(R.string.api_format_pdf))) {
                exportPath = String.format("%s/export_%s.pdf", exportPath, type);
                boolean state =  this.exportPDF(exportPath, type, where);
                if(state) {
                    try {
                        PDFBuilder.openPDFFile(exportPath, this.getApplicationContext());
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ApiActivity.this);
                    }
                }
                return state;
            } else if(format.equals(this.getString(R.string.api_format_xml))) {
                return this.exportXml(exportPath, type, where);
            }
        }
        return false;
    }

    @Override
    protected void initControls() {
        this.sqLite = MainActivity.globals.getSqLite();

        // init other controls
        this.spApiChoice = this.findViewById(R.id.spApiChoice);
        this.apiChoice = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, this.getResources().getStringArray(R.array.api_choice));
        this.spApiChoice.setAdapter(this.apiChoice);
        this.apiChoice.notifyDataSetChanged();


        this.spApiType = this.findViewById(R.id.spApiType);
        this.apiType = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, new ArrayList<>());
        this.spApiType.setAdapter(this.apiType);
        this.apiType.notifyDataSetChanged();

        this.spApiEntryType = this.findViewById(R.id.spApiEntryType);
        this.apiEntryType = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, this.getResources().getStringArray(R.array.api_entry_type));
        this.spApiEntryType.setAdapter(this.apiEntryType);
        this.apiEntryType.notifyDataSetChanged();
        this.spApiEntryType.setEnabled(false);

        this.spApiEntry = this.findViewById(R.id.spApiEntry);
        this.apiEntry = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, new ArrayList<>());
        this.spApiEntry.setAdapter(this.apiEntry);
        this.apiEntry.notifyDataSetChanged();
        this.spApiEntry.setEnabled(false);

        this.spApiFormat = this.findViewById(R.id.spApiFormat);
        this.apiFormat = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, new ArrayList<>());
        this.spApiFormat.setAdapter(this.apiFormat);
        this.apiFormat.notifyDataSetChanged();
        this.spApiFormat.setEnabled(false);

        this.lblApiPath = this.findViewById(R.id.lblApiPathContent);
        this.lblApiPath.setEnabled(false);

        this.cmdApiPath = this.findViewById(R.id.cmdApiPath);
        this.cmdApiPath.setEnabled(false);
        if(!ApiHelper.isExternalStorageWritable()) {
            this.cmdApiPath.setVisibility(View.GONE);
        }

        this.cmdApiSave = this.findViewById(R.id.cmdApiSave);
        this.cmdApiSave.setEnabled(false);
    }
}
