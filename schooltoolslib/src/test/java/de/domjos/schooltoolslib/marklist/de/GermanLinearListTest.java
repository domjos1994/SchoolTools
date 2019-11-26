
/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.marklist.de;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import de.domjos.schooltoolslib.model.marklist.MarkListInterface;
import de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode;

import static org.junit.Assert.assertEquals;

public class GermanLinearListTest {
    private GermanLinearList linearList;

    @Before
    public void setUp() {
        this.linearList = new GermanLinearList(null, 20);
        this.linearList.setDictatMode(false);
        this.linearList.setMarkMultiplier(0.1);
        this.linearList.setPointsMultiplier(0.5);
        this.linearList.setMarkMode(MarkListWithMarkMode.MarkMode.normalMarks);
        this.linearList.setViewMode(MarkListInterface.ViewMode.bestMarkFirst);
    }

    @Test
    public void testMarkListMax20() throws Exception {
        Map<Double, Double> markList = this.linearList.calculate();
        assertEquals(markList.get(5.1), 3.5, 0.0);
        assertEquals(markList.get(4.3), 7.0, 0.0);
        assertEquals(markList.get(3.4), 10.5, 0.0);
        assertEquals(markList.get(2.5), 14.0, 0.0);
        assertEquals(markList.get(1.6), 17.5, 0.0);
    }

    @Test
    public void testMarkListMax36() throws Exception {
        this.linearList.setMaxPoints(36);
        this.linearList.setViewMode(MarkListInterface.ViewMode.highestPointsFirst);

        Map<Double, Double> markList = this.linearList.calculate();
        assertEquals(markList.get(3.5), 5.5, 0.0);
        assertEquals(markList.get(7.0), 5.0, 0.0);
        assertEquals(markList.get(10.5), 4.5, 0.0);
        assertEquals(markList.get(14.0), 4.1, 0.0);
        assertEquals(markList.get(17.5), 3.6, 0.0);
        assertEquals(markList.get(21.0), 3.1, 0.0);
        assertEquals(markList.get(24.5), 2.6, 0.0);
        assertEquals(markList.get(28.0), 2.1, 0.0);
        assertEquals(markList.get(31.5), 1.6, 0.0);
        assertEquals(markList.get(35.0), 1.1, 0.0);
    }
}
