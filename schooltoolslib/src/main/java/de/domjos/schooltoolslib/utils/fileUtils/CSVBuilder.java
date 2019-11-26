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

/**
 * @author Dominic Joas
 */
public class CSVBuilder {
    private final char separator;
    private final StringBuilder stringBuilder;
    private int numberOfColumns;
    private CSVRow currentRow;

    public CSVBuilder(char separator, int numberOfColumns) {
        this(separator, null, numberOfColumns);
    }

    public CSVBuilder(char separator, List<String> headers) {
        this(separator, headers, headers.size());
    }

    private CSVBuilder(char separator, List<String> headers, int numberOfColumns) {
        this.separator = separator;
        this.stringBuilder = new StringBuilder();
        this.numberOfColumns = numberOfColumns;
        if(headers!=null) {
            for(String column : headers) {
                this.stringBuilder.append(this.escapeText(column));
                this.stringBuilder.append(this.separator);
            }
        }
        this.newLine();
    }

    public void addValue(int value) throws Exception {
        this.currentRow.addValue(value);
    }

    public void addValue(double value) throws Exception {
        this.currentRow.addValue(value);
    }

    public void addValue(String value) throws Exception {
        this.currentRow.addValue(value);
    }

    public void addValue(boolean value) throws Exception {
        this.currentRow.addValue(value);
    }

    public void addValue(Date value) throws Exception {
        this.currentRow.addValue(value);
    }

    public void newLine() {
        if(this.currentRow!=null) {
            String content = this.currentRow.getContent();
            this.stringBuilder.append(content.substring(0, content.length()-1));
            this.stringBuilder.append("\n");
        }
        this.currentRow = new CSVRow(this.separator, this.numberOfColumns);
    }

    public String getContent() {
        this.newLine();
        return this.stringBuilder.toString();
    }

    private String escapeText(String text) {
        return text.replace("\n", "[n]").replace(";", "[,]");
    }
}
