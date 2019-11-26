/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.utils.fileUtils;

import android.content.Context;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import de.domjos.schooltoolslib.R;
import de.domjos.schooltoolslib.helper.Converter;

public class FileBridge {
    private final Context context;
    private final Object object;
    private final String file;
    private Map<String, Method> methods;

    public FileBridge(Object object, String file, Context context) {
        this.context = context;
        this.object = object;
        this.file = file;
        this.methods = new LinkedHashMap<>();
    }

    public void writeObjectToFile() throws Exception {
        if(this.file!=null && this.object!=null) {
            if(!this.file.isEmpty()) {
                this.knownExtension(false);
            } else {
                throw new Exception(this.context.getString(R.string.message_wrong_method));
            }
        } else {
            throw new Exception(this.context.getString(R.string.message_wrong_method));
        }

        if(this.file.endsWith(".txt") || this.file.endsWith(".csv")) {
            this.writeObjectToTXTOrCSV();
        } else if(this.file.endsWith(".xml")) {
            this.writeObjectToXML();
        } else {
            this.writeObjectToPDF();
        }
    }

    public void readObjectFromFile() throws Exception {
        if(this.file!=null && this.object==null) {
            if(!this.file.isEmpty()) {
                if(new File(this.file).exists()) {
                    this.knownExtension(true);
                } else {
                    throw new Exception(this.context.getString(R.string.message_file_not_exists));
                }
            } else {
                throw new Exception(this.context.getString(R.string.message_wrong_method));
            }
        } else {
            throw new Exception(this.context.getString(R.string.message_wrong_method));
        }

        if(this.file.endsWith(".txt") || this.file.endsWith(".csv")) {
            this.readObjectFromTXTOrCsv();
        } else {
            this.readObjectFromXML();
        }
    }

    private void writeObjectToTXTOrCSV() throws Exception {
        List<Object> ls = new LinkedList<>();
        if(this.object.getClass()== List.class) {
            if(!((List)this.object).isEmpty()) {
                boolean init = false;
                for(Object tmp : (List)this.object) {
                    if(!init) {
                        this.methods = this.getMethods(tmp, "get");
                        init = true;
                    }
                    ls.add(tmp);
                }
            }
        } else {
            ls.add(this.object);
            this.methods = this.getMethods(this.object, "get");
        }

        StringBuilder header = new StringBuilder();
        for(String key : this.methods.keySet()) {
            header.append(key);
            header.append(";");
        }
        header.delete(header.length()-1, header.length()-1);

        StringBuilder content = new StringBuilder();
        for(Object tmp : ls) {
            for(Method method : this.methods.values()) {
                if(this.isReturnTypePrimitive(method)) {
                    content.append(method.invoke(tmp));
                    content.append(";");
                } else if(this.isReturnTypeDate(method)) {
                    content.append(Converter.convertDateToString((Date) method.invoke(tmp)));
                    content.append(";");
                } else {
                    content.append(";");
                }
            }
            content.delete(content.length()-1, content.length()-1);
            content.append("\n");
        }

        this.writeStringToFile(header.toString() + "\n" + content.toString());
    }

    private void writeObjectToXML() {

    }

    private void writeObjectToPDF() {

    }

    private void readObjectFromTXTOrCsv() {

    }

    private void readObjectFromXML() {

    }

    private boolean isReturnTypePrimitive(Method method) {
        Class<?> cls = method.getReturnType();
        return cls==String.class || cls==Integer.class || cls==Boolean.class || cls==Long.class || cls==Double.class || cls==int.class || cls==boolean.class || cls==long.class || cls==double.class;
    }

    private boolean isReturnTypeDate(Method method) {
        Class<?> cls = method.getReturnType();
        return cls==Date.class;
    }

    private Map<String, Method> getMethods(Object object, String pre) {
        Map<String, Method> methodMap = new LinkedHashMap<>();
        for(Method method : object.getClass().getMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                if (method.getName().startsWith(pre)) {
                    if (method.getDeclaringClass().getName().startsWith("de.domjos.schooltools")) {
                        methodMap.put(method.getName().replace(pre, ""), method);
                    }
                }
            }
        }
        return methodMap;
    }

    private void knownExtension(boolean read) throws Exception {
        if (!this.file.endsWith(".txt") && !this.file.endsWith(".csv") && !this.file.endsWith(".xml")) {
            if (read) {
                throw new Exception(this.context.getString(R.string.message_file_format_unknown));
            } else {
                if (!this.file.endsWith(".pdf")) {
                    throw new Exception(this.context.getString(R.string.message_file_format_unknown));
                }
            }
        }
    }

    private void writeStringToFile(String content) throws Exception {
        PrintWriter out = new PrintWriter(this.file);
        out.write(content);
        out.flush();
        out.close();
    }
}
