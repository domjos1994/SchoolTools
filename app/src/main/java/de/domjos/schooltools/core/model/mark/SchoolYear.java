/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.mark;

import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.core.model.Subject;

/**
 * Model of a School-Year
 * @see de.domjos.schooltools.adapter.YearAdapter
 * @author Dominic Joas
 * @version 1.0
 */
public class SchoolYear {
    private int ID;
    private Year year;
    private Subject subject;
    private List<Test> tests;

    public SchoolYear() {
        this.ID = 0;
        this.year = null;
        this.subject = null;
        this.tests = new LinkedList<>();
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Test> getTests() {
        return this.tests;
    }

    public void addTest(Test test) {
        this.tests.add(test);
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public double calculateAverage() {
        if(!this.tests.isEmpty()) {
            double sumMarks = 0.0, marks = 0.0;
            for(Test test : this.tests) {
                if(test.getMark()!=0.0) {
                    sumMarks += (test.getMark() * test.getWeight());
                    marks += test.getWeight();
                }
            }
            return (sumMarks / marks);
        } else {
            return 0.0;
        }
    }
}
