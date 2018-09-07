/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.model.todo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Model-Class for the ToDo-Lists
 * @see de.domjos.schooltools.activities.ToDoActivity
 * @see de.domjos.schooltools.core.model.todo.ToDo
 * @author Dominic Joas
 * @version 1.0
 */
public class ToDoList {
    private int ID;
    private String title;
    private String description;
    private Date listDate;
    private List<ToDo> toDos;

    public ToDoList() {
        this.ID = 0;
        this.title = "";
        this.description = "";
        this.listDate = null;
        this.toDos = new LinkedList<>();
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

    public Date getListDate() {
        if(this.listDate!=null) {
            return (Date) this.listDate.clone();
        } else {
            return null;
        }
    }

    public void setListDate(Date listDate) {
        if(listDate!=null) {
            this.listDate = (Date) listDate.clone();
        } else {
            this.listDate = null;
        }
    }

    public List<ToDo> getToDos() {
        return this.toDos;
    }

    public void addToDo(ToDo toDo) {
        this.toDos.add(toDo);
    }

    public void setToDos(List<ToDo> toDos) {
        this.toDos = toDos;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
