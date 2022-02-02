/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper.evenTypes;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.Memory;

public class WidgetCalendarMemory extends Event {
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

    public void setMemory(Memory memory) throws Exception {
        super.setCalendar(ConvertHelper.convertStringToDate(memory.getDate(), MainActivity.globals.getUserSettings().getDateFormat()));
        super.setName(memory.getTitle());
        super.setDescription(memory.getDescription());
        super.setId(memory.getId());
    }
}
