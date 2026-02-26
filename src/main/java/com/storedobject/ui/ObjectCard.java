package com.storedobject.ui;

import com.storedobject.core.StoredObject;

/**
 * Represents a generic card that holds a reference to an object of type T, where T is a subclass
 * of StoredObject. This class extends the functionality of the Card class.
 *
 * @param <T> the type of the object stored in the card, which must extend the StoredObject class
 *
 * @author Syam
 */
public class ObjectCard<T extends StoredObject> extends Card<T> {
}
