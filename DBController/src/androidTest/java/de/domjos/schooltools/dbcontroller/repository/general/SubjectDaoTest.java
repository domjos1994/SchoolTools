package de.domjos.schooltools.dbcontroller.repository.general;

import org.junit.Assert;
import org.junit.Test;

import de.domjos.schooltools.dbcontroller.BaseTest;
import de.domjos.schooltools.dbcontroller.model.general.Subject;
import de.domjos.schooltools.dbcontroller.model.general.Teacher;

public class SubjectDaoTest extends BaseTest {

    private Teacher getTeacher() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        this.teacherDao.insertAll(teacher);
        return this.teacherDao.getTeacherByName(teacher.getFirstName(), teacher.getLastName());
    }

    @Test
    public void testInsertDelete() {
        Teacher teacher = this.getTeacher();

        Subject subject = new Subject();
        subject.setTitle("Math");
        subject.setAlias("M");
        subject.setMainSubject(true);
        subject.setTeacherID(teacher.getID());
        this.subjectDao.insertAll(subject);

        Assert.assertEquals(this.subjectDao.countSubjects(), 1);

        subject = this.subjectDao.getSubjectByTitle(subject.getTitle());
        this.subjectDao.deleteAll(subject);
        Assert.assertEquals(this.subjectDao.countSubjects(), 0);
    }
}
