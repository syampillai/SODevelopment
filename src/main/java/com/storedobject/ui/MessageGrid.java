package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.Component;

import java.util.List;

/**
 * A grid to be shown in a window with an "Ok" button and it will be closed when the button is pressed.
 *
 * @param <T> Type of object in the grid.
 * @author Syam
 */
public class MessageGrid<T extends StoredObject> extends ActionGrid<T> {

    public MessageGrid(Class<T> objectClass, List<T> items) {
        super(objectClass, items);
    }

    public MessageGrid(Class<T> objectClass, List<T> items, Iterable<String> columns) {
        super(objectClass, items, columns);
    }

    public MessageGrid(Class<T> objectClass, List<T> items, String message) {
        super(objectClass, items, message);
    }

    public MessageGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, String message) {
        super(objectClass, items, columns, message);
    }

    public MessageGrid(Class<T> objectClass, List<T> items, Component message) {
        super(objectClass, items, message);
    }

    public MessageGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, Component message) {
        super(objectClass, items, columns, message);
    }
}
