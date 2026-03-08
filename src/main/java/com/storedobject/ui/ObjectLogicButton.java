package com.storedobject.ui;

import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ClickHandler;
import com.storedobject.vaadin.Icon;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;

/**
 * A special button that can be used to execute a {@link StoredObject}'s instance related logic. The logic can be
 * stored in a {@link com.storedobject.core.PrintLogicDefinition} and the {@link PrintButton} could paint this button
 * in appropriate logic such as {@link ObjectEditor}, {@link ObjectBrowser}, etc..
 *
 * @param <T> Type of object with which this button is associated.
 *
 * @author Syam
 */
public abstract class ObjectLogicButton<T extends StoredObject> extends Button {

    PrintLogicDefinition definition; // This is set for internal use only
    private final Class<T> objectClass;

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     */
    public ObjectLogicButton(Class<T> objectClass) {
        this(objectClass, "-", (String) null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     * @param icon Icon
     */
    public ObjectLogicButton(Class<T> objectClass, Component icon) {
        this(objectClass, null, icon);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     * @param text Text label to display
     * @param icon Name of the icon to use
     */
    public ObjectLogicButton(Class<T> objectClass, String text, String icon) {
        this(objectClass, text, icon == null || icon.isEmpty() ? null : new Icon(icon));
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     * @param text Text label to display
     * @param icon Icon to use
     */
    public ObjectLogicButton(Class<T> objectClass, String text, Component icon) {
        this(objectClass, text, icon, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     * @param icon Icon to use
     */
    public ObjectLogicButton(Class<T> objectClass, VaadinIcon icon) {
        this(objectClass, null, new Icon(icon));
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object with which this button is associated
     * @param text Text label to display
     * @param icon Icon to use
     */
    public ObjectLogicButton(Class<T> objectClass, String text, VaadinIcon icon) {
        this(objectClass, text, new Icon(icon));
    }

    private ObjectLogicButton(Class<T> objectClass, String text, Component icon, @SuppressWarnings("unused") boolean dummy) {
        super(text, icon, null);
        this.objectClass = objectClass;
    }

    /**
     * Get the class of the object with which this button is associated
     *
     * @return Class of the object with which this button is associated
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * This will be invoked when the button is clicked.
     *
     * @param object Object instance.
     * @param source Source of the event.
     */
    public abstract void accept(T object, Object source);

    @Override
    public final Registration addClickHandler(ClickHandler clickHandler) {
        return null;
    }

    @Override
    public final Registration addClickListener(ComponentEventListener<ClickEvent<com.vaadin.flow.component.button.Button>> listener) {
        return null;
    }

    void listem(ComponentEventListener<ClickEvent<com.vaadin.flow.component.button.Button>> listener) {
        super.addClickListener(listener);
    }
}
