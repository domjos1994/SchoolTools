/*
 * Copyright (C) 2017  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.exceptions;

import android.content.Context;

import de.domjos.schooltools.core.model.marklist.MarkList;
import de.domjos.schooltools.helper.Helper;

/**
 * Exception-Class for the MarkLists
 * @see java.lang.Exception
 * @author Dominic Joas
 * @version 1.0
 */
public class MarkListException extends Exception {
    private int msgID;
    private MarkList markList;
    
    public MarkListException(Context context, int msgID, MarkList markList) {
        super(Helper.getLanguage(context, msgID));
        this.msgID = msgID;
        this.markList = markList;
    }

    /**
     * @return the msgID
     */
    public int getMsgID() {
        return msgID;
    }

    /**
     * @param msgID the msgID to set
     */
    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    /**
     * @return the markList
     */
    public MarkList getMarkList() {
        return markList;
    }

    /**
     * @param markList the markList to set
     */
    public void setMarkList(MarkList markList) {
        this.markList = markList;
    }
}
