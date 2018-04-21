/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.marklist.de;

import android.content.Context;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.exceptions.MarkListException;
import de.domjos.schooltools.core.model.marklist.MarkListWithMarkMode;
import de.domjos.schooltools.core.utils.MathUtils;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to calculate a German List with Crease
 * @author Dominic Joas
 * @version 1.0
 */
public class GermanListWithCrease extends MarkListWithMarkMode {
    private Context context;
    private double bestMarkAt, worstMarkTo;
    private double customPoints, customMark;
    
    public GermanListWithCrease(Context context, int maxPoints) {
        super(context, maxPoints);
        this.context = context;
        this.bestMarkAt = super.getMaxPoints();
        this.worstMarkTo = 0.0;
        this.customPoints = super.getMaxPoints() / 2;
        this.customMark = (super.getMaxMark() - super.getMinMark()) / 2;
    }

    public double getBestMarkAt() {
        return this.bestMarkAt;
    }

    public void setBestMarkAt(double bestMarkAt) {
        this.bestMarkAt = bestMarkAt;
    }

    public double getWorstMarkTo() {
        return this.worstMarkTo;
    }

    public void setWorstMarkTo(double worstMarkTo) {
        this.worstMarkTo = worstMarkTo;
    }

    public double getCustomPoints() {
        return this.customPoints;
    }

    public void setCustomPoints(double customPoints) {
        this.customPoints = customPoints;
    }

    public double getCustomMark() {
        return this.customMark;
    }

    public void setCustomMark(double customMark) {
        this.customMark = customMark;
    }
    
    @Override
    protected void validateChildFields() throws MarkListException {
        if(this.bestMarkAt>super.getMaxPoints() || this.bestMarkAt<0) {
            throw new MarkListException(this.context, R.string.message_marklist_9, this);
        }
        if(this.worstMarkTo>super.getMaxPoints() || this.worstMarkTo<0) {
            throw new MarkListException(this.context, R.string.message_marklist_10, this);
        }
        if(this.bestMarkAt<this.worstMarkTo) {
            throw new MarkListException(this.context, R.string.message_marklist_11, this);
        }
        
        if(this.customPoints>super.getMaxPoints() || this.customPoints<=0) {
            throw new MarkListException(this.context, R.string.message_marklist_12, this);
        }
        if(this.customPoints>this.bestMarkAt || this.customPoints<this.worstMarkTo) {
            throw new MarkListException(this.context, R.string.message_marklist_13, this);
        }
        if(this.customMark>super.getMaxMark() || this.customMark<super.getMinMark()) {
            throw new MarkListException(this.context, R.string.message_marklist_14, this);
        }
    }

    @Override
    protected Map<Double, Double> calculateMarkList() throws MarkListException {
        Map<Double, Double> markList;
        if(super.isDictatMode()) {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), 0, super.getMinMark(), super.getMaxPoints()-this.bestMarkAt, super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints()-this.bestMarkAt, this.customMark, this.customPoints, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customMark, this.customPoints, super.getMaxMark(), super.getMaxPoints()-this.worstMarkTo, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints()-this.worstMarkTo, super.getMaxMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case worstMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints(), super.getMaxMark(), super.getMaxPoints()-this.worstMarkTo, super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints()-this.worstMarkTo, this.customMark, this.customPoints, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customMark, this.customPoints, super.getMinMark(), super.getMaxPoints()-this.bestMarkAt, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints()-this.bestMarkAt, super.getMinMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case highestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(0, super.getMinMark(), super.getMaxPoints()-this.bestMarkAt, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-this.bestMarkAt, super.getMinMark(), this.customPoints, this.customMark, super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customPoints, this.customMark, super.getMaxPoints()-this.worstMarkTo, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-this.worstMarkTo, super.getMaxMark(), super.getMaxPoints(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                case lowestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMaxMark(), super.getMaxPoints()-this.worstMarkTo, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-this.worstMarkTo, super.getMaxMark(), this.customPoints, this.customMark, super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customPoints, this.customMark, super.getMaxPoints()-this.bestMarkAt, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints()-this.bestMarkAt, super.getMinMark(), 0, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                default:
                    return null;
            }
        } else {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints(), super.getMinMark(), this.bestMarkAt, super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), this.bestMarkAt, this.customMark, this.customPoints, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customMark, this.customPoints, super.getMaxMark(), this.worstMarkTo, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), this.worstMarkTo, super.getMaxMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case worstMarkFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), 0, super.getMaxMark(), this.worstMarkTo, super.getMarkMultiplier(), super.getPointsMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), this.worstMarkTo, this.customMark, this.customPoints, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customMark, this.customPoints, super.getMinMark(), this.bestMarkAt, super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), this.bestMarkAt, super.getMinMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier(), markList);
                    break;
                case highestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMinMark(), this.bestMarkAt, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.bestMarkAt, super.getMinMark(), this.customPoints, this.customMark, super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customPoints, this.customMark, this.worstMarkTo, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.worstMarkTo, super.getMaxMark(), 0, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                case lowestPointsFirst:
                    markList = MathUtils.calculateLineBetweenTwoPoints(0, super.getMaxMark(), this.worstMarkTo, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.worstMarkTo, super.getMaxMark(), this.customPoints, this.customMark, super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.customPoints, this.customMark, this.bestMarkAt, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    markList = MathUtils.calculateLineBetweenTwoPoints(this.bestMarkAt, super.getMinMark(), super.getMaxPoints(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier(), markList);
                    break;
                default:
                    return null;
            }
        }
        return markList;
    }
}
