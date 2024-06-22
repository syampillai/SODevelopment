package com.storedobject.core;

/**
 * This class handles the logic related to notifying changes in data.
 * It extends the DataLogic class.
 *
 * @author Syam
 */
public class DataChangeNotifierLogic extends DataLogic {

    /**
     * The default constructor.
     */
    public DataChangeNotifierLogic() {
    }

    /**
     * Method for column-related operations.
     *
     * @param columns accepts a Columns object.
     */
    public static void columns(Columns columns) {
    }

    /**
     * Method for index-related operations.
     * The Index 'DataClassName' is added in this method.
     *
     * @param indices accepts an Indices object.
     */
    public static void indices(Indices indices) {
        indices.add("DataClassName", true);
    }

    /**
     * Method to get the unique condition based on the class name.
     *
     * @return String value representing the unique condition.
     */
    @Override
    public String getUniqueCondition() {
        return "DataClassName='"
                + getDataClassName().trim().replace("'", "''")
                + "'";
    }

    /**
     * Validates the data using the parent class' validateData method.
     * Also, validates if the logic class is a 'Data Change Notifier'.
     *
     * @param tm accepts a TransactionManager object.
     * @throws Exception If validation fails, it throws an exception.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        Class<?> logicClass = getLogicClass();
        if(logicClass == null || !DataChangeNotifier.class.isAssignableFrom(logicClass)) {
            throw new Invalid_State("Not a 'Data Change Notifier'");
        }
        try {
            logicClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new Invalid_State(logicClass.getName() + " should have a default public constructor");
        }
    }

    /**
     * Retrieves the DataChangeNotifier class for a given data class.
     *
     * @param dataClass the data class for which to retrieve the DataChangeNotifier class
     * @return the DataChangeNotifier class for the given data class, or null if the dataClass is null
     * or the dataClass is not assignable from StoredObject class
     */
    public static Class<DataChangeNotifier> getFor(Class<?> dataClass) {
        if(dataClass == null || dataClass == StoredObject.class || !dataClass.isAssignableFrom(StoredObject.class)) {
            return null;
        }
        DataChangeNotifierLogic logic = get(DataChangeNotifierLogic.class, "DataClassName='"
                + dataClass.getName() + "'");
        if(logic != null) {
            //noinspection unchecked
            return (Class<DataChangeNotifier>) logic.getLogicClass();
        }
        return getFor(dataClass.getSuperclass());
    }
}