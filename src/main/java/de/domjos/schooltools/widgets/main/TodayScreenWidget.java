package de.domjos.schooltools.widgets.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.EventAdapter;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.helper.Converter;
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
        this.eventAdapter = new EventAdapter(activity, R.layout.main_today_event, new ArrayList<Map.Entry<String, String>>());
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
                    if(Helper.compareDateWithCurrentDate(Converter.convertStringToDate(memory.getDate()))) {
                        this.eventAdapter.add(new AbstractMap.SimpleEntry<>("Er.(" + memory.getStringType(this.activity) + ")", memory.getTitle()));
                    }
                } catch (Exception ex) {
                    Helper.printException(this.activity, ex);
                }
            }

            if(this.eventAdapter.isEmpty()) {
                this.eventAdapter.add(new AbstractMap.SimpleEntry<>(this.activity.getString(R.string.main_noEntry), ""));
            }
        }
    }
}
