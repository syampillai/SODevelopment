package com.storedobject.ui;

public interface ObjectEditorListener {

    default void editingStarted() {
    }

    default void editingEnded() {
    }

    default void editingCancelled() {
    }
}