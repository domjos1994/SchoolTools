/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.marklist.de;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import de.domjos.schooltools.core.model.marklist.MarkListInterface;

import static junit.framework.Assert.assertEquals;

public class GermanIHKListTest {
    private GermanIHKList germanIHKList;

    @Before
    public void setUp() {
        this.germanIHKList = new GermanIHKList(null, 20);
        this.germanIHKList.setDictatMode(false);
        this.germanIHKList.setMarkMultiplier(0.1);
        this.germanIHKList.setPointsMultiplier(0.5);
        this.germanIHKList.setViewMode(MarkListInterface.ViewMode.bestMarkFirst);
    }

    @Test
    public void testMarkListMax20() throws Exception {
        Map<Double, Double> markList = this.germanIHKList.calculate();
        assertEquals(markList.get(1.0), 20.0, 0.0);
        assertEquals(markList.get(2.0), 17.0, 0.0);
        assertEquals(markList.get(3.0), 14.5, 0.0);
        assertEquals(markList.get(4.0), 11.5, 0.0);
        assertEquals(markList.get(5.0), 8.0, 0.0);
        assertEquals(markList.get(6.0), 1.0, 0.0);
    }
}
