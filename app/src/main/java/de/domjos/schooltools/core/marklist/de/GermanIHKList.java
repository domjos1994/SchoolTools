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
    protected void validateChildFields() throws MarkListException {

    }

    @Override
    protected Map<Double, Double> calculateMarkList() throws MarkListException {
        Map<Double, Double> marklist = new LinkedHashMap<>();

        if(super.isDictatMode()) {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    for(double i = super.getMinMark(); i <= super.getMaxMark(); i += super.getMarkMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            if(i<=entry.getKey()) {
                                if(entry.getValue()==0.0) {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        super.getMaxPoints() - MathUtils.round(0.0, super.getPointsMultiplier())
                                    );
                                } else {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        super.getMaxPoints() - MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier())
                                    );
                                }
                                break;
                            }
                        }
                    }
                    break;
                case worstMarkFirst:
                    for(double i = super.getMaxMark(); i > super.getMinMark(); i -= super.getMarkMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            if(i<=entry.getKey()) {
                                if(entry.getValue()==0) {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        super.getMaxPoints() - MathUtils.round(0.0, super.getPointsMultiplier())
                                    );
                                } else {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        super.getMaxPoints() - MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier())
                                    );
                                }
                                break;
                            }
                        }
                    }
                    break;
                case highestPointsFirst:
                    for(double i = 0; i <= super.getMaxPoints(); i += super.getPointsMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            double points = 0.0;
                            if(entry.getValue()!=0.0) {
                                points = MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier());
                            }
                            if(i<=points) {
                                marklist.put(super.getMaxPoints() - points, MathUtils.round(entry.getKey(), super.getMarkMultiplier()));
                                break;
                            }
                        }
                    }
                    break;
                case lowestPointsFirst:
                    for(double i = super.getMaxPoints(); i > 0; i -= super.getPointsMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            double points = 0.0;
                            if(entry.getValue()!=0.0) {
                                points = MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier());
                            }
                            if(i<=points) {
                                marklist.put(super.getMaxPoints() - points, MathUtils.round(entry.getKey(), super.getMarkMultiplier()));
                                break;
                            }
                        }
                    }
                    break;
                default:
                    return null;
            }
        } else {
            switch(super.getViewMode()) {
                case bestMarkFirst:
                    for(double i = super.getMinMark(); i <= super.getMaxMark(); i += super.getMarkMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            if(i<=entry.getKey()) {
                                if(entry.getValue()==0.0) {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        MathUtils.round(0.0, super.getPointsMultiplier())
                                    );
                                } else {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier())
                                    );
                                }

                                break;
                            }
                        }
                    }
                    break;
                case worstMarkFirst:
                    for(double i = super.getMaxMark(); i > super.getMinMark(); i -= super.getMarkMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            if(i<=entry.getKey()) {
                                if(entry.getValue()==0.0) {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        MathUtils.round(0.0, super.getPointsMultiplier())
                                    );
                                } else {
                                    marklist.put(
                                        MathUtils.round(i, super.getMarkMultiplier()),
                                        MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier())
                                    );
                                }
                                break;
                            }
                        }
                    }
                    break;
                case highestPointsFirst:
                    for(double i = 0; i <= super.getMaxPoints(); i += super.getPointsMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            double points = 0.0;
                            if(entry.getValue()!=0.0) {
                                points = MathUtils.round((entry.getValue() / 100) * i, super.getPointsMultiplier());
                            }
                            if(i<=points) {
                                marklist.put(points, MathUtils.round(entry.getKey(), super.getMarkMultiplier()));
                                break;
                            }
                        }
                    }
                    break;
                case lowestPointsFirst:
                    for(double i = super.getMaxPoints(); i > 0; i -= super.getPointsMultiplier()) {
                        for(Map.Entry<Double, Double> entry : super.getPercentages().entrySet()) {
                            double points = 0.0;
                            if(entry.getValue()!=0.0) {
                                points = MathUtils.round((entry.getValue() / 100) * super.getMaxPoints(), super.getPointsMultiplier());
                            }
                            if(i<=points) {
                                marklist.put(points, MathUtils.round(entry.getKey(), super.getMarkMultiplier()));
                                break;
                            }
                        }
                    }
                    break;
                default:
                    return null;
            }
        }

        return marklist;
    }
}
