/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.utils.fileUtils;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class to create an XML-File or
 * get data from it
 * @see de.domjos.schooltools.core.utils.fileUtils.XMLElement
 * @author Dominic Joas
 * @version 1.0
 */
public class XMLBuilder {
    private Document document;
    private Element root;
    private File xmlFile;

    public XMLBuilder(File file) throws Exception {
        // init document-builder-factory
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // create document and root-element
        this.document = dBuilder.parse(file);
        this.root = this.document.getDocumentElement();
        this.xmlFile = file;
    }

    public XMLBuilder(InputStream is) throws Exception {
        // init document-builder-factory
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // create document and root-element
        this.document = dBuilder.parse(is);
        this.root = this.document.getDocumentElement();
    }

    public XMLBuilder(String root, File file) throws ParserConfigurationException {
        // init document-builder-factory
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // create document and root-element
        this.document = docBuilder.newDocument();
        this.root = this.document.createElement(root);
        this.xmlFile = file;
    }

    public void addElement(XMLElement element) {
        Element newElement = this.document.createElement(element.getElement());
        for(Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
            newElement.setAttribute(entry.getKey(), entry.getValue());
        }
        newElement.setTextContent(element.getContent());

        if(!element.getSubElements().isEmpty()) {
            for(XMLElement subElement : element.getSubElements()) {
                this.addElementToParent(newElement, subElement);
            }
        }

        if(element.getParentElement().equals("")) {
            this.root.appendChild(newElement);
        } else {
            NodeList nodeList = this.root.getElementsByTagName(element.getParentElement());
            if(nodeList.getLength()!=0) {
                nodeList.item(0).appendChild(newElement);
            }
        }
    }

    private void addElementToParent(Element parent, XMLElement element) {
        Element newElement = this.document.createElement(element.getElement());
        for(Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
            newElement.setAttribute(entry.getKey(), entry.getValue());
        }
        newElement.setTextContent(element.getContent());
        if(!element.getSubElements().isEmpty()) {
            for(XMLElement subElement : element.getSubElements()) {
                this.addElementToParent(newElement, subElement);
            }
        }
        parent.appendChild(newElement);
    }

    public void addElement(XMLElement element, int index) {
        Element newElement = this.document.createElement(element.getElement());
        for(Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
            newElement.setAttribute(entry.getKey(), entry.getValue());
        }
        newElement.setTextContent(element.getContent());

        if(element.getParentElement().equals("")) {
            this.root.appendChild(newElement);
        } else {
            NodeList nodeList = this.root.getElementsByTagName(element.getParentElement());
            if(nodeList.getLength()!=0) {
                nodeList.item(index).appendChild(newElement);
            }
        }
    }

    public List<String> getChildElements(String root) {
        List<String> elements = new LinkedList<>();
        NodeList nl = this.document.getElementsByTagName(root);
        if(nl.getLength()>=1) {
            Node node = nl.item(0);
            if(node.hasChildNodes()) {
                NodeList nodeList = node.getChildNodes();
                for(int i = 0; i<=nodeList.getLength()-1; i++) {
                    elements.add(nodeList.item(i).getNodeName());
                }
            }
        }
        return elements;
    }

    public void updateElement(XMLElement element, String parentElement) {
        Node parentNode = this.document.getElementsByTagName(parentElement).item(0);
        Node node = null;
        if(parentNode.hasChildNodes()) {
            for(int i = 0; i<=parentNode.getChildNodes().getLength()-1; i++) {
                Node tmp = parentNode.getChildNodes().item(i);
                if(tmp.getNodeName().equals(element.getElement())) {
                    node = tmp;
                    break;
                }
            }
        }

        if(node!=null) {
            for (Map.Entry<String, String> attr : element.getAttributes().entrySet()) {
                for (int i = 0; i <= node.getAttributes().getLength() - 1; i++) {
                    if (node.getAttributes().item(i).getNodeName().equals(attr.getKey())) {
                        node.getAttributes().item(i).setNodeValue(attr.getValue());
                    }
                }
            }
            if(element.getContent()!=null) {
                if(!element.getContent().equals("")) {
                    node.setTextContent(element.getContent());
                }
            }
        }
    }

    public void updateText(String text, String element) {
        Node parentNode = this.document.getElementsByTagName(element).item(0);
        parentNode.setTextContent(text);
    }

    public XMLElement getElement(String name,boolean hasText) {
        XMLElement element = null;
        NodeList nodeList = this.document.getElementsByTagName(name);
        if(nodeList.getLength()!=0) {
            Node node = nodeList.item(0);
            element = new XMLElement(node.getNodeName());
            element.setParentElement(name);
            for(int i = 0; i<=node.getAttributes().getLength()-1; i++) {
                element.addAttribute(node.getAttributes().item(i).getNodeName(), node.getAttributes().item(i).getNodeValue());
            }
            if(hasText) {
                element.setContent(node.getTextContent());
            }
        }
        return element;
    }

    public List<XMLElement> getElements(String name) {
        List<XMLElement> xmlElements = new LinkedList<>();
        NodeList nodeList = this.document.getElementsByTagName(name);
        if(nodeList.getLength()!=0) {
            for(int i = 0; i<=nodeList.getLength()-1; i++) {
                xmlElements.add(this.getSubNode(nodeList.item(i)));
            }
        }
        return xmlElements;
    }

    private XMLElement getSubNode(Node node) {
        XMLElement xmlElement = new XMLElement(node.getNodeName());
        if(node.hasAttributes()) {
            for(int i = 0; i<=node.getAttributes().getLength()-1; i++) {
                xmlElement.addAttribute(node.getAttributes().item(i).getNodeName(), node.getAttributes().item(i).getNodeValue());
            }
        }
        if(node.hasChildNodes()) {
            for(int i = 0; i<=node.getChildNodes().getLength()-1; i++) {
                Node subNode = node.getChildNodes().item(i);
                try {
                    xmlElement.setContent(((Text)subNode).getWholeText());
                } catch (Exception ex) {
                    xmlElement.addSubElement(this.getSubNode(subNode));
                }
            }
        }
        return xmlElement;
    }

    public void save() throws Exception {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        boolean state = true;
        for(int i = 0; i<=this.document.getChildNodes().getLength()-1; i++) {
            if(this.document.getChildNodes().item(i).getNodeName().equals(this.root.getNodeName())) {
                state = false;
                break;
            }
        }
        if(state) {
            this.document.appendChild(this.root);
        }
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(this.xmlFile);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
    }
}

