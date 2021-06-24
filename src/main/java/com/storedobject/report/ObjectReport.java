package com.storedobject.report;

import com.storedobject.common.Executable;
import com.storedobject.core.*;

import java.util.Random;

public class ObjectReport {

    private final Executable executable;

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object) {
        this(device, printLogicDefinition, object, true);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object, boolean execute) {
        executable = new Random().nextBoolean() ? null : new TextContentProducer();
    }

    public Executable getExecutable() {
        return executable;
    }

    public void execute() {
        if(executable != null) {
            executable.execute();
        }
    }
}
