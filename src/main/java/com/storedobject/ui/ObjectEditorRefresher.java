package com.storedobject.ui;

import com.storedobject.core.StoredObject;

/**
 * An implementation of ObjectChangedListener that refreshes the editor
 * whenever changes are saved.
 *
 * @param <T> Type of object.
 *
 * @author Syam
 */
public class ObjectEditorRefresher<T extends StoredObject> implements ObjectChangedListener<T> {

    private final ObjectEditor<?> editor;

    /**
     * Constructor.
     * @param editor Editor to refresh whenever object changes are committed.
     */
    public ObjectEditorRefresher(ObjectEditor<?> editor) {
        this.editor = editor;
    }

    /**
     * Refreshes the editor by invoking editor's reload() method whenever object is saved.
     * @param object Object being committed.
     */
    @Override
    public void saved(T object) {
        editor.reload();
    }
}