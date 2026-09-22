package com.storedobject.ui.ai;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.View;

/**
 * Represents a specialized implementation of the {@link AcceptData} class, designed
 * for accepting and processing objects of type {@code O}, where {@code O} extends
 * {@link StoredObject}. This class leverages an associated {@link ObjectField} to
 * facilitate object selection and management within forms.
 *
 * @param <O> The type of object being accepted, which must extend {@link StoredObject}.
 *
 * @author Syam
 */
public class AcceptObject<O extends StoredObject> extends AcceptData {

    private final ObjectField<O> objectField;
    private O object;

    /**
     * Creates an instance of AcceptObject, which is a specialized implementation
     * for accepting and processing objects of type {@code O}. It initializes an
     * {@link ObjectField} to handle the object selection process and ensures the field is required.
     *
     * @param knowledge The {@link Knowledge} object that provides contextual information
     *                  and capabilities for the acceptance operation.
     * @param objectClass The class type of the object being accepted. This must be a class
     *                    that extends {@link StoredObject}.
     * @param purpose A descriptive string indicating the purpose of the object being accepted.
     *                This is typically displayed to the user.
     */
    public AcceptObject(Knowledge knowledge, Class<O> objectClass, String purpose) {
        super(knowledge, "Select");
        this.objectField = new ObjectField<>(purpose, objectClass);
        addField(objectField);
        setRequired(objectField);
    }

    /**
     * Retrieves the {@link ObjectField} associated with this instance. The {@code ObjectField}
     * is used for selecting and managing objects of type {@code O}.
     *
     * @return The {@code ObjectField} instance associated with managing objects of type {@code O}.
     */
    public ObjectField<O> getObjectField() {
        return objectField;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        object = null;
        map.remove("object");
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        object = objectField.getObject();
        map.put("object", object);
        return true;
    }

    /**
     * Retrieves the object of type {@code O} currently associated with this instance.
     * This method ensures that the internal data structure is up to date by invoking
     * {@code getMap()} before returning the object. If the object has been processed
     * and set, it will return the corresponding instance; otherwise, it may return null.
     *
     * @return The object of type {@code O} managed by this instance, or null if the
     *         object has not been set or processed yet.
     */
    public O getObject() {
        getMap();
        return object;
    }
}
