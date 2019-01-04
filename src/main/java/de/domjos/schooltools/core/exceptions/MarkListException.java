/*
 * Copyright (C) 2017-2019  Dominic Joas
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
    private final Context context;
    private final int msgID;
    private final MarkList markList;

    /**
     * Default-Constructor
     * @param context context of the app to get the lang-property
     * @param msgID id of the lang-property for the message
     * @param markList the marklist with the exception
     */
    public MarkListException(Context context, int msgID, MarkList markList) {
        super(Helper.getLanguage(context, msgID));
        this.context = context;
        this.msgID = msgID;
        this.markList = markList;
    }

    /**
     * Get the marklist with the exception
     * @return the markList
     */
    private MarkList getMarkList() {
        return markList;
    }

    /**
     * Overrides the toString-Method to add Marklist-Data to toString()
     * @see Exception#toString()
     * @return The Data of the Exception
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s%n%s%n%n", this.context.getString(this.msgID), this.getMarkList().toString()));
        for(StackTraceElement element : this.getStackTrace()) {
            builder.append(String.format("%s(%s#%s:%s)%n", element.getFileName(), element.getClassName(), element.getMethodName(), element.getLineNumber()));
        }
        return builder.toString();
    }
}
