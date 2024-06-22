package com.storedobject.core;

/**
 * Represents a data change event for a specific object.
 *
 * @author Syam
 */
public final class DataChanged {

    /**
     * Enum representing the types of changes that can occur.
     */
    public enum CHANGE {
        INSERTED, UPDATED, DELETED, UNDELETED, MIGRATED, LINK_CHANGED, CUSTOM
    }

    private final TransactionManager tm;
    private final byte change;
    private final StoredObject object;

    /**
     * Initializes a new instance of the DataChanged class.
     *
     * @param object The object that has changed.
     * @param change The change that occurred.
     */
    DataChanged(TransactionManager tm, StoredObject object, byte change) {
        this.tm = tm;
        this.object = object;
        this.change = change;
    }

    /**
     * Retrieves the object instance that was changed.
     *
     * @return the object instance
     */
    public StoredObject getObject() {
        return object;
    }

    /**
     * Get the type of change.
     *
     * @return Type of change.
     */
    public CHANGE getChange() {
        return getChange(change);
    }

    /**
     * Get the type of change from a byte code.
     *
     * @param change Byte code representing the type of change.
     * @return Type of change.
     */
    public static CHANGE getChange(byte change) {
        return switch (change) {
            case 0 -> CHANGE.INSERTED;
            case 1 -> CHANGE.UPDATED;
            case 2 -> CHANGE.DELETED;
            case 3 -> CHANGE.UNDELETED;
            case 4 -> CHANGE.MIGRATED;
            case 5 -> CHANGE.LINK_CHANGED;
            default -> CHANGE.CUSTOM;
        };
    }

    /**
     * Get the byte code of the change.
     *
     * @return The byte code of the change.
     */
    public byte getChangeCode() {
        return change;
    }

    /**
     * Retrieves the TransactionManager that can be used for further processing.
     *
     * @return the TransactionManager that can be used fr further processing.
     */
    public TransactionManager getTransactionManager() {
        return tm;
    }
}