package com.storedobject.core;

/**
 * A marker interface to denote that the {@link StoredObject} class that implements this will trigger an event
 * when a change is made to its instance data. The event is captured by the platform's internal event management system
 * and is made available for further processing. The change event is triggered only when the changes are saved to the
 * database.
 *
 * <p>Typically, the change is passed to a {@link DataChangeNotifier} once the transaction is committed. A
 * {@link DataChangeNotifier} can be registered with the platform by defining an entry in the
 * {@link DataChangeNotifierLogic}.</p>
 *
 * <p>The {@link DataChangeNotifier} receives the changes as a list of {@link DataChanged} instances.</p>
 * @author Syam
 */
public interface TriggerChangeEvent {

    /**
     * Get the current change code. Typically, change code is the ordinal value of the
     * {@link com.storedobject.core.DataChanged.CHANGE} passed as the parameter. However, by returning a value
     * above 9, a custom change code can be created. (This will be available to the {@link DataChangeNotifier} later).
     *
     * @param change Change that happened now.
     * @return Change code.
     */
    default byte changeCode(DataChanged.CHANGE change) {
        return (byte) change.ordinal();
    }
}
