package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;

public class ObjectReport {

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object) {
        this(device, printLogicDefinition, object, true);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object, boolean execute) {
    }

    public Runnable getRunnable() {
        return () -> {};
    }

    public void execute() {
    }
}
