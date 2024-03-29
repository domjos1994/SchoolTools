/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.domjos.schooltoolslib.utils.fileUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dominic Joas
 */
public class CSVBridge {
    private String header;
    private final String SEPARATOR;
    private final Map<Integer, CSVObject> lineMap;
    private int currentLine;
    private String format;
    
    public CSVBridge(String separator, String header, String format) {
        this(separator, header, "", false, format);
    }
    
    public CSVBridge(String separator, String header, String content, boolean contentWithHeader, String format) {
        this.SEPARATOR = separator;
        this.format = format;
        this.lineMap = new LinkedHashMap<>();
        if(contentWithHeader) {
            if (!content.isEmpty()) {
                String[] lines = CSVObject.splitWithEscape(content, "\n");
                this.header = lines[0];
                for(int i = 1; i<=lines.length-1; i++) {
                    this.lineMap.put(i, new CSVObject(this.SEPARATOR, this.header, lines[i]));
                }
            }
        } else {
            if(!header.isEmpty()) {
                this.header = header;
                this.lineMap.put(1, new CSVObject(this.SEPARATOR, this.header));
            }
        }
        this.currentLine = 1;
    }

    public int size() {
        return this.lineMap.size();
    }

    public void writeValue(int line, String key, String value) {
        CSVObject obj = this.lineMap.get(line);
        if(obj==null) {
            return;
        }
        obj.writeValue(key, value);
        this.lineMap.put(line, obj);
    }
    
    public void writeValue(int line, String key, Integer value) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, value);
        this.lineMap.put(line, obj);
    }

    public void writeValue(int line, String key, Long value) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, value);
        this.lineMap.put(line, obj);
    }
    
    public void writeValue(int line, String key, Double value) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, value);
        this.lineMap.put(line, obj);
    }
    
    public void writeValue(int line, String key, Boolean value) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, value);
        this.lineMap.put(line, obj);
    }

    public void writeValue(int line, String key, Date value) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, value, this.format);
        this.lineMap.put(line, obj);
    }

    public void writeValue(int line, String key, CSVObject object, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, object, startTag, endTag);
        this.lineMap.put(line, obj);
    }

    public void writeValue(int line, String key, List<CSVObject> objects, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return;
        }
        obj.writeValue(key, objects, startTag, endTag);
        this.lineMap.put(line, obj);
    }
    
    public String readStringValue(int line, String key) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readStringValue(key);
    }

    public Integer readIntegerValue(int line, String key) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readIntegerValue(key);
    }

    public Double readDoubleValue(int line, String key) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readDoubleValue(key);
    }

    public Date readDateValue(int line, String key) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readDateValue(key, this.format);
    }

    public CSVObject readObjectValue(int line, String key, String separator, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readObjectValue(key, separator, startTag, endTag);
    }

    public CSVObject readObjectValue(int line, String key, String header, String separator, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readObjectValue(key, header, separator, startTag, endTag);
    }

    public List<CSVObject> readObjectsValue(int line, String key, String separator, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readObjectsValue(key, separator, startTag, endTag);
    }

    public List<CSVObject> readObjectsValue(int line, String key, String header, String separator, String startTag, String endTag) {
        CSVObject obj = this.lineMap.get(line);
        if (obj == null) {
            return null;
        }
        return obj.readObjectsValue(key, header, separator, startTag, endTag);
    }
    
    public void addNewLine() {
        currentLine++;
        this.lineMap.put(currentLine, new CSVObject(this.SEPARATOR, this.header));
    }

    public void replaceWithNewLine() {
        this.lineMap.put(currentLine, new CSVObject(this.SEPARATOR, this.header));
    }

    @Override
    public String toString() {
        String content;
        if(this.header.endsWith(this.SEPARATOR)) {
            content = this.header.substring(0, this.header.length() - this.SEPARATOR.length()) + "\n";
        } else {
            content = this.header + "\n";
        }
        StringBuilder builder = new StringBuilder(content);
        for(CSVObject obj : this.lineMap.values()) {
            builder.append(obj.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
