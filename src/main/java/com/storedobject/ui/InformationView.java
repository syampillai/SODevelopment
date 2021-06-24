package com.storedobject.ui;

/**
 * A marker interface denoting that a {@link com.storedobject.vaadin.View} is used only for displaying some sort of
 * information on the screen. For example, a report output shown on the screen can be marked with this interface
 * so that it won't block the execution of other logic when running the application on single-logic or
 * abort-when-logic-switched mode.
 *
 * @author Syam
 */
public interface InformationView {
}
