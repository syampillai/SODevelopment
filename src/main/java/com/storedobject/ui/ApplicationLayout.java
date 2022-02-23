package com.storedobject.ui;

import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasComponents;

public interface ApplicationLayout extends com.storedobject.vaadin.ApplicationLayout {

    default void initialized() {
    }

    default boolean isMenuOpened() {
        return false;
    }

    default void viewDetached(View view) {
    }

    default HasComponents getProgressBarHolder() {
        return null;
    }

    @Override
    default void drawMenu(com.storedobject.vaadin.Application application) {
        ((Application) application).drawMenu(application);
    }

    default boolean isMenuVisible() {
        return true;
    }
}
