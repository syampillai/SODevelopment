package com.storedobject.core;

public abstract class Database_Id_Message extends Database_Message {

    private final String id;
    private final String tail;

    public Database_Id_Message(String id) {
        int p = id.indexOf(',');
        if(p > 0) {
            tail = id.substring(p + 1).trim();
            id = id.substring(0, p);
        } else {
            tail = null;
        }
        this.id = id;
    }

    protected String getCustomMessage() {
        return tail == null ? id : (id + ", " + tail);
    }

    public Id getId() {
        return new Id(id);
    }

    String getTail() {
        return tail;
    }
}
