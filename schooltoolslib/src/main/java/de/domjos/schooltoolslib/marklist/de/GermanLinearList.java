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

import de.domjos.schooltoolslib.exceptions.MarkListException;
import de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode;
import de.domjos.schooltoolslib.utils.MathUtils;
import java.util.Map;

/**
 * Class to calculate a German Linear-List
 * @see de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode
 * @author Dominic Joas
 * @version 1.0
 */
public class GermanLinearList extends MarkListWithMarkMode {
    
    public GermanLinearList(Context context, int maxPoints) {
        super(context, maxPoints);
    }
    
    @Override
    protected void validateChildFields() throws MarkListException {
        // nothing to do here
    }

    @Override
    protected Map<Double, Double> calculateMarkList() throws MarkListException {
        if(super.isDictatMode()) {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), 0, super.getMaxMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier());
                case worstMarkFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), super.getMaxPoints(), super.getMinMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier());
                case highestPointsFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(0, super.getMinMark(), super.getMaxPoints(), super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                case lowestPointsFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMaxMark(), 0, super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                default:
                    return null;
            }
        } else {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMinMark(), super.getMaxPoints(), super.getMaxMark(), 0, super.getMarkMultiplier(), super.getPointsMultiplier());
                case worstMarkFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMaxMark(), 0, super.getMinMark(), super.getMaxPoints(), super.getMarkMultiplier(), super.getPointsMultiplier());
                case highestPointsFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(super.getMaxPoints(), super.getMinMark(), 0, super.getMaxMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                case lowestPointsFirst:
                    return MathUtils.calculateLineBetweenTwoPoints(0, super.getMaxMark(), super.getMaxPoints(), super.getMinMark(), super.getPointsMultiplier(), super.getMarkMultiplier());
                default:
                    return null;
            }
        }
    }
}
