package com.storedobject.core;

import com.storedobject.core.annotation.*;

public class ApplicationAlertHandler extends StoredObject {

    private String dataClassName;
    private String logicClassName;

    public ApplicationAlertHandler() {
    }

    public static void columns(Columns columns) {
        columns.add("DataClassName", "text");
        columns.add("LogicClassName", "text");
    }

    public static void indices(Indices indices) {
        indices.add("DataClassName", true);
    }

    public String getUniqueCondition() {
        return "DataClassName='"
                + getDataClassName().trim().replace("'", "''")
                + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setDataClassName(String dataClassName) {
        this.dataClassName = dataClassName;
    }

    @Column(order = 100)
    public String getDataClassName() {
        return dataClassName;
    }

    public void setLogicClassName(String logicClassName) {
        this.logicClassName = logicClassName;
    }

    @Column(order = 200)
    public String getLogicClassName() {
        return logicClassName;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(dataClassName) || getDataClass() == null) {
            throw new Invalid_Value("Data Class Name");
        }
        if (StringUtility.isWhite(logicClassName) || getLogicClass() == null) {
            throw new Invalid_Value("Logic Class Name");
        }
        super.validateData(tm);
    }

    public final Class<? extends StoredObject> getDataClass() {
        try {
            //noinspection unchecked
            return (Class<? extends StoredObject>) JavaClassLoader.getLogic(dataClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }

    public final Class<?> getLogicClass() {
        try {
            //noinspection
            return JavaClassLoader.getLogic(logicClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }
}
