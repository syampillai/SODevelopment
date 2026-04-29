package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;

public class ObjectCardTemplate<T extends StoredObject> extends TemplateComponent implements ObjectSetter<T>, SupportsConcurrentClick {

    private final ConcurrentClick concurrentClick = new ConcurrentClick();
    private T object;

    /**
     * Constructor.
     *
     * @param templateCode Template containing HTML and CSS (within style tag).
     */
    public ObjectCardTemplate(String templateCode) {
        super(templateCode);
    }

    /**
     * Sets the object of type T for this instance.
     * <p>Note: This is set automatically by the card grid.</p>
     *
     * @param object the object to be stored may be null.
     */
    @Override
    public void setObject(T object) {
        this.object = object;
    }

    /**
     * Retrieves the stored object of type T.
     *
     * @return the object of type T contained in this instance, or null if no object is set.
     */
    public final T getObject() {
        return object;
    }

    @Override
    public final ConcurrentClick getConcurrentClick() {
        return concurrentClick;
    }
}
