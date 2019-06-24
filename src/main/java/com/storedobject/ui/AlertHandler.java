package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public interface AlertHandler {

    default void handleAlert(Id id) {
        handleAlert(StoredObject.get(id));
    }

    default void handleAlert(@SuppressWarnings("unused") StoredObject so) {
    }

    default String getAlertIcon() {
        return "vaadin:cog_o";
    }
}