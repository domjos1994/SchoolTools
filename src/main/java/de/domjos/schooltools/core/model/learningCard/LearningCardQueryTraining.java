/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.learningCard;

import java.util.LinkedList;
import java.util.List;

public class LearningCardQueryTraining {
    private int ID;
    private LearningCardQuery learningCardQuery;
    private List<LearningCardQueryResult> results;

    public LearningCardQueryTraining() {
        this.ID = 0;
        this.learningCardQuery = null;
        this.results = new LinkedList<>();
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public LearningCardQuery getLearningCardQuery() {
        return this.learningCardQuery;
    }

    public void setLearningCardQuery(LearningCardQuery learningCardQuery) {
        this.learningCardQuery = learningCardQuery;
    }

    public List<LearningCardQueryResult> getResults() {
        return this.results;
    }

    public void setResults(List<LearningCardQueryResult> results) {
        this.results = results;
    }
}
