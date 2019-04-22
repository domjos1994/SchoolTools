/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableRow;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.ApiActivity;
import de.domjos.schooltools.activities.BookmarkActivity;
import de.domjos.schooltools.activities.HelpActivity;
import de.domjos.schooltools.activities.LearningCardOverviewActivity;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.activities.MarkActivity;
import de.domjos.schooltools.activities.MarkListActivity;
import de.domjos.schooltools.activities.NoteActivity;
import de.domjos.schooltools.activities.SettingsActivity;
import de.domjos.schooltools.activities.TimeTableActivity;
import de.domjos.schooltools.activities.TimerActivity;
import de.domjos.schooltools.activities.ToDoActivity;
import de.domjos.schooltools.custom.ScreenWidget;

public final class ButtonScreenWidget extends ScreenWidget {
    private TableRow trMarkList, trMark, trTimeTable, trNotes, trTimer, trTodo, trExport, trSettings, trHelp, trLearningCards, trBookMarks;

    public ButtonScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    public void loadButtons() {
        this.openActivitiesByButtons(
            Arrays.asList(trMark, trTimeTable, trNotes, trTimer, trTodo, trBookMarks, trExport, trHelp),
            Arrays.asList(MarkActivity.class, TimeTableActivity.class, NoteActivity.class, TimerActivity.class, ToDoActivity.class, BookmarkActivity.class, ApiActivity.class, HelpActivity.class)
        );

        this.trMarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMarkListIntent();
            }
        });

        this.trLearningCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), LearningCardOverviewActivity.class);
                activity.startActivity(intent);
            }
        });

        this.trSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), SettingsActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public void init() {
        this.trMarkList = super.view.findViewById(R.id.trMarkList);
        this.trMark = super.view.findViewById(R.id.trMark);
        this.trTimeTable = super.view.findViewById(R.id.trTimeTable);
        this.trNotes = super.view.findViewById(R.id.trNote);
        this.trTimer = super.view.findViewById(R.id.trTimer);
        this.trTodo = super.view.findViewById(R.id.trToDo);
        this.trExport = super.view.findViewById(R.id.trExport);
        this.trSettings = super.view.findViewById(R.id.trSettings);
        this.trHelp = super.view.findViewById(R.id.trHelp);
        this.trLearningCards = super.view.findViewById(R.id.trLearningCards);
        this.trBookMarks = super.view.findViewById(R.id.trBookMarks);
    }

    public void openMarkListIntent() {
        if(MainActivity.globals.getGeneralSettings().isAcceptMarkListMessage()) {
            Intent intent = new Intent(activity.getApplicationContext(), MarkListActivity.class);
            activity.startActivity(intent);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setTitle(R.string.message_marklist_important_message_header);
            dialogBuilder.setMessage(R.string.message_marklist_important_message_content);
            dialogBuilder.setPositiveButton(R.string.message_marklist_important_message_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.globals.getGeneralSettings().setAcceptMarkListMessage(true);
                    Intent intent = new Intent(activity.getApplicationContext(), MarkListActivity.class);
                    activity.startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton(R.string.sys_cancel, null);
            dialogBuilder.show();
        }
    }

    public void hideButtons() {
        this.trMarkList.setVisibility(View.GONE);
        this.trMark.setVisibility(View.GONE);
        this.trTimeTable.setVisibility(View.GONE);
        this.trNotes.setVisibility(View.GONE);
        this.trTimer.setVisibility(View.GONE);
        this.trTodo.setVisibility(View.GONE);
        this.trLearningCards.setVisibility(View.GONE);
        this.trBookMarks.setVisibility(View.GONE);

        Set<String> modules = MainActivity.globals.getUserSettings().getShownModule();
        for(String content : modules) {
            this.hideButton(content, R.string.main_nav_mark_list, trMarkList);
            this.hideButton(content, R.string.main_nav_mark, trMark);
            this.hideButton(content, R.string.main_nav_timetable, trTimeTable);
            this.hideButton(content, R.string.main_nav_timer, trTimer);
            this.hideButton(content, R.string.main_nav_notes, trNotes);
            this.hideButton(content, R.string.main_nav_todo, trTodo);
            this.hideButton(content, R.string.main_nav_learningCards, trLearningCards);
            this.hideButton(content, R.string.main_nav_bookmarks, trBookMarks);
        }
    }

    private void hideButton(String content, int id, TableRow row) {
        if(content.equals(activity.getString(id))) {
            row.setVisibility(View.VISIBLE);
        }
    }

    private void openActivitiesByButtons(List<TableRow> tr, List<Class<? extends AppCompatActivity>> clss) {
        for(int i = 0; i<=tr.size()-1; i++) {
            this.openIntentWithButton(tr.get(i), clss.get(i));
        }
    }

    private void openIntentWithButton(TableRow tr, final Class<? extends AppCompatActivity> cls) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), cls);
                activity.startActivity(intent);
            }
        });
    }
}
