package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.office.ODTReport;
import com.storedobject.office.ObjectFiller;

import java.lang.reflect.Constructor;

/**
 * For internal use only.
 *
 * @author Syam
 */
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
        if(ObjectFiller.class.isAssignableFrom(cpClass)) { // ObjectFiller
            try {
                StreamData streamData = printLogicDefinition.getODTFormat();
                if(streamData == null) {
                    throw new Invalid_State("No ODT format found for: " + printLogicDefinition.getPrintLogicClassName());
                }
                ObjectFiller of = (ObjectFiller) cpClass.getConstructor().newInstance();
                ODTReport r = new ODTReport(device, printLogicDefinition.getODTFormat(), of);
                of.setReportingObject(object, printLogicDefinition);
                return r;
            } catch (Throwable e) {
                Throwable cause = e.getCause();
                device.log(cause == null ? e : cause);
                return null;
            }
        }
        String parameter = printLogicDefinition.getParameter();
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
                        if(parameter == null) {
                            constructor = cpClass.getConstructor(dClass, Object.class, oClass);
                        } else {
                            constructor = cpClass.getConstructor(dClass, Object.class, oClass, String.class);
                        }
                        break;
                    } catch(NoSuchMethodException ignored) {
                    }
                    try {
                        if(parameter == null) {
                            constructor = cpClass.getConstructor(Device.class, Object.class, oClass);
                        } else {
                            constructor = cpClass.getConstructor(Device.class, Object.class, oClass, String.class);
                        }
                        break;
                    } catch(NoSuchMethodException ignored) {
                    }
                }
                sourceAvailable = false;
                try {
                    if(parameter == null) {
                        constructor = cpClass.getConstructor(dClass, oClass);
                    } else {
                        constructor = cpClass.getConstructor(dClass, oClass, String.class);
                    }
                    break;
                } catch(NoSuchMethodException ignored) {
                }
                try {
                    if(parameter == null) {
                        constructor = cpClass.getConstructor(Device.class, oClass);
                    } else {
                        constructor = cpClass.getConstructor(Device.class, oClass, String.class);
                    }
                    break;
                } catch(NoSuchMethodException ignored) {
                }
                oClass = oClass.getSuperclass();
            }
            if(constructor != null) {
                try {
                    var ex = (Runnable) (sourceAvailable ?
                            (parameter == null ? constructor.newInstance(device, source,  object) : constructor.newInstance(device, object, parameter))
                            : (parameter == null ? constructor.newInstance(device, object) : constructor.newInstance(device, object, parameter)));
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
