package de.domjos.schooltools.core.model.timetable;

import de.domjos.schooltools.core.model.Subject;

public class PupilHour {
    private Subject subject;
    private Teacher teacher;
    private String roomNumber;

    public PupilHour(Subject subject, Teacher teacher, String roomNumber) {
        this.subject = subject;
        this.teacher = teacher;
        this.roomNumber = roomNumber;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
