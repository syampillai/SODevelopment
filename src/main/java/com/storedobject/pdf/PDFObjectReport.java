package com.storedobject.pdf;

import com.storedobject.core.Device;
import com.storedobject.core.StoredObject;

public abstract class PDFObjectReport<T extends StoredObject> extends PDFReport {

    protected final T object;

    public PDFObjectReport(Device device, T object) {
        super(device);
        this.object = object;
    }

    public PDFObjectReport(Device device, T object, boolean letterHead) {
        super(device, letterHead);
        this.object = object;
    }

    public PDFObjectReport(Device device, T object, boolean letterHead, boolean printLogo) {
        super(device, letterHead, printLogo);
        this.object = object;
    }

    public final T getObject() {
        return object;
    }
}
