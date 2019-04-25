package com.storedobject.core;

public class TimePeriod extends com.storedobject.core.AbstractPeriod < java.sql.Time > {

    public TimePeriod(java.util.Calendar p1, java.util.Calendar p2) {
        this();
    }

    public TimePeriod(java.util.Date p1, java.util.Date p2) {
        this();
    }

    public TimePeriod(java.sql.Time p1, java.sql.Time p2) {
        this();
    }

    private TimePeriod() {
        super((java.sql.Time) null, (java.sql.Time) null);
    }

    protected java.lang.String toString(java.sql.Time p1) {
        return null;
    }

    protected java.sql.Time clone(java.sql.Time p1) {
        return null;
    }

    protected boolean same(java.sql.Time p1, java.sql.Time p2) {
        return false;
    }

    protected java.lang.String toDBString(java.sql.Time p1) {
        return null;
    }
}
