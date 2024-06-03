package com.storedobject.core;

/**
 * A marker interface to denote that the {@link StoredObject} class that implements this will trigger an event
 * when a change is made to its instance data. The event is captured by the platform's internal event management system
 * and is made available for further processing. The change event is triggered only when the changes are saved to the
 * database.
 *
 * @author Syam
 */
public interface TriggerChangeEvent {
}
