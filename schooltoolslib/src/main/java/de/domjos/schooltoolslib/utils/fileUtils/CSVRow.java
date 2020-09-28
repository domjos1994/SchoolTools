/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.utils.fileUtils;

import java.util.Date;
import java.util.List;

import de.domjos.customwidgets.utils.ConvertHelper;

/**
 * @author Dominic Joas
 */
public class CSVRow {
    private final char separator;
    private final StringBuilder stringBuilder;
    private int numberOfColumns, currentColumn = 1;

    public CSVRow(char separator, int numberOfColumns) {
        this.separator = separator;
        this.stringBuilder = new StringBuilder();
        this.numberOfColumns = numberOfColumns;
    }

    public void addValue(int value) throws Exception {
        if(this.numberOfColumns>=this.currentColumn) {
            this.stringBuilder.append(value);
            this.stringBuilder.append(this.separator);
            this.currentColumn++;
        } else {
            throw new Exception("Number of Columns in Row higher than header!");
        }
    }

    public void addValue(double value) throws Exception {
        if(this.numberOfColumns>=this.currentColumn) {
            this.stringBuilder.append(value);
            this.stringBuilder.append(this.separator);
            this.currentColumn++;
        } else {
            throw new Exception("Number of Columns in Row higher than header!");
        }
    }

    public void addValue(String value) throws Exception {
        if(this.numberOfColumns>=this.currentColumn) {
            if(value!=null) {
                if(!value.isEmpty()) {
                    this.stringBuilder.append(this.escapeText(value));
                    this.stringBuilder.append(this.separator);
                    this.currentColumn++;
                } else {
                    this.writeEmptyColumn();
                }
            } else {
                this.writeEmptyColumn();
            }
        } else {
            throw new Exception("Number of Columns in Row higher than header!");
        }
    }

    public void addValue(boolean value) throws Exception {
        if(this.numberOfColumns>=this.currentColumn) {
            this.stringBuilder.append(value);
            this.stringBuilder.append(this.separator);
            this.currentColumn++;
        } else {
            throw new Exception("Number of Columns in Row higher than header!");
        }
    }

    public void addValue(Date value, String format) throws Exception {
        if(this.numberOfColumns>=this.currentColumn) {
            if(value!=null) {
                this.stringBuilder.append(ConvertHelper.convertDateToString(value, format));
                this.stringBuilder.append(this.separator);
                this.currentColumn++;
            } else {
                this.writeEmptyColumn();
            }
        } else {
            throw new Exception("Number of Columns in Row higher than header!");
        }
    }

    public void addObjects(List<CSVRow> objects, char startSign, char endSign) {
        if(objects!=null) {
            if(!objects.isEmpty()) {
                for(CSVRow row : objects) {
                    this.stringBuilder.append(startSign);
                    this.stringBuilder.append(row.getContent());
                    this.stringBuilder.append(endSign);
                }
                this.stringBuilder.append(this.separator);
                this.currentColumn++;
            } else {
                this.writeEmptyColumn();
            }
        } else {
            this.writeEmptyColumn();
        }
    }

    public String getContent() {
        return this.stringBuilder.toString();
    }

    private String escapeText(String text) {
        return text.replace("\n", "[n]").replace(";", "[,]");
    }

    private void writeEmptyColumn() {
        this.stringBuilder.append("__empty__");
        this.stringBuilder.append(this.separator);
        this.currentColumn++;
    }
}
