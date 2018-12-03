package de.domjos.schooltools.core.utils.fileUtils;

import android.graphics.Color;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.timetable.Teacher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ObjectFileConnectorTest {
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
    public void testXML() throws Exception {
        File xmlExportFile = new File("test.xml");
        ObjectFileConnector.ObjectToFile(group, xmlExportFile, ';');
        int oldID = group.getID();
        group = (LearningCardGroup) ObjectFileConnector.FileToObject(xmlExportFile, LearningCardGroup.class, "");
        assertEquals(group.getID(), oldID);
    }

    @Test
    public void testCSV() throws Exception {
        File xmlExportFile = new File("test.csv");
        ObjectFileConnector.ObjectToFile(group, xmlExportFile, ';');
        int oldID = group.getID();
        group = (LearningCardGroup) ObjectFileConnector.FileToObject(xmlExportFile, LearningCardGroup.class, ";");
        assertEquals(group.getID(), oldID);
    }
}
