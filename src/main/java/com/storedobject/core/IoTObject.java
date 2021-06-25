package com.storedobject.core;

import java.sql.Timestamp;

/**
 * IoT (Internet of Things) object that is specially treated. When transacted singly, it doesn't maintain any
 * history or audit trail. This is not used for keeping normal transactional data and is mostly created or updated by
 * IoT data capture processes. The {@link #directUpdate(TransactionManager)} method can be used to directly update
 * the data in the case of existing instances without even creating new transactions.
 *
 * @author Syam
 */
public abstract class IoTObject extends StoredObject{

    /**
     * Mandatory attribute - data collection time.
     *
     * @param collectedAt Data collection time.
     */
    public void setCollectedAt(Timestamp collectedAt) {
    }

    /**
     * Get the time at which this data is collected.
     *
     * @return Data collection time.
     */
    public Timestamp getCollectedAt() {
        return DateUtility.now();
    }

    /**
     * Do direct update without creating a transaction. If the instance is not yet in the database, a new entry is
     * created in the database via a transaction.
     *
     * @param tm Currently valid transaction manager.
     * @throws Exception If any error occurs.
     */
    public void directUpdate(TransactionManager tm) throws Exception {
    }

    /**
     * Set the unit {@link Id} of this IoT object. Typically, every IoT object instance belongs to some unit
     * (plant or equipment being monitored). There must be a "data class" for every such unit and the {@link Id} of
     * that unit must be set here. Rarely, an IoT object itself may be independent of any unit (for example,
     * generic values like temperature/humidity of an environment etc.) and in such cases, an empty implementation of
     * this method is fine.
     *
     * @param unitId Id of the unit.
     */
    public abstract void setUnit(Id unitId);

    /**
     * Get the latest data record collected.
     *
     * @param objectClass IOT object class.
     * @param <IOT> Type of object class.
     * @return Latest data record or null if not available.
     */
    public static <IOT extends IoTObject> IOT getLatest(Class<IOT> objectClass) {
        return objectClass == null ? null : get(objectClass);
    }
}
