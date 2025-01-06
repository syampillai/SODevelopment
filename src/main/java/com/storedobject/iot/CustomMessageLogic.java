package com.storedobject.iot;

import com.storedobject.core.*;
import java.lang.reflect.Modifier;

public final class CustomMessageLogic extends StoredObject implements RequiresApproval {

    private String logicName;
    private boolean active;
    private Class<CustomMessageProcessor> processorClass;

    public CustomMessageLogic() {
        active = true;
    }

    public static void columns(Columns columns) {
        columns.add("LogicName", "text");
        columns.add("Active", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("LogicName", true);
    }

    @Override
    public String getUniqueCondition() {
        return "LogicName='" + getLogicName().trim().replace("'", "''") + "'";
    }

    public void setLogicName(String logicName) {
        this.logicName = logicName;
    }

    public String getLogicName() {
        return logicName;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(logicName)) {
            throw new Invalid_Value("Logic Name");
        }
        logicName = logicName.strip();
        if(getProcessorClass() == null) {
            throw new Invalid_Value("Logic Name");
        }
        checkForDuplicate("LogicName");
        super.validateData(tm);
    }

    public Class<CustomMessageProcessor> getProcessorClass() {
        if(processorClass != null) {
            return processorClass;
        }
        try {
            @SuppressWarnings("unchecked")
            Class<CustomMessageProcessor> sc = (Class<CustomMessageProcessor>)JavaClassLoader.getLogic(logicName);
            if(Modifier.isAbstract(sc.getModifiers())) {
                return null;
            }
            processorClass = sc;
            return processorClass;
        } catch(Throwable error) {
            return null;
        }
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        processorClass = null;
    }
}