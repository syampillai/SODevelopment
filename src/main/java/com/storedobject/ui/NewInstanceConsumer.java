package com.storedobject.ui;

import com.storedobject.core.StoredObject;

/**
 * This consumer will be asked to accept a newly created instance of a {@link StoredObject} by its {@link ObjectEditor}
 * whenever a new instance is created.
 *
 * @author Syam
 */
public interface NewInstanceConsumer {

    <T extends StoredObject> void instanceCreated(T newInstance);
}
