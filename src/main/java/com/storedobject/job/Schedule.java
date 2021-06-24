package com.storedobject.job;

import com.storedobject.core.Device;
import com.storedobject.core.StoredObject;

public class Schedule extends StoredObject {

    private Device device;

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
}