/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.utils.xFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import de.domjos.customwidgets.utils.ConvertHelper;

public class ObjectXML {

    public static void saveObjectListToXML(String root, List<?> lst, String path, String format) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.newDocument();
        Element element = document.createElement(root);
        for(Object object : lst) {
            element.appendChild(ObjectXML.convertObjectToXMLElement(object, document, format));
        }
        document.appendChild(element);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(path);
        transformer.transform(source, result);
    }

    static List<Object> saveXMLToObjectList(String path, String format) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document document = dBuilder.parse(path);
        Element element = (Element) document.getFirstChild();
        List<Object> objectList = new LinkedList<>();
        for(int i = 0; i<=element.getChildNodes().getLength()-1; i++) {
            Node node = element.getChildNodes().item(i);
            objectList.add(ObjectXML.convertXMLElementToObject(node, format));
        }
        return objectList;
    }

    private static Object convertXMLElementToObject(Node node, String format) throws Exception {
        Class<?> cls = ObjectXML.findClassByName(node.getNodeName());
        if(cls==null) {
            throw new Exception();
        }
        Object object = cls.newInstance();
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        fieldMap = getAllFields(object.getClass(), fieldMap);
        if(node.hasAttributes()) {
            for(int i = 0; i<=node.getAttributes().getLength()-1; i++) {
                Node attribute = node.getAttributes().item(i);
                Field field = fieldMap.get(attribute.getNodeName());
                if(field!=null) {
                    if(field.getType() == int.class || field.getType() == Integer.class) {
                        field.setInt(object, Integer.parseInt(attribute.getNodeValue()));
                    }
                    if(field.getType() == long.class || field.getType() == Long.class) {
                        field.setLong(object, Long.parseLong(attribute.getNodeValue()));
                    }
                    if(field.getType() == double.class || field.getType() == Double.class) {
                        field.setDouble(object, Double.parseDouble(attribute.getNodeValue()));
                    }
                    if(field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.setBoolean(object, Boolean.parseBoolean(attribute.getNodeValue()));
                    }
                    if(field.getType() == float.class || field.getType() == Float.class) {
                        field.setFloat(object, Float.parseFloat(attribute.getNodeValue()));
                    }
                    if(field.getType() == byte[].class || field.getType() == Byte[].class) {
                        field.set(object, attribute.getNodeValue().getBytes());
                    }
                    if(field.getType() == String.class) {
                        field.set(object, attribute.getNodeValue());
                    }
                    if(field.getType() == Date.class) {
                        field.set(object, ConvertHelper.convertStringToDate(attribute.getNodeValue(), format));
                    }
                }
            }
        }
        if(node.hasChildNodes()) {
            for(int i = 0; i<=node.getChildNodes().getLength()-1; i++) {
                Node subNode = node.getChildNodes().item(i);
                if(subNode.hasAttributes()) {
                    Field field = fieldMap.get(subNode.getNodeName());
                    if(field!=null) {
                        field.set(object, convertXMLElementToObject(subNode, format));
                    }
                } else {
                    if(subNode.hasChildNodes()) {
                        List<Object> lst = new LinkedList<>();
                        for(int j = 0; j<=subNode.getChildNodes().getLength()-1; j++) {
                            lst.add(convertXMLElementToObject(subNode.getChildNodes().item(j), format));
                        }
                        Field field = fieldMap.get(subNode.getNodeName());
                        if(field!=null) {
                            field.set(object, lst);
                        }
                    }
                }
            }
        }

        return object;
    }

    private static Map<String, Field> getAllFields(Class<?> cls, Map<String, Field> map) {
        if(cls.getSuperclass()!=null) {
            map = ObjectXML.getAllFields(cls.getSuperclass(), map);
        }

        for(Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            map.put(field.getName(), field);
        }
        return map;
    }

    private static Element convertObjectToXMLElement(Object object, Document document, String format) throws Exception {
        Element element = document.createElement(object.getClass().getSimpleName());

        ObjectXML.convertObjectToXMLElementSubClass(object.getClass(), object, element, format);

        return element;
    }

    private static void convertObjectToXMLElementSubClass(Class<?> cls, Object object, Element element, String format) throws Exception {
        if(cls.getSuperclass()!=null) {
            convertObjectToXMLElementSubClass(cls.getSuperclass(), object, element, format);
        }

        for(Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.get(object)!=null) {
                if (field.getType().isPrimitive() || field.getType() == String.class) {
                    element.setAttribute(field.getName(), Objects.requireNonNull(field.get(object)).toString());
                } else if (field.getType() == Date.class) {
                    if (field.get(object) != null) {
                        element.setAttribute(field.getName(), ConvertHelper.convertDateToString((Date) field.get(object), format));
                    } else {
                        element.setAttribute(field.getName(), null);
                    }
                } else if (field.getType() == List.class) {
                    Element sub = element.getOwnerDocument().createElement(field.getName());

                    for (Object subObject : (List<?>) Objects.requireNonNull(field.get(object))) {
                        Element subSub = element.getOwnerDocument().createElement(subObject.getClass().getSimpleName());
                        ObjectXML.convertObjectToXMLElementSubClass(subObject.getClass(), subObject, subSub, format);
                        sub.appendChild(subSub);
                    }
                    element.appendChild(sub);
                } else if(field.getType() == byte[].class || field.getType() == Byte[].class) {
                    byte[] bytes = (byte[]) field.get(object);
                    element.setAttribute(field.getName(), Arrays.toString(bytes));
                } else {
                    Element sub = element.getOwnerDocument().createElement(field.getName());
                    ObjectXML.convertObjectToXMLElementSubClass(field.getType(), field.get(object), sub, format);
                    element.appendChild(sub);
                }
            }
        }
    }

    private static Class<?> findClassByName(String name) {
        List<String> packages = Arrays.asList("model", "model.learningCard", "model.mark", "model.marklist", "model.objects", "model.timetable", "model.todo", "marklist.de");

        for(String pkg : packages) {
            Class<?> cls;
            try {
                name = name.substring(0,1).toUpperCase() + name.substring(1);
                cls = Class.forName("de.domjos.schooltools.core." + pkg + "." + name);
            } catch (Exception|Error ignored) {
                continue;
            }
            return cls;
        }

        return null;
    }
}
