package de.domjos.schooltools.core.utils.fileUtils;

import de.domjos.schooltools.helper.Converter;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ObjectFileConnector {
    public static void ObjectToFile(Object object, File file, char separator) throws Exception {
        ObjectToFile(Collections.singletonList(object), file, separator);
    }

    public static void ObjectToFile(List<Object> objects, File file, char separator) throws Exception {
        if(objects!=null) {
            if(!objects.isEmpty()) {
                String extension = file.getAbsoluteFile().getName().substring(file.getAbsoluteFile().getName().lastIndexOf("."));
                switch (extension) {
                    case ".xml":
                        XMLBuilder xmlBuilder = new XMLBuilder(objects.get(0).getClass().getSimpleName() + "s", file);
                        for(Object object : objects) {
                            xmlBuilder.addElement(ObjectFileConnector.getElementFromObject(object, object.getClass().getSimpleName()));
                        }
                        xmlBuilder.save();
                        break;
                    case ".txt":
                    case ".csv":
                        CSVBridge csvBridge = ObjectFileConnector.getCSVBuilderFromObject(objects, separator);
                        if(csvBridge!=null) {
                            PrintWriter out = new PrintWriter(file);
                            out.write(csvBridge.toString());
                            out.flush();
                            out.close();
                        }
                        break;
                    case ".pdf":

                        break;
                    default:
                        throw new Exception("Format unknown!");
                }
            }
        }
    }

    public static Object FileToObject(File file, Class<?> cls, String separator) throws Exception {
        Object object = null;
        if(file!=null) {
            if(file.exists()) {
                String extension = file.getAbsoluteFile().getName().substring(file.getAbsoluteFile().getName().lastIndexOf("."));
                switch (extension) {
                    case ".xml":
                        object = getObjectFromXML(file, cls);
                        break;
                    case ".txt":
                    case ".csv":
                        object = getObjectFromCSV(file, cls, separator);
                        break;
                    case ".pdf":

                        break;
                    default:
                        throw new Exception("Format unknown!");
                }
            }
        }
        return object;
    }

    private static XMLElement getElementFromObject(Object object, String name) {
        try {
            XMLElement xmlElement = new XMLElement(name);
            Map<String, String> mp = new LinkedHashMap<>();
            for(Method method : object.getClass().getMethods()) {
                if(Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                    if(method.getName().startsWith("get")) {
                        if (method.getDeclaringClass().getName().startsWith("de.domjos.schooltools")) {
                            Class<?> cls = method.getReturnType();
                            if (cls == Integer.class || cls == String.class || cls == Double.class || cls == Date.class || cls == Long.class || cls == Boolean.class
                                    || cls == int.class || cls == long.class || cls == boolean.class || cls == double.class) {

                                mp.put(method.getName().replace("get", ""), String.valueOf(method.invoke(object)));
                            } else if (cls == List.class) {
                                List ls = (List) method.invoke(object);
                                if (ls != null) {
                                    XMLElement sub = new XMLElement(method.getName().replace("get", ""));
                                    for (Object obj : ls) {
                                        if (obj != null) {
                                            sub.addSubElement(ObjectFileConnector.getElementFromObject(obj, method.getName().replace("get", "").substring(0, method.getName().replace("get", "").length()-1)));
                                        }
                                    }
                                    xmlElement.addSubElement(sub);
                                }
                            } else {
                                Object obj = method.invoke(object);
                                if (obj != null) {
                                    xmlElement.addSubElement(ObjectFileConnector.getElementFromObject(method.invoke(object), method.getName().replace("get", "")));
                                }
                            }
                        }
                    }
                }
            }
            xmlElement.setAttributes(mp);
            return xmlElement;
        } catch(Exception ex) {
            return null;
        }
    }

    private static Object getObjectFromXML(File file, Class<?> cls) throws Exception {
        Object object = null;
        XMLBuilder builder = new XMLBuilder(file);
        List<XMLElement> childElements = builder.getElements(cls.getSimpleName());
        if(childElements!=null) {
            if(!childElements.isEmpty()) {
                if(childElements.size()==1) {
                    object = ObjectFileConnector.setClassToObject(childElements.get(0));
                }
            }
        }

        return object;
    }

    private static void setAttributeToMethod(Method method, Object object, String value) throws Exception {
        try {
            Class<?> type = method.getParameterTypes()[0];
            if(type==Date.class) {
                method.invoke(object, Converter.convertStringToDate(value));
            }
            if(type==Integer.class || type==int.class) {
                method.invoke(object, Integer.parseInt(value));
            }
            if(type==Double.class || type==double.class) {
                method.invoke(object, Double.parseDouble(value));
            }
            if(type==Long.class || type==long.class) {
                method.invoke(object, Long.parseLong(value));
            }
            if(type==String.class) {
                method.invoke(object, value);
            }
        } catch (Exception ex) {}
    }

    private static Object setClassToObject(XMLElement element) throws Exception {
        Class<?> cls = ObjectFileConnector.findClassBySimpleName(element.getElement());

        Object object = null;
        if(cls!=null) {
            object = cls.newInstance();
            Map<String, Method> methodMap = ObjectFileConnector.getMethods(object, "set");
            for(Map.Entry<String, String> attribute : element.getAttributes().entrySet()) {
                Method method = methodMap.get(attribute.getKey());

                ObjectFileConnector.setAttributeToMethod(method, object, attribute.getValue());
            }

            for(XMLElement subElement : element.getSubElements()) {
                if(!subElement.getAttributes().isEmpty()) {
                    Method method = methodMap.get(subElement.getElement());
                    method.invoke(object, ObjectFileConnector.setClassToObject(subElement));
                }
            }
        }

        return object;
    }

    private static Class<?> findClassBySimpleName(String simpleName) {
         String[] searchPackages = {
                "de.domjos.schooltools.core.marklist.de",
                "de.domjos.schooltools.core.model.learningCard",
                "de.domjos.schooltools.core.model.mark",
                "de.domjos.schooltools.core.model.marklist",
                "de.domjos.schooltools.core.model.objects",
                "de.domjos.schooltools.core.model.timetable",
                "de.domjos.schooltools.core.model.todo",
                "de.domjos.schooltools.core.model"};

        for(String pkg : searchPackages){
            try {
                return Class.forName(pkg + "." + simpleName);
            } catch (ClassNotFoundException e) {
                //not in this package, try another
            }
        }
        return null;
    }

    private static CSVBridge getCSVBuilderFromObject(List<Object> objects, char separator) {
        try {
            if(objects!=null) {
                if(!objects.isEmpty()) {
                    Map<String, Method> methods = ObjectFileConnector.getMethods(objects.get(0), "get");
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String item : methods.keySet()) {
                        stringBuilder.append(item);
                        stringBuilder.append(separator);
                    }

                    CSVBridge csvBridge = new CSVBridge(String.valueOf(separator), stringBuilder.toString());
                    int i = 1;
                    for(Object object : objects) {
                        for(Method method : methods.values()) {
                            String name = method.getName().replace("get", "");
                            Class<?> cls = method.getReturnType();
                            if (cls == Integer.class || cls == String.class || cls == Double.class || cls == Date.class || cls == Long.class || cls == Boolean.class
                                    || cls == int.class || cls == long.class || cls == boolean.class || cls == double.class) {
                                if(cls == Integer.class || cls == int.class || cls == Long.class || cls == long.class) {
                                    csvBridge.writeValue(i, name, (int) method.invoke(object));
                                }
                                if(cls == Double.class || cls == double.class) {
                                    csvBridge.writeValue(i, name, (double) method.invoke(object));
                                }
                                if(cls == String.class) {
                                    csvBridge.writeValue(i, name, (String) method.invoke(object));
                                }
                                if(cls == Boolean.class || cls == boolean.class) {
                                    csvBridge.writeValue(i, name, (boolean) method.invoke(object));
                                }
                                if(cls == Date.class) {
                                    csvBridge.writeValue(i, name, (Date) method.invoke(object));
                                }
                            } else if (cls == List.class) {
                                List<CSVObject> objectList = new LinkedList<>();
                                for(Object obj : (List) method.invoke(object)) {
                                    objectList.add(ObjectFileConnector.getCSVObject(obj));
                                }
                                csvBridge.writeValue(i, name, objectList, "(", ")");
                            } else {
                                csvBridge.writeValue(i, name, ObjectFileConnector.getCSVObject(method.invoke(object)), "(", ")");
                            }
                        }
                        if(objects.size()!=i) {
                            csvBridge.addNewLine();
                        }
                        i++;
                    }
                    return csvBridge;
                }
            }

            return null;
        } catch(Exception ex) {
            return null;
        }
    }

    private static Object getObjectFromCSV(File file, Class<?> cls, String separator) throws Exception {
        Object object = null;

        String content = ObjectFileConnector.convertStreamToString(new FileInputStream(file));
        String header = ObjectFileConnector.getHeaderFromClass(cls, separator);
        Map<String, List<String>> items = ObjectFileConnector.getLinesByHeaderKeys(header, content, separator);
        Map<String, Method> methodMap = ObjectFileConnector.getMethods(cls.newInstance(), "set");
        object = cls.newInstance();
        for(Map.Entry<String, List<String>> item : items.entrySet()) {
            if(item.getValue().size()==1) {
                if(item.getValue().get(0).startsWith("(") && item.getValue().get(0).endsWith(")")) {
                    ObjectFileConnector.addObject(object, methodMap.get(item.getKey()), item.getKey(), item.getValue().get(0));
                } else {
                    ObjectFileConnector.setAttributeToMethod(methodMap.get(item.getKey()), object, item.getValue().get(0));
                }
            } else if(item.getValue().size()>1) {
                for(int i = 0; i<=item.getValue().size()-1; i++) {
                    if(((List)object).size()-1<i) {
                        ((List) object).add(cls.newInstance());
                    }
                    if(item.getValue().get(i).startsWith("(") && item.getValue().get(i).endsWith(")")) {
                        ObjectFileConnector.addObject(object, methodMap.get(item.getKey()), item.getKey(), item.getValue().get(i));
                    } else {
                        ObjectFileConnector.setAttributeToMethod(methodMap.get(item.getKey()), ((List) object).get(i), item.getValue().get(i));
                    }
                }
            }
        }
        return object;
    }

    private static void addObject(Object parent, Method setter, String key, String value) throws Exception {
        Class<?> cls = ObjectFileConnector.findClassBySimpleName(key.endsWith("s")?key.substring(0, key.length()-1):key);
        if(cls!=null) {
            Object child = cls.newInstance();
            Map<String, Method> methodMap = ObjectFileConnector.getMethods(child, "set");

            value = value.substring(1, value.length()-1);
            if(value.contains(")(")) {
                child = new LinkedList<>();
                for(String item : value.split("\\)\\(")) {
                    Object obj = cls.newInstance();
                    ObjectFileConnector.setData(key, item, child, methodMap);
                    ((List) child).add(obj);
                }
                setter.invoke(parent, (List)child);
            } else {
                ObjectFileConnector.setData(key, value, child, methodMap);
                setter.invoke(parent, child);
            }
        }
    }

    private static void setData(String key, String value, Object child, Map<String, Method> methodMap) throws Exception {
        String[] parts = value.split("\\|");
        int i = 0;
        for(Method method : methodMap.values()) {
            ObjectFileConnector.addMethods(method, key, parts[i], child);
            i++;
        }
    }

    private static void addMethods(Method method, String key, String value, Object object) throws Exception {
        if(!value.trim().contains("_empty_")) {
            if(value.startsWith("(") && value.endsWith(")")) {
                ObjectFileConnector.addObject(object, method, key, value);
            } else {
                ObjectFileConnector.setAttributeToMethod(method, object, value);
            }
        }
    }

    private static Map<String, List<String>> getLinesByHeaderKeys(String header, String content, String separator) {
        Map<String, List<String>> lines = new LinkedHashMap<>();
        content = content.replace(header.substring(0, header.length()-1)+"\n", "");
        String[] spl = header.split(separator);

        for(int i = 0; i<=spl.length-1; i++) {
            List<String> items = new LinkedList<>();
            for(String item : content.split("\n")) {
                items.add(item.split(separator)[i]);
            }
            lines.put(spl[i], items);
        }
        return lines;
    }

    private static String getHeaderFromClass(Class<?> cls, String separator) throws Exception {
        Map<String, Method> methods = ObjectFileConnector.getMethods(cls.newInstance(), "get");
        StringBuilder stringBuilder = new StringBuilder();
        for(String item : methods.keySet()) {
            stringBuilder.append(item);
            stringBuilder.append(separator);
        }
        return stringBuilder.toString();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static Map<String, Method> getMethods(Object object, String pre) {
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

    private static CSVObject getCSVObject(Object object) throws Exception {
        Map<String, Method> methods = ObjectFileConnector.getMethods(object, "get");
        StringBuilder stringBuilder = new StringBuilder();
        for(String item : methods.keySet()) {
            stringBuilder.append(item);
            stringBuilder.append("|");
        }

        CSVObject csvObject = new CSVObject("|", stringBuilder.toString());
        for(Method method : methods.values()) {
            String name = method.getName().replace("get", "");
            Class<?> cls = method.getReturnType();
            if (cls == Integer.class || cls == String.class || cls == Double.class || cls == Date.class || cls == Long.class || cls == Boolean.class
                    || cls == int.class || cls == long.class || cls == boolean.class || cls == double.class) {
                if(cls == Integer.class || cls == int.class || cls == Long.class || cls == long.class) {
                    csvObject.writeValue(name, (int) method.invoke(object));
                }
                if(cls == Double.class || cls == double.class) {
                    csvObject.writeValue(name, (double) method.invoke(object));
                }
                if(cls == String.class) {
                    csvObject.writeValue(name, (String) method.invoke(object));
                }
                if(cls == Boolean.class || cls == boolean.class) {
                    csvObject.writeValue(name, (boolean) method.invoke(object));
                }
                if(cls == Date.class) {
                    csvObject.writeValue(name, (Date) method.invoke(object));
                }
            } else if (cls == List.class) {
                List<CSVObject> objectList = new LinkedList<>();
                for(Object obj : (List) method.invoke(object)) {
                    objectList.add(ObjectFileConnector.getCSVObject(obj));
                }
                csvObject.writeValue(name, objectList, "(", ")");
            } else {
                csvObject.writeValue(name, ObjectFileConnector.getCSVObject(method.invoke(object)), "(", ")");
            }
        }
        return csvObject;
    }
}
