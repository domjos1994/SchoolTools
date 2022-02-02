/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.utils.xFileUtils;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltoolslib.model.Bookmark;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.timetable.Teacher;

public class ObjectXMLTest {

    @Test
    public void convertObjectToXMLTest() throws Exception {
        List<Bookmark> bookmarkList = new LinkedList<>();
        Bookmark bookmark1 = new Bookmark();
        bookmark1.setTitle("My Website");
        bookmark1.setDescription("This is my website!");
        bookmark1.setTags("website, test, my");
        bookmark1.setID(1);

        Bookmark bookmark2 = new Bookmark();
        bookmark2.setID(2);
        bookmark2.setTitle("App-Picture");
        bookmark2.setTags("app, picture,....");

        Subject subject = new Subject();
        subject.setID(1);
        subject.setAlias("D");
        subject.setTitle("German");
        subject.setMainSubject(true);
        subject.setHoursInWeek(4);

        Teacher teacher = new Teacher();
        teacher.setFirstName("Mr");
        teacher.setLastName("Bond");
        subject.setTeacher(teacher);

        bookmark2.setSubject(subject);
        InputStream byteArrayInputStream =new FileInputStream(new File("data/icon.png"));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = byteArrayInputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        bookmark2.setData(buffer.toByteArray());

        bookmarkList.add(bookmark1);
        bookmarkList.add(bookmark2);

        ObjectXML.saveObjectListToXML("ToDoLists", bookmarkList, "test.xml");
        List<Object> list = ObjectXML.saveXMLToObjectList("test.xml");
    }
}
