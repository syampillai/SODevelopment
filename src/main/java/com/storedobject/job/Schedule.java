package com.storedobject.job;

import com.storedobject.core.Device;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionManager;

public class Schedule extends StoredObject {

    private Device device;
    private TransactionManager tm;

    public Schedule() {
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getName() {
        return "";
    }

    public String getDescription() {
        return "";
    }

    public TransactionManager getTransactionManager() {
        return tm;
    }

    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }
}