/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltoolslib.model.learningCard;

import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.objects.BaseCategoryObject;
import de.domjos.schooltoolslib.model.timetable.Teacher;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LearningCardGroup extends BaseCategoryObject {
    private Date deadLine;
    private Subject subject;
    private Teacher teacher;
    private List<LearningCard> learningCards;

    public LearningCardGroup() {
        super();
        this.deadLine = null;
        this.subject = null;
        this.teacher = null;
        this.learningCards = new LinkedList<>();
    }

    public Date getDeadLine() {
        return this.deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
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

    public List<LearningCard> getLearningCards() {
        return this.learningCards;
    }

    public void setLearningCards(List<LearningCard> learningCards) {
        this.learningCards = learningCards;
    }
}
