package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

public class Error_Running_SQL extends SORuntimeException {

    public Error_Running_SQL(int p1, java.lang.String p2, java.lang.String p3) {
        this();
    }

    public Error_Running_SQL(int p1, java.lang.String p2, java.lang.Exception p3) {
        this();
    }

    private Error_Running_SQL() {
    }

    public java.lang.Exception getException() {
        return null;
    }

    public java.lang.String getState() {
        return null;
    }

    public void setSQL(java.lang.String p1) {
    }

    public int getError() {
        return 0;
    }
}
