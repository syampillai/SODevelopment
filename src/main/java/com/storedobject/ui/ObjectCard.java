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
public class ObjectCard<T extends StoredObject> extends Card {

    private T object;

    /**
     * Default constructor for the ObjectCard class.
     * Initializes an instance of ObjectCard without setting any specific object.
     */
    public ObjectCard() {
    }

    /**
     * Retrieves the object of type T stored in this card.
     *
     * @return the object of type T stored in this card
     */
    public T getObject() {
        return object;
    }

    /**
     * Sets the object of type T for this card. Display attributes of the card should be set based on the object's property values.
     *
     * @param object the object to be associated with this card must be of type T
     *               where T is a subclass of StoredObject
     */
    public void setObject(T object) {
        this.object = object;
    }
}
