package com.storedobject.ui;

import com.vaadin.flow.component.Component;

public interface SupportsConcurrentClick {

    ConcurrentClick getConcurrentClick();

    default void registerClick(Component c, Runnable runnable) {
        getConcurrentClick().registerClick(c, runnable);
    }
}
