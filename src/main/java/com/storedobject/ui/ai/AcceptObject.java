package com.storedobject.ui.ai;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectField;

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
public class AcceptObject<O extends StoredObject> extends AcceptData<O> {

    private final ObjectField<O> objectField;

    /**
     * Creates an instance of AcceptObject, which is a specialized implementation
     * for accepting and processing objects of type {@code O}. It initializes an
     * {@link ObjectField} to handle the object selection process and ensures the field is required.
     *
     * @param objectClass The class type of the object being accepted. This must be a class
     *                    that extends {@link StoredObject}.
     * @param knowledge The {@link Knowledge} object that provides contextual information
     *                  and capabilities for the acceptance operation.
     * @param purpose A descriptive string indicating the purpose of the object being accepted.
     *                This is typically displayed to the user.
     */
    public AcceptObject(Class<O> objectClass, Knowledge knowledge, String purpose) {
        super(objectClass, knowledge, "Select");
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
    protected boolean process() {
        clearAlerts();
        data = objectField.getObject();
        return true;
    }

    @Override
    public String getDataLabel() {
        return "object";
    }
}
