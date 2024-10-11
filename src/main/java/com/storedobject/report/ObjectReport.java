package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.office.ODTReport;

import java.lang.reflect.Constructor;

public class ObjectReport {

    private final Runnable executable;

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object) {
        this(device, printLogicDefinition, object, true);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object, boolean execute) {
        this(device, printLogicDefinition, null, object, execute);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, Object objectSource, StoredObject object) {
        this(device, printLogicDefinition, objectSource, object, true);
    }

    public ObjectReport(Device device, PrintLogicDefinition printLogicDefinition, Object objectSource, StoredObject object,
                        boolean execute) {
        if(object == null) {
            executable = () -> {};
        } else {
            executable = create(device, printLogicDefinition, object, objectSource);
            if (execute && executable != null) {
                executable.run();
            }
        }
    }

    private Runnable create(Device device, PrintLogicDefinition printLogicDefinition, StoredObject object, Object source) {
        var cpClass = printLogicDefinition.getLogicClass();
        if(cpClass == null) {
            device.log("Unable to create " + printLogicDefinition.getPrintLogicClassName());
            return null;
        }
        Constructor<?> constructor;
        Class<?> dClass = device.getClass();
        Class<?> oClass;
        boolean sourceAvailable = false;
        while(dClass != null) {
            constructor = null;
            oClass = object.getClass();
            while(StoredObject.class.isAssignableFrom(oClass)) {
                if(source != null) {
                    sourceAvailable = true;
                    try {
                        constructor = cpClass.getConstructor(dClass, Object.class, oClass);
                        break;
                    } catch(NoSuchMethodException ignored) {
                    }
                    try {
                        constructor = cpClass.getConstructor(Device.class, Object.class, oClass);
                        break;
                    } catch(NoSuchMethodException ignored) {
                    }
                }
                sourceAvailable = false;
                try {
                    constructor = cpClass.getConstructor(dClass, oClass);
                    break;
                } catch(NoSuchMethodException ignored) {
                }
                try {
                    constructor = cpClass.getConstructor(Device.class, oClass);
                    break;
                } catch(NoSuchMethodException ignored) {
                }
                oClass = oClass.getSuperclass();
            }
            if(constructor != null) {
                try {
                    Runnable ex = (Runnable) (sourceAvailable ? constructor.newInstance(device, source,  object)
                            : constructor.newInstance(device, object));
                    if(!Id.isNull(printLogicDefinition.getODTFormatId()) &&
                            ODTReport.class.isAssignableFrom(cpClass)) {
                        ((ODTReport)ex).setTemplate(printLogicDefinition.getODTFormatId());
                    }
                    return ex;
                } catch(Throwable e) {
                    Throwable cause = e.getCause();
                    device.log(cause == null ? e : cause);
                }
                return null;
            }
            dClass = dClass.getSuperclass();
        }
        device.log("Unable to create " + printLogicDefinition.getPrintLogicClassName());
        return null;
    }

    public Runnable getRunnable() {
        return executable;
    }

    public void execute() {
        if(executable != null) {
            executable.run();
        }
    }
}
