/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltoolslib.model.marklist;

import android.content.Context;

import de.domjos.schooltoolslib.exceptions.MarkListException;
import java.util.Map;

/**
 * Abstract Mark-List-Class with Mark-Modes instead of
 * Getters and Setters for the Min- and Max-Mark.
 * @see de.domjos.schooltoolslib.model.marklist.MarkList
 * @author Dominic Joas
 * @version 1.0
 */
public abstract class MarkListWithMarkMode extends MarkList {
    private MarkMode markMode;
    
    public MarkListWithMarkMode(Context context, int maxPoints) {
        super(context, maxPoints);
        this.markMode = MarkMode.normalMarks;
    }

    public MarkMode getMarkMode() {
        return this.markMode;
    }

    public void setMarkMode(MarkMode markMode) {
        if(markMode==MarkMode.normalMarks) {
            super.setMinMark(1);
            super.setMaxMark(6);
        } else {
            super.setMinMark(15);
            super.setMaxMark(0);
        }
        this.markMode = markMode;
    }
    
    @Override
    protected abstract void validateChildFields() throws MarkListException;

    @Override
    protected abstract Map<Double, Double> calculateMarkList() throws MarkListException;
    
    public enum MarkMode {
        normalMarks,
        pointMarks
    }
}
