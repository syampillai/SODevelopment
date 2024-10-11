package com.storedobject.iot;

import com.storedobject.core.*;
import java.lang.reflect.Modifier;

public final class CustomMessageLogic extends StoredObject implements RequiresApproval {

    private String messageKey;
    private String logicName;
    private boolean active;
    private Class<CustomMessageProcessor> processorClass;

    public CustomMessageLogic() {
        active = true;
    }

    public static void columns(Columns columns) {
        columns.add("MessageKey", "text");
        columns.add("LogicName", "text");
        columns.add("Active", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(MessageKey)", true);
    }

    @Override
    public String getUniqueCondition() {
        return "lower(MessageKey)='" + getMessageKey().trim().toLowerCase().
                replace("'", "''") + "'";
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
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
        messageKey = messageKey == null ? "" : messageKey.strip();
        if(StringUtility.isWhite(messageKey)) {
            throw new Invalid_Value("Connector Command");
        }
        if(StringUtility.isWhite(logicName)) {
            throw new Invalid_Value("Logic Name");
        }
        logicName = logicName.strip();
        super.validateData(tm);
    }

    public static CustomMessageLogic get(String messageKey) {
        return get(CustomMessageLogic.class, "lower(MessageKey)='"
                + messageKey.toLowerCase().replace("'", "''") + "'");
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
}