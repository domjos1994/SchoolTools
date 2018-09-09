/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.utils.fileUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CSVBridgeTest {
    private String content;

    @Before
    public void init() {
        CSVBridge bridge = new CSVBridge(";", "id;TITLE_PARAM;description;listDate;todo;");
        bridge.writeValue(1, "id", 1);
        bridge.writeValue(1, "TITLE_PARAM", "Wunschliste");
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
        bridge.writeValue(2, "TITLE_PARAM", "Wunschliste 2");
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
        this.content = bridge.toString();
    }

    @Test
    public void testCSVWriting() {
        assertTrue(!this.content.isEmpty());
    }

    @Test
    public void testCSVReading() {
        CSVBridge readableBridge = new CSVBridge(";", "", this.content, true);
        List<CSVObject> objects = readableBridge.readObjectsValue(2, "todo", "|", "(", ")");
        assertEquals(objects.get(1).readDoubleValue("3"), 6.0);
    }
}
