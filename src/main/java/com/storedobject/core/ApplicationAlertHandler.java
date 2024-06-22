package com.storedobject.core;

/**
 * You can insert an entry in this data class for handling application alerts.
 *
 * @author Syam
 */
public class ApplicationAlertHandler extends DataLogic {

    /**
     * Constructor for the ApplicationAlertHandler class.
     * Initializes a new instance of the class.
     */
    public ApplicationAlertHandler() {
    }

    /**
     * Takes in a Columns object and performs some operation.
     *
     * @param columns the Columns object to perform the operation on
     */
    public static void columns(Columns columns) {
    }

    /**
     * Adds an index to the given Indices object.
     *
     * @param indices The Indices object to which the index will be added.
     *                This object keeps track of all the indices.
     */
    public static void indices(Indices indices) {
        indices.add("DataClassName", true);
    }

    /**
     * Returns the unique condition for the data class.
     *
     * @return the unique condition in the format "DataClassName='DataClassNameValue'"
     */
    @Override
    public String getUniqueCondition() {
        return "DataClassName='"
                + getDataClassName().trim().replace("'", "''")
                + "'";
    }
}
