/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper.evenTypes;

import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.TimerEvent;

public class WidgetCalendarTimerEvent extends Event {
    private TimerEvent timerEvent;

    public WidgetCalendarTimerEvent() {
        super();

        super.setColor(android.R.color.transparent);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_event_black_24dp;
    }

    public TimerEvent getTimerEvent() {
        return this.timerEvent;
    }

    public void setTimerEvent(TimerEvent timerEvent) {
        this.timerEvent = timerEvent;
        super.setCalendar(this.timerEvent.getEventDate());
        super.setName(this.timerEvent.getTitle());
        super.setDescription(this.timerEvent.getDescription());
        super.setId(this.timerEvent.getId());
    }
}
