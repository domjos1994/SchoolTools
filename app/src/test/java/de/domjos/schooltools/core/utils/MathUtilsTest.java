/*
 * Copyright (C) 2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.domjos.schooltools.core.utils.fileUtils.CSVBridge;
import de.domjos.schooltools.core.utils.fileUtils.CSVObject;
import de.domjos.schooltools.helper.ApiHelper;
import de.domjos.schooltools.settings.MarkListSettings;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dominic Joas
 */

public class MathUtilsTest {

    @Test
    public void roundTest() {
        assertEquals(MathUtils.round(2.25, 0.5), 2.5, 0.0);
        assertEquals(MathUtils.round(2.225, 0.05), 2.25, 0.0);
    }

    @Test
    public void extendedRoundTest() {
        final double VAL = 4.334615384615384;
        assertEquals(MathUtils.round(VAL, 0.1), 4.3, 0.0);
    }

    @Test
    public void testCSVBridge() {
        CSVBridge bridge = new CSVBridge(";", "id;title;description;listDate;todo;");
        bridge.writeValue(1, "id", 1);
        bridge.writeValue(1, "title", "Wunschliste");
        bridge.writeValue(1, "description", "Dies ist die Wunschliste\nfür Weihnachten!");
        bridge.writeValue(1, "listDate", new Date());
        CSVObject csvObject = new CSVObject("|", 5);
        csvObject.writeValue("1", 1);
        csvObject.writeValue("2", "Playstation 4");
        csvObject.writeValue("3", 10.0);
        csvObject.writeValue("4", new Date(2019,12,31));
        csvObject.writeValue("5", false);
        CSVObject csvObject2 = new CSVObject("|", 5);
        csvObject2.writeValue("1", 2);
        csvObject2.writeValue("2", "XBOX one");
        csvObject2.writeValue("3", 9.0);
        csvObject2.writeValue("4", new Date(2019,12,31));
        csvObject2.writeValue("5", true);
        bridge.writeValue(1, "todo", Arrays.asList(csvObject, csvObject2), "(",")");
        bridge.addNewLine();
        bridge.writeValue(2, "id", 2);
        bridge.writeValue(2, "title", "Wunschliste 2");
        bridge.writeValue(2, "description", "Dies ist die Wunschliste\nfür Ostern!");
        bridge.writeValue(2, "listDate", new Date());
        CSVObject csvObject3 = new CSVObject("|", 5);
        csvObject3.writeValue("1", 3);
        csvObject3.writeValue("2", "Playstation 5");
        csvObject3.writeValue("3", 7);
        csvObject3.writeValue("4", new Date(2020,12,31));
        csvObject3.writeValue("5", false);
        CSVObject csvObject4 = new CSVObject("|", 5);
        csvObject4.writeValue("1", 2);
        csvObject4.writeValue("2", "Nintendo wii");
        csvObject4.writeValue("3", 6.0);
        csvObject4.writeValue("4", new Date(2020,12,31));
        csvObject4.writeValue("5", true);
        bridge.writeValue(2, "todo", Arrays.asList(csvObject3, csvObject4), "(",")");
        String content = bridge.toString();

        CSVBridge readableBridge = new CSVBridge(";", "", content, true);
        Date dt = readableBridge.readDateValue(2, "todo");
        List<CSVObject> objects = readableBridge.readObjectsValue(2, "todo", "|", "(", ")");
        System.out.println(objects);
    }
}
