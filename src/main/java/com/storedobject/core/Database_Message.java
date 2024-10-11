package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

public abstract class Database_Message extends SORuntimeException {

    @Override
    public String getEndUserMessage() {
        String m = getClass().getName();
        m = m.substring(m.lastIndexOf('.') + 1);
        m = "Error: " + m.replace('_', ' ');
        String id = getCustomMessage();
        if(id != null) {
            m += " (" + id + ")";
        }
        return m;
    }
}

