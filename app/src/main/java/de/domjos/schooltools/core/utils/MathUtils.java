/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utils-Class for mathematical functions
 * @see de.domjos.schooltools.core.marklist.de.GermanLinearList
 * @see de.domjos.schooltools.core.marklist.de.GermanListWithCrease
 * @author Dominic Joas
 * @version 1.0
 */
public class MathUtils {

    /**
     * Function which calculates a line between to points
     * and adds the rounded values to a new map
     * @see #calculateLineBetweenTwoPoints(double, double, double, double, double, double, Map)
     * @param x1 X-Value (Point 1)
     * @param y1 Y-Value (Point 1)
     * @param x2 X-Value (Point 2)
     * @param y2 Y-Value (Point 2)
     * @param xMultiplier X-Multiplier to round X-Values
     * @param yMultiplier Y-Multiplier to round Y-Values
     * @return the Map with the Values
     */
    public static Map<Double, Double> calculateLineBetweenTwoPoints(double x1, double y1, double x2, double y2, double xMultiplier, double yMultiplier) {
        return MathUtils.calculateLineBetweenTwoPoints(x1, y1, x2, y2, xMultiplier, yMultiplier, new LinkedHashMap<Double, Double>());
    }

    /**
     * Function which calculates a line between to points
     * and adds the rounded values to an available map
     * @param x1 X-Value (Point 1)
     * @param y1 Y-Value (Point 1)
     * @param x2 X-Value (Point 2)
     * @param y2 Y-Value (Point 2)
     * @param xMultiplier X-Multiplier to round X-Values
     * @param yMultiplier Y-Multiplier to round Y-Values
     * @param values map to add the values
     * @return the Map with the Values
     */
    public static Map<Double, Double> calculateLineBetweenTwoPoints(double x1, double y1, double x2, double y2, double xMultiplier, double yMultiplier, Map<Double, Double> values) {
        int i = 0;
        if(x1<x2) {
            for(double curX = x1; curX <= x2; curX += xMultiplier, i++) {
                if(curX==16) {
                    int test = 0;
                }
                if(y1<y2) {
                    double factor = ((y2 - y1) / ((x2 - x1) / xMultiplier));
                    double key = MathUtils.round(curX, xMultiplier);
                    if(!values.containsKey(key)) {
                        values.put(key, MathUtils.round(y1 + (i * factor), yMultiplier));
                    }
                } else {
                    double factor = ((y1 - y2) / ((x2 - x1) / xMultiplier));
                    double key = MathUtils.round(curX, xMultiplier);
                    if(!values.containsKey(key)) {
                        values.put(key, MathUtils.round(y1 - (i * factor), yMultiplier));
                    }
                }
            }
        } else if(x1==x2) {
            values.put(MathUtils.round(x1, xMultiplier), MathUtils.round(y1, yMultiplier));
        } else {
            for(double curX = x1; curX >= x2; curX -= xMultiplier, i++) {
                if(y1<y2) {
                    double factor = ((y2 - y1) / ((x1 - x2) / xMultiplier));
                    double key = MathUtils.round(curX, xMultiplier);
                    if(!values.containsKey(key)) {
                        values.put(key, MathUtils.round(y1 + (i * factor), yMultiplier));
                    }
                } else {
                    double factor = ((y1 - y2) / ((x1 - x2) / xMultiplier));
                    double key = MathUtils.round(curX, xMultiplier);
                    if(!values.containsKey(key)) {
                        values.put(key, MathUtils.round(y1 - (i * factor), yMultiplier));
                    }
                }
            }
        }
        return values;
    }

    /**
     * Function to round and format the value to a given Multiplier
     * @see #roundValues(double, double, DecimalFormat)
     * @param value the value to round
     * @param multiplier the multiplier
     * @return the rounded value
     */
    public static double round(double value, double multiplier) {
        DecimalFormat dt = new DecimalFormat("0.00");
        String strValue = dt.format(value).replace(",", ".");

        if(strValue.endsWith("5")) {
            strValue = roundValues(value, multiplier, dt);

            String digitsToPoint = strValue.substring(0, strValue.length()-2);
            String digitsFive = strValue.substring(strValue.length()-2, strValue.length()-1);

            value = Double.parseDouble(digitsToPoint + String.valueOf(Integer.parseInt(digitsFive) + 0));

        } else {
            value = Double.parseDouble(strValue.substring(0, strValue.length()));
        }

        return Double.parseDouble(roundValues(value, multiplier, dt));
    }

    /**
     * Function to round an value and
     * format it to the decimal-format
     * @param value the value
     * @param multiplier the multiplier
     * @param decimalFormat the decimal-format
     * @return the rounded value
     */
    private static String roundValues(double value, double multiplier, DecimalFormat decimalFormat) {
        double multi = 1.0 / multiplier;
        double rounded = Math.round(value * multi);
        double mini = rounded / multi;
        return decimalFormat.format(mini).replace(",", ".");
    }
}
