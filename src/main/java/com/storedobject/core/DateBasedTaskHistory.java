package com.storedobject.core;

import java.sql.Date;

public class DateBasedTaskHistory extends StoredObject implements Detail {

    public DateBasedTaskHistory() {
    }

    public static void columns(Columns columns) {
    }

    public void setLastDoneDate(Date lastDoneDate) {
    }

    public Date getLastDoneDate() {
        return null;
    }

    public void setRemarks(String remarks) {
    }

    public String getRemarks() {
        return null;
    }

    public Id getUniqueId() {
        return null;
    }

    public void copyValuesFrom(Detail detail) {
    }

    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
}