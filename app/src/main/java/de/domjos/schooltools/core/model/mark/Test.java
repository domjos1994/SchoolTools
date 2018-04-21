/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.mark;

import java.util.Date;

/**
 * Model of a Test in the school
 * @see de.domjos.schooltools.adapter.TestAdapter
 * @author Dominic Joas
 * @version 1.0
 */
public class Test {
    private int ID;
    private double weight, mark, average;
    private String title, description, themes;
    private Date testDate;
    private Date memoryDate;

    public Test() {
        this.ID = 0;
        this.weight = 0.0;
        this.mark = 0.0;
        this.average = 0.0;
        this.title = "";
        this.description = "";
        this.themes = "";
        this.testDate = null;
        this.memoryDate = null;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMark() {
        return this.mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public double getAverage() {
        return this.average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThemes() {
        return this.themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public Date getTestDate() {
        return this.testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public Date getMemoryDate() {
        return this.memoryDate;
    }

    public void setMemoryDate(Date memoryDate) {
        this.memoryDate = memoryDate;
    }
}
