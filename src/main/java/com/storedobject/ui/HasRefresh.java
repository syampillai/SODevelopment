package com.storedobject.ui;

/**
 * Interface to denote that a UI class has a {@link #refresh()} method to refresh the screen.
 * Typically, {@link com.storedobject.vaadin.View}s added to {@link PresentationRunner} can implement this.
 *
 * @author Syam
 */
@FunctionalInterface
public interface HasRefresh {
    void refresh();
}
