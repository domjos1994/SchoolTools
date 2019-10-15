/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.utils.fileUtils;

import android.content.Context;
import android.graphics.Color;
import androidx.test.platform.app.InstrumentationRegistry;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.utils.Helper;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class FileBridgeTest {
    private LearningCardGroup group;

    @Before
    public void init() {
        group = new LearningCardGroup();
        group.setID(1);
        group.setDeadLine(new Date());
        group.setCategory("Vocabulary");
        group.setDescription("This is a test!");
        group.setTitle("Hello World");
        Teacher teacher = new Teacher();
        teacher.setFirstName("Alfons");
        teacher.setLastName("Kalfons");
        group.setTeacher(teacher);

        Subject subject = new Subject();
        subject.setAlias("Eng");
        subject.setTitle("English");
        subject.setID(1);
        subject.setHoursInWeek(4);
        subject.setMainSubject(true);
        subject.setTeacher(teacher);
        subject.setDescription("F**king");
        subject.setBackgroundColor(String.valueOf(Color.BLACK));
        group.setSubject(subject);

        LearningCard who = new LearningCard();
        who.setID(1);
        who.setTitle("wer");
        who.setQuestion("wer");
        who.setAnswer("wo");

        LearningCard why = new LearningCard();
        why.setID(2);
        why.setTitle("warum");
        why.setQuestion("warum");
        why.setAnswer("why");

        LearningCard what = new LearningCard();
        what.setID(3);
        what.setTitle("was");
        what.setQuestion("was");
        what.setAnswer("what");
        group.getLearningCards().add(who);
        group.getLearningCards().add(why);
        group.getLearningCards().add(what);
    }

    @Test
    public void testObjectToFile() throws Exception {
        FileBridge fileBridge = new FileBridge(this.group, "test.csv", Helper.getContext());
        fileBridge.writeObjectToFile();
    }

    @Test
    public void testObjectFromFile() throws Exception {
        FileBridge fileBridge = new FileBridge(null, "test.csv", Helper.getContext());
        fileBridge.readObjectFromFile();
    }
}
