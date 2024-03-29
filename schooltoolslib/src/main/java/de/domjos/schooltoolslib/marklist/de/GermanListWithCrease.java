/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltoolslib.marklist.de;

import android.content.Context;

import de.domjos.schooltoolslib.R;
import de.domjos.schooltoolslib.exceptions.MarkListException;
import de.domjos.schooltoolslib.model.marklist.ExtendedMarkList;
import de.domjos.schooltoolslib.utils.MathUtils;
import java.util.Map;

/**
 * Class to calculate a German List with Crease
 * @author Dominic Joas
 * @version 1.0
 */
public class GermanListWithCrease extends ExtendedMarkList {
    private Context context;
    
    public GermanListWithCrease(Context context, int maxPoints) {
        super(context, maxPoints);
        this.context = context;
    }
    
    @Override
    protected void validateChildFields() throws MarkListException {
        if(super.getBestMarkAt()>super.getMaxPoints() || super.getBestMarkAt()<0) {
            throw new MarkListException(this.context, R.string.message_marklist_9, this);
        }
        if(super.getWorstMarkTo()>super.getMaxPoints() || super.getWorstMarkTo()<0) {
            throw new MarkListException(this.context, R.string.message_marklist_10, this);
        }
        if(super.getBestMarkAt()<super.getWorstMarkTo()) {
            throw new MarkListException(this.context, R.string.message_marklist_11, this);
        }
        
        if(super.getCustomPoints()>super.getMaxPoints() || super.getCustomPoints()<=0) {
            throw new MarkListException(this.context, R.string.message_marklist_12, this);
        }
        if(super.getCustomPoints()>super.getBestMarkAt() || super.getCustomPoints()<super.getWorstMarkTo()) {
            throw new MarkListException(this.context, R.string.message_marklist_13, this);
        }
        if(super.getCustomMark()>super.getMaxMark() || super.getCustomMark()<super.getMinMark()) {
            throw new MarkListException(this.context, R.string.message_marklist_14, this);
        }
    }

    @Override
    protected Map<Double, Double> calculateMarkList() {
        Map<Double, Double> markList;
        if(super.isDictatMode()) {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), 0, super.getMinMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getCustomMark(), super.getCustomPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomMark(), super.getCustomPoints(), super.getMaxMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getMaxMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case worstMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints(), super.getMaxMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getCustomMark(), super.getCustomPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomMark(), super.getCustomPoints(), super.getMinMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getMinMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case highestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(0, super.getMinMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-super.getBestMarkAt(), super.getMinMark(), super.getCustomPoints(), super.getCustomMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomPoints(), super.getCustomMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-super.getWorstMarkTo(), super.getMaxMark(), super.getMaxPoints(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                case lowestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMaxMark(), super.getMaxPoints()-super.getWorstMarkTo(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-super.getWorstMarkTo(), super.getMaxMark(), super.getCustomPoints(), super.getCustomMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomPoints(), super.getCustomMark(), super.getMaxPoints()-super.getBestMarkAt(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-super.getBestMarkAt(), super.getMinMark(), 0, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                default:
                    return null;
            }
        } else {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints(), super.getMinMark(), super.getBestMarkAt(), super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getBestMarkAt(), super.getCustomMark(), super.getCustomPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomMark(), super.getCustomPoints(), super.getMaxMark(), super.getWorstMarkTo(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getWorstMarkTo(), super.getMaxMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case worstMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), 0, super.getMaxMark(), super.getWorstMarkTo(), super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getWorstMarkTo(), super.getCustomMark(), super.getCustomPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomMark(), super.getCustomPoints(), super.getMinMark(), super.getBestMarkAt(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getBestMarkAt(), super.getMinMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case highestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMinMark(), super.getBestMarkAt(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getBestMarkAt(), super.getMinMark(), super.getCustomPoints(), super.getCustomMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomPoints(), super.getCustomMark(), super.getWorstMarkTo(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getWorstMarkTo(), super.getMaxMark(), 0, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                case lowestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(0, super.getMaxMark(), super.getWorstMarkTo(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getWorstMarkTo(), super.getMaxMark(), super.getCustomPoints(), super.getCustomMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getCustomPoints(), super.getCustomMark(), super.getBestMarkAt(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getBestMarkAt(), super.getMinMark(), super.getMaxPoints(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                default:
                    return null;
            }
        }
        return markList;
    }
}
