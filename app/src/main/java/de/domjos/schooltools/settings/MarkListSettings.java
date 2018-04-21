/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.settings;

public class MarkListSettings {
    private int id;
    private String title;
    private int type, maxPoints, marMode, viewMode;
    private double customPoints, customMark, bestMarkAt, worstMarkTo;
    private boolean dictatMode, halfPoints, tenthMarks;

    public MarkListSettings(String title) {
        this.id = 0;
        this.title = title;
        this.type = 0;
        this.maxPoints = 20;
        this.marMode = 0;
        this.viewMode = 2;
        this.customPoints = 10.0;
        this.customMark = 3.5;
        this.bestMarkAt = 20.0;
        this.worstMarkTo = 0.0;
        this.dictatMode = false;
        this.halfPoints = false;
        this.tenthMarks = true;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxPoints() {
        return this.maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public int getMarMode() {
        return this.marMode;
    }

    public void setMarMode(int marMode) {
        this.marMode = marMode;
    }

    public int getViewMode() {
        return this.viewMode;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
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

    public int getDictatMode() {
        if(this.dictatMode) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isDictatMode() {
        return this.dictatMode;
    }

    public void setDictatMode(boolean dictatMode) {
        this.dictatMode = dictatMode;
    }

    public void setDictatMode(int dictatMode) {
        this.dictatMode = dictatMode==1;
    }

    public int getHalfPoints() {
        if(this.halfPoints) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isHalfPoints() {
        return this.halfPoints;
    }

    public void setHalfPoints(boolean halfPoints) {
        this.halfPoints = halfPoints;
    }

    public void setHalfPoints(int halfPoints) {
        this.halfPoints = halfPoints==1;
    }

    public int getTenthMarks() {
        if(this.tenthMarks) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isTenthMarks() {
        return this.tenthMarks;
    }

    public void setTenthMarks(boolean tenthMarks) {
        this.tenthMarks = tenthMarks;
    }

    public void setTenthMarks(int tenthMarks) {
        this.tenthMarks = tenthMarks==1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
