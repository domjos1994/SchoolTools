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

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltoolslib.helper.Helper;

/**
 *
 * @author Dominic Joas
 */
public class CSVObject {
    private final String SEPARATOR;
    private final static String EMPTY = "__empty__";
    private final Map<Integer, String> columnOrder;
    private final Map<String, String> columns;
    private String line;
    
    CSVObject(String separator, String header) {
        this(separator, header, "");
    }
    
    CSVObject(String separator, String header, String line) {
        this.SEPARATOR = separator;
        this.columns = new LinkedHashMap<>();
        this.columnOrder = new LinkedHashMap<>();

        String[] columnArray;
        if(!line.isEmpty() && header.isEmpty()) {
            columnArray = CSVObject.splitWithEscape(line, this.SEPARATOR);
            for(int i = 1; i<=columnArray.length; i++) {
                this.columnOrder.put(i, String.valueOf(i));
            }
        } else if(!header.isEmpty()) {
            columnArray = CSVObject.splitWithEscape(header, this.SEPARATOR);
            for(int i = 0; i<=columnArray.length-1; i++) {
                this.columnOrder.put(i, columnArray[i]);
            }
        } else {
            columnArray = null;
        }
        
        if(columnArray!=null) {
            if(line.isEmpty()) {
                for (String column : columnArray) {
                    this.columns.put(column, "");
                }
            } else {
                String[] columnValues = CSVObject.splitWithEscape(line, this.SEPARATOR);
                for (int i = 0; i<=columnArray.length-1; i++) {
                    if(columnValues.length-1>=i) {
                        this.columns.put(String.valueOf(columnOrder.values().toArray()[i]), columnValues[i]);
                    } else {
                        this.columns.put(String.valueOf(columnOrder.values().toArray()[i]), "");
                    }
                }
            }
            this.line = line;
        }
    }

    public CSVObject(String separator, int numberOfColumns) {
        this.SEPARATOR = separator;
        this.columns = new LinkedHashMap<>();
        this.columnOrder = new LinkedHashMap<>();
        this.line = "";

        for(int i = 1; i<=numberOfColumns; i++) {
            this.columnOrder.put(i, String.valueOf(i));
        }

        for (String column : this.columnOrder.values()) {
            this.columns.put(column, "");
        }
    }


    public void writeValue(String key, String value) {
        if(value.isEmpty()) {
            this.columns.put(key, CSVObject.EMPTY);
        } else {
            this.columns.put(key, this.escapeText(value));
        }
    }
    
