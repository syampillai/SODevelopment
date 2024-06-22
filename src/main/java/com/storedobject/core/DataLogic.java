package com.storedobject.core;

import com.storedobject.core.annotation.*;

/**
 * An abstract class that deals with data classes and their respective logic classes.
 *
 * @author Syam
 */
public abstract class DataLogic extends StoredObject {

    protected String dataClassName;
    protected String logicClassName;

    /**
     * Default constructor.
     */
    public DataLogic() {
    }

    /**
     * Adds columns "DataClassName" and "LogicClassName" to the provided columns object.
     *
     * @param columns the columns to which new elements are to be added.
     */
    public static void columns(Columns columns) {
        columns.add("DataClassName", "text");
        columns.add("LogicClassName", "text");
    }

    /**
     * Returns hints.
     *
     * @return ObjectHint.SMALL_LIST
     */
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    /**
     * Sets Data Class Name.
     *
     * @param dataClassName the name of the data class.
     */
    public void setDataClassName(String dataClassName) {
        this.dataClassName = dataClassName;
    }

    /**
     * Gets Data Class Name.
     *
     * @return Data Class Name
     */
    @Column(order = 100)
    public String getDataClassName() {
        return dataClassName;
    }

    /**
     * Sets Logic Class Name.
     *
     * @param logicClassName the name of the logic class.
     */
    public void setLogicClassName(String logicClassName) {
        this.logicClassName = logicClassName;
    }

    /**
     * Gets Logic Class Name.
     *
     * @return Logic Class Name
     */
    @Column(order = 200)
    public String getLogicClassName() {
        return logicClassName;
    }

    /**
     * Validates input data. Throws an exception if inputs are invalid.
     *
     * @param tm A TransactionManager object
     * @throws Exception with a relevant message if validation fails
     */
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

    /**
     * Returns the Data Class instance. If it fails to load, it returns null.
     *
     * @return The class object of the corresponding data class
     */
    public final Class<? extends StoredObject> getDataClass() {
        try {
            //noinspection unchecked
            return (Class<? extends StoredObject>) JavaClassLoader.getLogic(dataClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }

    /**
     * Returns the Logic Class instance. If it fails to load, it returns null.
     *
     * @return The class object of the corresponding logic class
     */
    public final Class<?> getLogicClass() {
        try {
            return JavaClassLoader.getLogic(logicClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }
}