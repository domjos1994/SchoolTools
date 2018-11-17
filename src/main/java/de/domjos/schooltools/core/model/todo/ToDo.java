/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.todo;

import de.domjos.schooltools.core.model.objects.BaseCategoryObject;

import java.util.Date;

/**
 * Model-Class for the toDos
 * @see de.domjos.schooltools.activities.ToDoActivity
 * @see de.domjos.schooltools.core.model.todo.ToDoList
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDo extends BaseCategoryObject {
    private int importance;
    private boolean solved;
    private Date memoryDate;

    public ToDo() {
        super();
        this.importance = 0;
        this.solved = false;
        this.memoryDate = null;
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

    public Date getMemoryDate() {
        if(this.memoryDate!=null) {
            return (Date) this.memoryDate.clone();
        } else {
            return null;
        }
    }

    public void setMemoryDate(Date memoryDate) {
        if(memoryDate!=null) {
            this.memoryDate = (Date) memoryDate.clone();
        } else {
            this.memoryDate = null;
        }
    }
}
