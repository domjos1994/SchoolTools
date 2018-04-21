/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.marklist;

import android.content.Context;

import de.domjos.schooltools.core.exceptions.MarkListException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract Mark-List-Class with a percentage-list
 * as base for the Mark-List-Calculation
 * @see de.domjos.schooltools.core.model.marklist.MarkList
 * @author Dominic Joas
 * @version 1.0
 */
public abstract class MarkListWithPercentage extends MarkList {
    private Map<Double, Double> percentages;
    private double minPercentage, maxPercentage;
    
    public MarkListWithPercentage(Context context, int maxPoints) {
        super(context, maxPoints);
        this.percentages = new LinkedHashMap<>();
        this.minPercentage = Integer.MAX_VALUE;
        this.maxPercentage = Integer.MIN_VALUE;
    }
    
    public Map<Double, Double> getPercentages() {
        return this.percentages;
    }
    
    public void addPercentage(double mark, double percentage) {
        this.percentages.put(mark, percentage);
        if(this.minPercentage>=percentage) {
            this.minPercentage = percentage;
            super.setMinMark((int)mark);
        }
        
        if(this.maxPercentage>=percentage) {
            this.maxPercentage = percentage;
            super.setMaxMark((int)mark);
        }
    }
    
    public void setPercentages(Map<Double, Double> percentages) {
        this.percentages = percentages;
    }
    
    @Override
    protected abstract void validateChildFields() throws MarkListException;

    @Override
    protected abstract Map<Double, Double> calculateMarkList() throws MarkListException;
}
