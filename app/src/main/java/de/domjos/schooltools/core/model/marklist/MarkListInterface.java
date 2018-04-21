/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.model.marklist;

import de.domjos.schooltools.core.exceptions.MarkListException;
import java.util.Map;

/**
 * Interface for the Mark-Lists
 * @author Dominic Joas
 * @version 1.0
 */
public interface MarkListInterface {
    
    int getMaxPoints();
    void setMaxPoints(int maxPoints);
    
    
    double getMarkMultiplier();
    void setMarkMultiplier(double multiplier);
    
    double getPointsMultiplier();
    void setPointsMultiplier(double multiplier);
    
    
    ViewMode getViewMode();
    void setViewMode(ViewMode viewMode);
    
    boolean isDictatMode();
    void setDictatMode(boolean dictatMode);
    
    Map<Double, Double> calculate() throws MarkListException;
    
    enum ViewMode {
        bestMarkFirst,
        worstMarkFirst,
        highestPointsFirst,
        lowestPointsFirst
    }
}