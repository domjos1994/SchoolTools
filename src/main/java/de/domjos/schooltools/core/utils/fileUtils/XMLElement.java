/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.utils.fileUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Model-Class for an XML-Element
 * @see de.domjos.schooltools.core.utils.fileUtils.XMLBuilder
 * @author Dominic Joas
 * @version 1.0
 */
public class XMLElement {
    private String element, parentElement, content;
    private Map<String, String> attributes;
    private List<XMLElement> subElements;

    public XMLElement(String name) {
        this.element = name;
        this.parentElement = "";
        this.content = "";
        this.attributes = new LinkedHashMap<>();
        this.subElements = new LinkedList<>();
    }

    public String getElement() {
        return this.element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getParentElement() {
        return this.parentElement;
    }

    public void setParentElement(String parentElement) {
        this.parentElement = parentElement;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(String key, String content) {
        this.attributes.put(key, content);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addSubElement(XMLElement xmlElement) {
        this.subElements.add(xmlElement);
    }

    public List<XMLElement> getSubElements() {
        return this.subElements;
    }
}

