package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.Component;

/**
 * Represents a generic card that holds a reference to an object of type T, where T is a subclass
 * of StoredObject. This class extends the functionality of the Card class.
 *
 * @param <T> the type of the object stored in the card, which must extend the StoredObject class
 *
 * @author Syam
 */
public class ObjectCard<T extends StoredObject> extends Card<T> {

    /**
     * Constructs an instance of ObjectCard with no initial configuration or root component.
     * This constructor initializes the ObjectCard using the default behavior of its parent class.
     */
    public ObjectCard() {
    }

    /**
     * Constructs an ObjectCard with the specified root component.
     * The root component represents the content to be displayed within the card.
     * This constructor initializes the ObjectCard by invoking the parent Card class's constructor,
     * applying default styles and layout settings.
     *
     * @param root the content component to be displayed within the ObjectCard.
     *             It is passed to the parent Card class as its content.
     */
    public ObjectCard(Component root) {
        super(root);
    }

    /**
     * Refreshes the content of the card.
     */
    public void refresh() {
        setObject(getObject());
    }

    /**
     * Reloads the object and refreshes the card with updated data.
     */
    public void reload() {
        T object = getObject();
        if(object != null) {
            object.reload();
            setObject(object);
        }
    }
}
