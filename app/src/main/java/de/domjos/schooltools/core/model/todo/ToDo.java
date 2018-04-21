/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.todo;

import java.util.Date;

/**
 * Model-Class for the toDos
 * @see de.domjos.schooltools.activities.ToDoActivity
 * @see de.domjos.schooltools.core.model.todo.ToDoList
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDo {
    private int ID;
    private String title;
    private String description;
    private int importance;
    private boolean solved;
    private String category;
    private Date memoryDate;

    public ToDo() {
        this.ID = 0;
        this.title = "";
        this.description = "";
        this.importance = 0;
        this.solved = false;
        this.category = "";
        this.memoryDate = null;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImportance() {
        return this.importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public boolean isSolved() {
        return this.solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getMemoryDate() {
        return this.memoryDate;
    }

    public void setMemoryDate(Date memoryDate) {
        this.memoryDate = memoryDate;
    }
}
