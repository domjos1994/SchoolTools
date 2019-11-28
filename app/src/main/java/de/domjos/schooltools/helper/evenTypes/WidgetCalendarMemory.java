package de.domjos.schooltools.helper.evenTypes;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Memory;

public class WidgetCalendarMemory extends Event {
    private Memory memory;
    private int icon;

    public WidgetCalendarMemory() {
        super();

        super.setColor(android.R.color.holo_blue_dark);
    }

    @Override
    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Memory getMemory() {
        return this.memory;
    }

    public void setMemory(Memory memory) throws Exception {
        this.memory = memory;
        super.setCalendar(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this.memory.getDate()));
        super.setName(this.memory.getTitle());
        super.setDescription(this.memory.getDescription());
        super.setId(this.memory.getID());
    }
}
