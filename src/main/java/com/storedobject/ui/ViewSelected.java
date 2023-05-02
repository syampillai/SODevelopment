package com.storedobject.ui;

/**
 * This can be implemented by a {@link com.storedobject.vaadin.View} so that its {@link #viewSelected()} method
 * is invoked whenever it is selected to make it appear on the screen.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ViewSelected {
    void viewSelected();
}
