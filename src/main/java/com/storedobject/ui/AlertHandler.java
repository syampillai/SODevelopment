package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

/**
 * Alert handler. This is used to handle application alerts. There could be a {@link StoredObject} instance
 * associated with an alert and that may be processed when handling the alert.
 *
 * @author Syam
 */
public interface AlertHandler {

    /**
     * Handle the alert.
     *
     * @param id Id of the associated object instance.
     */
    default void handleAlert(Id id) {
        handleAlert(StoredObject.get(id));
    }

    /**
     * Handle the alert.
     *
     * @param so Associated object instance.
     */
    default void handleAlert(@SuppressWarnings("unused") StoredObject so) {
    }

    /**
     * Get the icon name to be used for showing the alert action button. By default, "vaadin:cog_o" is used.
     *
     * @return Alert icon name,
     */
    default String getAlertIcon() {
        return "vaadin:cog_o";
    }

    /**
     * Get the caption name to be used on the alert action button. By default, "Process" is used.
     *
     * @return Alert button caption.
     */
    default String getAlertCaption() {
        return "Process";
    }
}