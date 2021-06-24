package com.storedobject.ui;

/**
 * "Object Editor" listener to track changes of the editor.
 *
 * @author Syam
 */
public interface ObjectEditorListener {

    /**
     * Fired when editing started.
     */
    default void editingStarted() {
    }

    /**
     * Fired when editing ended.
     */
    default void editingEnded() {
    }

    /**
     * Fired when editing cancelled.
     */
    default void editingCancelled() {
    }
}