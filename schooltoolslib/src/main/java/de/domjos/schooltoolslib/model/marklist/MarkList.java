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

import de.domjos.schooltoolslib.R;
import de.domjos.schooltoolslib.exceptions.MarkListException;
import java.util.Map;

/**
 * Parent class of all Mark-Lists
 * @see de.domjos.schooltoolslib.model.marklist.MarkListInterface
 * @author Dominic Joas
 * @version 1.0
 */
public abstract class MarkList implements MarkListInterface{
    private Context context;
    private int maxPoints, minMark, maxMark;
    private double pointsMultiplier, markMultiplier;
    private boolean dictatMode;
    private ViewMode viewMode;
    
    public MarkList(Context context, int maxPoints) {
        this.maxPoints = maxPoints;
        this.minMark = 1;
        this.maxMark = 6;
        this.pointsMultiplier = 0.5;
        this.markMultiplier = 0.1;
        this.dictatMode = false;
        this.viewMode = ViewMode.bestMarkFirst;
        this.context = context;
    }
    
    @Override
    public int getMaxPoints() {
        return this.maxPoints;
    }

    @Override
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    protected int getMinMark() {
        return this.minMark;
    }

    protected void setMinMark(int minMark) {
        this.minMark = minMark;
    }

    protected int getMaxMark() {
        return this.maxMark;
    }

    protected void setMaxMark(int maxMark) {
        this.maxMark = maxMark;
    }

    @Override
    public double getPointsMultiplier() {
        return this.pointsMultiplier;
    }

    @Override
    public void setPointsMultiplier(double multiplier) {
        this.pointsMultiplier = multiplier;
    }
    
    @Override
    public double getMarkMultiplier() {
        return this.markMultiplier;
    }

    @Override
    public void setMarkMultiplier(double multiplier) {
        this.markMultiplier = multiplier;
    }

    @Override
    public ViewMode getViewMode() {
        return this.viewMode;
    }

    @Override
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public boolean isDictatMode() {
        return this.dictatMode;
    }

    @Override
    public void setDictatMode(boolean dictatMode) {
        this.dictatMode = dictatMode;
    }

    private void validateParentFields() throws MarkListException {
        if(this.maxPoints<=0) {
            throw new MarkListException(context, R.string.message_marklist_1, this);
        }
        if(this.maxPoints>=40000) {
            throw new MarkListException(context, R.string.message_marklist_2, this);
        }
        
        if(this.minMark<0) {
            throw new MarkListException(context, R.string.message_marklist_3, this);
        }
        if(this.minMark>this.maxPoints) {
            throw new MarkListException(context, R.string.message_marklist_4, this);
        }
        if(this.maxMark<0) {
            throw new MarkListException(context, R.string.message_marklist_5, this);
        }
        if(this.maxMark>this.maxPoints) {
            throw new MarkListException(context, R.string.message_marklist_6, this);
        }
        
        if(this.markMultiplier>5 || this.markMultiplier<=0) {
            throw new MarkListException(context, R.string.message_marklist_7, this);
        }
        if(this.pointsMultiplier>5 || this.pointsMultiplier<=0) {
            throw new MarkListException(context, R.string.message_marklist_8, this);
        }
    }
    
    protected abstract void validateChildFields() throws MarkListException;
    protected abstract Map<Double, Double> calculateMarkList() throws MarkListException;
    
    @Override
    public Map<Double, Double> calculate() throws MarkListException {
        this.validateParentFields();
        this.validateChildFields();
        return this.calculateMarkList();
    }

    @Override
    public String toString() {
        return this.context.getString(R.string.main_nav_mark_list) + "(" + this.getMaxPoints() + " P, Min:" + this.getMinMark() + ", Max:" + this.getMaxMark() + ", P:" + this.getPointsMultiplier() + ", M:" + this.getMarkMultiplier() + ", D:" + this.isDictatMode() + ")";
    }
}
