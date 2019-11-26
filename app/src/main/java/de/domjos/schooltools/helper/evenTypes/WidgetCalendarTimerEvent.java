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
        super.setId(this.timerEvent.getID());
    }
}