    public void writeValue(String key, Integer value) {
        if (value != null) {
            this.columns.put(key, String.valueOf(value));
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }

    public void writeValue(String key, Long value) {
        if (value != null) {
            this.columns.put(key, String.valueOf(value));
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }
    
    public void writeValue(String key, Double value) {
        if(value!=null) {
            this.columns.put(key, String.valueOf(value));
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }
    
    public void writeValue(String key, Boolean value) {
        if (value != null) {
            this.columns.put(key, String.valueOf(value));
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }

    public void writeValue(String key, Date value, String format) {
        if (value != null) {
            String date = ConvertHelper.convertDateToString(value, format);
            if(date.isEmpty()) {
                this.columns.put(key, CSVObject.EMPTY);
            } else {
                this.columns.put(key, date);
            }
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }

    public void writeValue(String key, CSVObject csvObject, String startTag, String endTag) {
        this.writeValue(key, Collections.singletonList(csvObject), startTag, endTag);
    }

    public void writeValue(String key, List<CSVObject> csvObjects, String startTag, String endTag) {
        StringBuilder content = new StringBuilder("");
        if(csvObjects!=null) {
            if(!csvObjects.isEmpty()) {
                for(CSVObject csvObject : csvObjects) {
                    if(csvObject!=null) {
                        content.append(startTag);
                        content.append(csvObject.toString());
                        content.append(endTag);
                    } else {
                        content.append(startTag);
                        content.append(CSVObject.EMPTY);
                        content.append(endTag);
                    }
                }
                this.columns.put(key, content.toString());
            } else {
                this.columns.put(key, CSVObject.EMPTY);
            }
        } else {
            this.columns.put(key, CSVObject.EMPTY);
        }
    }
    
    public String readStringValue(String key) {
        if (this.columns.containsKey(key)) {
            if (this.columns.get(key).equals(CSVObject.EMPTY)) {
                return "";
            } else {
                return this.unescapeText(this.columns.get(key));
            }
        } else {
            return "";
        }
    }

    public Integer readIntegerValue(String key) {
        String content = this.readStringValue(key);
        if(Helper.isInteger(content.trim())) {
            return Integer.parseInt(content.trim());
        } else {
            return null;
        }
    }

    public Double readDoubleValue(String key) {
        String content = this.readStringValue(key);
        if(Helper.isDouble(content.trim())) {
            return Double.parseDouble(content.trim());
        } else {
            return null;
        }
    }

    public Boolean readBooleanValue(String key) {
        if(this.columns.get(key).toLowerCase().equals("true")) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Date readDateValue(String key, String format) {
        try {
            String content = this.readStringValue(key);
            if(content!=null) {
                return ConvertHelper.convertStringToDate(content.trim(), format);
            }
        } catch (ParseException ex) {
            return null;
        }
        return null;
    }

    public CSVObject readObjectValue(String key, String separator, String startTag, String endTag) {
        List<CSVObject> csvObjects = this.readObjectsValue(key, separator, startTag, endTag);
        if(csvObjects!=null) {
            if(!csvObjects.isEmpty()) {
                return csvObjects.get(0);
            }
        }
        return null;
    }

    public CSVObject readObjectValue(String key, String header, String separator, String startTag, String endTag) {
        List<CSVObject> csvObjects = this.readObjectsValue(key, header, separator, startTag, endTag);
        if(csvObjects!=null) {
            if(!csvObjects.isEmpty()) {
                return csvObjects.get(0);
            }
        }
        return null;
    }

    public List<CSVObject> readObjectsValue(String key, String separator, String startTag, String endTag) {
        return this.readObjectsValue(key, "", separator, startTag, endTag);
    }

    public List<CSVObject> readObjectsValue(String key, String header, String separator, String startTag, String endTag) {
        List<CSVObject> objects = new LinkedList<>();
        String content = this.readStringValue(key);
        if(content!=null) {
            if(!content.isEmpty()) {
                String[] splContent = CSVObject.splitWithEscape(content, endTag + startTag);
                if(splContent!=null) {
                    for(String strObj : splContent) {
                        if(strObj.trim().startsWith(startTag)) {
                            strObj = strObj.substring(startTag.length());
                        }
                        if(strObj.trim().endsWith(endTag)) {
                            strObj = strObj.substring(0, strObj.length()-endTag.length());
                        }
                        if(strObj.trim().equals(CSVObject.EMPTY)) {
                            objects.add(null);
                        } else {
                            objects.add(new CSVObject(separator, header, strObj));
                        }
                    }
                }
            }
        }
        return objects;
    }

    @Override
    public String toString() {
        this.line = "";
        StringBuilder builder = new StringBuilder(this.line);
        for(String value : this.columnOrder.values()) {
            builder.append(this.columns.get(value));
            builder.append(this.SEPARATOR);
        }
        return builder.toString().substring(0, builder.toString().length() - this.SEPARATOR.length());
    }
    
    public static String[] splitWithEscape(String content, String separator) {
        Pattern p = Pattern.compile(separator, Pattern.LITERAL);
        return p.split(content);
    }

    private String escapeText(String text) {
        return text.replace("\n", "[n]").replace(";", "[,]");
    }

    private String unescapeText(String text) {
        return text.replace("[n]", "\n").replace("[,]", ";");
    }
}
