/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.marklist.de;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.Map;

import de.domjos.schooltools.core.exceptions.MarkListException;
import de.domjos.schooltools.core.model.marklist.MarkListWithPercentage;
import de.domjos.schooltools.core.utils.MathUtils;

/**
 * @author Dominic Joas
 */

public class GermanIHKList extends MarkListWithPercentage {

    public GermanIHKList(Context context, int maxPoints) {
        super(context, maxPoints);
        super.addPercentage(1.0, 100);
        super.addPercentage(1.1, 99);
        super.addPercentage(1.2, 97);
        super.addPercentage(1.3, 95);
        super.addPercentage(1.4, 93);
        super.addPercentage(1.5, 91);
        super.addPercentage(1.6, 90);
        super.addPercentage(1.7, 89);
        super.addPercentage(1.8, 88);
        super.addPercentage(1.9, 87);
        super.addPercentage(2.0, 86);
        super.addPercentage(2.1, 84);
        super.addPercentage(2.2, 83);
        super.addPercentage(2.3, 82);
        super.addPercentage(2.4, 81);
        super.addPercentage(2.5, 80);
        super.addPercentage(2.6, 79);
        super.addPercentage(2.7, 78);
        super.addPercentage(2.8, 76);
        super.addPercentage(2.9, 75);
        super.addPercentage(3.0, 73);
        super.addPercentage(3.1, 72);
        super.addPercentage(3.2, 70);
        super.addPercentage(3.3, 69);
        super.addPercentage(3.4, 67);
        super.addPercentage(3.5, 66);
        super.addPercentage(3.6, 65);
        super.addPercentage(3.7, 63);
        super.addPercentage(3.8, 61);
        super.addPercentage(3.9, 60);
        super.addPercentage(4.0, 58);
        super.addPercentage(4.1, 56);
        super.addPercentage(4.2, 54);
        super.addPercentage(4.3, 53);
        super.addPercentage(4.4, 51);
        super.addPercentage(4.5, 49);
        super.addPercentage(4.6, 48);
        super.addPercentage(4.7, 46);
        super.addPercentage(4.8, 44);
        super.addPercentage(4.9, 42);
        super.addPercentage(5.0, 40);
        super.addPercentage(5.1, 37);
        super.addPercentage(5.2, 35);
        super.addPercentage(5.3, 33);
        super.addPercentage(5.4, 31);
        super.addPercentage(5.5, 29);
        super.addPercentage(5.6, 28);
        super.addPercentage(5.7, 22);
        super.addPercentage(5.8, 16);
        super.addPercentage(5.9, 11);
        super.addPercentage(6.0, 5);
        super.setMinMark(1);
        super.setMaxMark(6);
    }

    @Override
    protected void validateChildFields() {

    }

    @Override
    protected Map<Double, Double> calculateMarkList() {
        Map<Double, Double> marklist = new LinkedHashMap<>();

        for(double i = super.getMinMark(); i<=super.getMaxMark(); i+=super.getMarkMultiplier()) {
            double percentage = super.getPercentages().get(MathUtils.round(i, 0.1));
            double percentagePoints = (super.getMaxPoints() / 100.0) * percentage;
            double mark = MathUtils.round(i, super.getMarkMultiplier());
            double points = MathUtils.round(percentagePoints, super.getPointsMultiplier());

            if(super.isDictatMode()) {
                switch(super.getViewMode()) {
                    case bestMarkFirst:
                        if(points % this.getMaxPoints() != 0) {
                            points = points % this.getMaxPoints();
                        }
                        marklist.put(mark, points);
                        break;
                    case worstMarkFirst:
                        if(mark % this.getMaxMark() != 0) {
                            mark = mark % this.getMaxMark();
                        }
                        marklist.put(mark, points);
                        break;
                    case highestPointsFirst:
                        if(points % this.getMaxPoints() != 0) {
                            points = points % this.getMaxPoints();
                        }
                        marklist.put(points, mark);
                        break;
                    case lowestPointsFirst:
                        if(mark % this.getMaxMark() != 0) {
                            mark = mark % this.getMaxMark();
                        }
                        marklist.put(points, mark);
                        break;
                    default:
                        return null;
                }
            } else {
                switch(super.getViewMode()) {
                    case bestMarkFirst:
                        marklist.put(mark, points);
                        break;
                    case worstMarkFirst:
                        if(points % this.getMaxPoints() != 0) {
                            points = points % this.getMaxPoints();
                        }
                        if(mark % this.getMaxMark() != 0) {
                            mark = mark % this.getMaxMark();
                        }
                        marklist.put(mark, points);
                        break;
                    case highestPointsFirst:
                        marklist.put(points, mark);
                        break;
                    case lowestPointsFirst:
                        if(points % this.getMaxPoints() != 0) {
                            points = points % this.getMaxPoints();
                        }
                        if(mark % this.getMaxMark() != 0) {
                            mark = mark % this.getMaxMark();
                        }
                        marklist.put(points, mark);
                        break;
                    default:
                        return null;
                }
            }
        }

        return marklist;
    }
}
