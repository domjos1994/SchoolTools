/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.marklist.de;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import de.domjos.schooltoolslib.model.marklist.MarkListInterface;
import de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dominic Joas
 */

public class GermanListWithCreaseTest {
    private GermanListWithCrease germanListWithCrease;

    @Before
    public void setUp() {
        this.germanListWithCrease = new GermanListWithCrease(null, 20);
        this.germanListWithCrease.setDictatMode(false);
        this.germanListWithCrease.setMarkMultiplier(0.1);
        this.germanListWithCrease.setPointsMultiplier(0.5);
        this.germanListWithCrease.setMarkMode(MarkListWithMarkMode.MarkMode.normalMarks);
        this.germanListWithCrease.setViewMode(MarkListInterface.ViewMode.bestMarkFirst);
        this.germanListWithCrease.setWorstMarkTo(4.0);
        this.germanListWithCrease.setBestMarkAt(16.0);
        this.germanListWithCrease.setCustomPoints(10.0);
        this.germanListWithCrease.setCustomMark(4.4);
    }

    @Test
    public void testMarkListMax20() throws Exception {
        Map<Double, Double> markList = this.germanListWithCrease.calculate();
        assertEquals(markList.get(5.2), 7.0, 0.0);
        assertEquals(markList.get(4.4), 10.0, 0.0);
        assertEquals(markList.get(2.7), 13.0, 0.0);
        assertEquals(markList.get(1.0), 20.0, 0.0);
    }

    @Test
    public void testMarkListMax36() throws Exception {
        this.germanListWithCrease.setMaxPoints(40);
        this.germanListWithCrease.setBestMarkAt(36.0);
        this.germanListWithCrease.setViewMode(MarkListInterface.ViewMode.highestPointsFirst);

        Map<Double, Double> markList = this.germanListWithCrease.calculate();
        Assert.assertEquals(markList.get(4.0), 6.0, 0.0);
        Assert.assertEquals(markList.get(7.0), 5.2, 0.0);
        Assert.assertEquals(markList.get(10.0), 4.4, 0.0); // unRounded: 4.33
        Assert.assertEquals(markList.get(14.0), 3.9, 0.0);
        Assert.assertEquals(markList.get(17.0), 3.5, 0.0);
        Assert.assertEquals(markList.get(21.0), 3.0, 0.0);
        Assert.assertEquals(markList.get(24.0), 2.6, 0.0);
        Assert.assertEquals(markList.get(28.0), 2.0, 0.0);
        Assert.assertEquals(markList.get(31.0), 1.7, 0.0);
        Assert.assertEquals(markList.get(35.0), 1.1, 0.0);
    }

    @Test
    public void testMarkListBugMax20() throws Exception {
        this.germanListWithCrease.setMaxPoints(20);
        this.germanListWithCrease.setMarkMultiplier(0.1);
        this.germanListWithCrease.setPointsMultiplier(0.5);
        this.germanListWithCrease.setBestMarkAt(18.0);
        this.germanListWithCrease.setWorstMarkTo(2.0);
        this.germanListWithCrease.setViewMode(MarkListInterface.ViewMode.lowestPointsFirst);

        Map<Double, Double> markList = this.germanListWithCrease.calculate();
        Assert.assertEquals(markList.get(1.0), 6.0, 0.0);
        Assert.assertEquals(markList.get(7.0), 5.2, 0.1);
        Assert.assertEquals(markList.get(10.5), 4.4, 0.0); // unRounded: 4.33
        Assert.assertEquals(markList.get(14.0), 3.9, 0.0);
        Assert.assertEquals(markList.get(17.5), 3.5, 0.0);
        Assert.assertEquals(markList.get(21.0), 3.0, 0.0);
        Assert.assertEquals(markList.get(24.5), 2.6, 0.0);
        Assert.assertEquals(markList.get(28.0), 2.0, 0.2);
        Assert.assertEquals(markList.get(31.5), 1.7, 0.2);
        Assert.assertEquals(markList.get(35.0), 1.1, 0.2);
    }
}
