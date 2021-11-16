package com.storedobject.ui;

import com.storedobject.core.StoredObject;

/**
 * Provider of {@link ObjectEditor}.
 *
 * @author Syam
 */
public interface ObjectEditorProvider {

    /**
     * Create an editor for the given class.
     *
     * @param objectClass Class of the object.
     * @param <OBJECT_TYPE> Type of the object.
     * @return An instance of {@link ObjectEditor}.
     */
    default <OBJECT_TYPE extends StoredObject> ObjectEditor<OBJECT_TYPE> createEditor(Class<OBJECT_TYPE> objectClass) {
        return ObjectEditor.create(objectClass);
    }
}
