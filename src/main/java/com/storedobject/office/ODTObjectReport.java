package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;

public class ODTObjectReport<T extends StoredObject> extends ODTReport {

    protected final T object;

    public ODTObjectReport(Device device, PrintLogicDefinition printLogicDefinition, T object) {
        super(device, printLogicDefinition.getODTFormatId());
        this.object = object;
    }

    public final T getObject() {
        return object;
    }

    public Object fill(String name) {
        return "?";
    }
}
