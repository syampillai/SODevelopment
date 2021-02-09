package com.storedobject.report;

import com.storedobject.core.*;

import java.util.Random;

public class ObjectReport {

    private final ContentProducer contentProducer;

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object) {
        this(device, printLogicDefinition, object, true);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object, boolean execute) {
        contentProducer = new Random().nextBoolean() ? null : new TextContentProducer();
    }

    public ContentProducer getContentProducer() {
        return contentProducer;
    }
}
