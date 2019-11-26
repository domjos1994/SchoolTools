/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model.mark;

import java.util.Date;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;

/**
 * Model of a Test in the school
 * @author Dominic Joas
 * @version 1.0
 */
public class Test extends BaseDescriptionObject {
    private double weight, mark, average;
    private String themes;
    private Date testDate;
    private Date memoryDate;

    public Test() {
        super();
        this.weight = 0.0;
        this.mark = 0.0;
        this.average = 0.0;
        this.themes = "";
        this.testDate = null;
        this.memoryDate = null;
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

    public String getThemes() {
        return this.themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public Date getTestDate() {
        if(this.testDate!=null) {
            return (Date) this.testDate.clone();
        } else {
            return null;
        }
    }

    public void setTestDate(Date testDate) {
        if(testDate!=null) {
            this.testDate = (Date)testDate.clone();
        } else {
            this.testDate = null;
        }
    }

    public Date getMemoryDate() {
        if(this.memoryDate != null) {
            return (Date) this.memoryDate.clone();
        } else {
            return null;
        }
    }

    public void setMemoryDate(Date memoryDate) {
        if(memoryDate!=null) {
            this.memoryDate = (Date) memoryDate.clone();
        } else {
            this.memoryDate = null;
        }
    }
}
