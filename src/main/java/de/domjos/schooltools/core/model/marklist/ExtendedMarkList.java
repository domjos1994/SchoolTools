/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.marklist;

import android.content.Context;

public abstract class ExtendedMarkList extends MarkListWithMarkMode {
    private double bestMarkAt, worstMarkTo;
    private double customPoints, customMark;

    public ExtendedMarkList(Context context, int maxPoints) {
        super(context, maxPoints);
        this.bestMarkAt = super.getMaxPoints();
        this.worstMarkTo = 0.0;
        this.customPoints = super.getMaxPoints() / 2.0;
        this.customMark = (super.getMaxMark() - super.getMinMark()) / 2.0;
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
}
