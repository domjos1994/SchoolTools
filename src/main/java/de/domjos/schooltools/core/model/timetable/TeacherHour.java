package de.domjos.schooltools.core.model.timetable;

import de.domjos.schooltools.core.model.Subject;

public class TeacherHour {
    private Subject subject;
    private SchoolClass schoolClass;
    private String roomNumber;

    public TeacherHour(Subject subject, SchoolClass schoolClass, String roomNumber) {
        this.subject = subject;
        this.schoolClass = schoolClass;
        this.roomNumber = roomNumber;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
