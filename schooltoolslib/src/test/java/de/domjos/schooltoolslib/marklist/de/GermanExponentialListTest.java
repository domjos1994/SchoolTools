/*
 * Copyright (C) 2017-2022  Dominic Joas
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

import static junit.framework.Assert.assertEquals;

public class GermanExponentialListTest {
    private GermanExponentialList germanExponentialList;

    @Before
    public void setUp() {
        this.germanExponentialList = new GermanExponentialList(null, 20);
        this.germanExponentialList.setDictatMode(false);
        this.germanExponentialList.setMarkMultiplier(0.1);
        this.germanExponentialList.setPointsMultiplier(0.5);
        this.germanExponentialList.setViewMode(MarkListInterface.ViewMode.bestMarkFirst);
    }

    @Test
    public void testMarkListMax20() throws Exception {
        Map<Double, Double> marklist = this.germanExponentialList.calculate();
        for(Map.Entry<Double, Double> entry : marklist.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        assertEquals(marklist.get(1.0), 20.0, 0.0);
    }
}
