/*
 * Copyright (C) 2017-2019  Dominic Joas
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

    /**
     * Gets the maximum number of points
     * @return the maximum number of points
     */
    int getMaxPoints();

    /**
     * Sets the maximum number of points
     * @param maxPoints the maximum number of points
     */
    void setMaxPoints(int maxPoints);


    /**
     * Gets the Mark-Multiplier
      * @return the Mark-Multiplier
     */
    double getMarkMultiplier();

    /**
     * Sets the Mark-Multiplier
     * @param multiplier the Mark-Multiplier
     */
    void setMarkMultiplier(double multiplier);


    /**
     * Gets the Point-Multiplier
     * @return the Point-Multiplier
     */
    double getPointsMultiplier();

    /**
     * Sets the Point-Multiplier
     * @param multiplier the Point-Multiplier
     */
    void setPointsMultiplier(double multiplier);


    /**
     * Gets the View-Mode
     * @return the View-Mode
     */
    ViewMode getViewMode();

    /**
     * Sets the View-Mode
     * @param viewMode the View-Mode
     */
    void setViewMode(ViewMode viewMode);


    /**
     * Gets the Dictat-Mode
     * @return the Dictat-Mode
     */
    boolean isDictatMode();

    /**
     * Sets the Dictat-Mode
     * @param dictatMode the Dictat-Mode
     */
    void setDictatMode(boolean dictatMode);

    /**
     * Calculates the Mark-List
     * @return Map with Mark-List in it
     * @throws MarkListException
     */
    Map<Double, Double> calculate() throws MarkListException;

    /**
     * Enum with the View-Mode
     */
    enum ViewMode {
        bestMarkFirst,
        worstMarkFirst,
        highestPointsFirst,
        lowestPointsFirst
    }
}