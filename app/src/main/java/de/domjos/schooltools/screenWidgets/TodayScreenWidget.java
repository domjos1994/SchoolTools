/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.EventAdapter;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.customwidgets.model.ScreenWidget;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.helper.Helper;

public final class TodayScreenWidget extends ScreenWidget {
    private EventAdapter eventAdapter;

    public TodayScreenWidget(View ll, Activity activity) {
        super(ll, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        ListView lvEvents = this.view.findViewById(R.id.lvEvents);
        this.eventAdapter = new EventAdapter(activity, R.layout.main_today_event, new ArrayList<>());
        lvEvents.setAdapter(this.eventAdapter);
        this.eventAdapter.notifyDataSetChanged();

        lvEvents.setOnTouchListener(Helper.addOnTouchListenerForScrolling());
    }

    public void addEvents() {
        this.eventAdapter.clear();
        if(this.view.getVisibility()== View.VISIBLE) {
            List<TimerEvent> timerEvents = MainActivity.globals.getSqLite().getTimerEvents("");
            for(TimerEvent event : timerEvents) {
                if(Helper.compareDateWithCurrentDate(event.getEventDate())) {
                    this.eventAdapter.add(new AbstractMap.SimpleEntry<>(this.activity.getString(R.string.main_nav_timer), event.getTitle()));
                }
            }

            List<Memory> memories = MainActivity.globals.getSqLite().getCurrentMemories();
            for(Memory memory : memories) {
                try {
                    if(Helper.compareDateWithCurrentDate(ConvertHelper.convertStringToDate(memory.getDate(), this.activity))) {
                        this.eventAdapter.add(new AbstractMap.SimpleEntry<>("Er.(" + memory.getStringType(this.activity) + ")", memory.getTitle()));
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, super.activity);
                }
            }

            if(this.eventAdapter.isEmpty()) {
                this.eventAdapter.add(new AbstractMap.SimpleEntry<>(this.activity.getString(R.string.main_noEntry), ""));
            }
        }
    }
}
