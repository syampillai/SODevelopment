package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.BiConsumer;

/**
 * A button component designed to work with objects of type {@link StoredObject}.
 * This button is used to trigger actions involving an {@link ObjectEditor} and an object of type {@code T}.
 * This can be used to define additional actions while viewing an object in the editor.
 *
 * @param <T> The type of the object that extends {@link StoredObject}.
 *
 * @author Syam
 */
public class ObjectViewerButton<T extends StoredObject> extends Button {

    final BiConsumer<ObjectEditor<T>, T> consumer;

    /**
     * Creates a button that performs a specific action involving an {@link ObjectEditor}
     * and an object of type {@code T}.
     *
     * @param text The text to display on the button.
     * @param consumer A {@link BiConsumer} that defines the action to be executed, taking the {@link ObjectEditor}
     *                 and the object of type {@code T} as arguments.
     */
    public ObjectViewerButton(String text, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(text, null);
        this.consumer = consumer;
    }

    /**
     * Creates a button with a specific icon and an action to be executed using an {@link ObjectEditor}
     * and an object of type {@code T}.
     *
     * @param icon The component used as an icon for the button.
     * @param consumer The action to be executed, defined as a {@link BiConsumer} that accepts an
     *                 {@link ObjectEditor} and an object of type {@code T}.
     */
    public ObjectViewerButton(Component icon, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(icon, null);
        this.consumer = consumer;
    }

    /**
     * Constructs an ObjectViewerButton with a text label, an icon specified by a resource path,
     * and a consumer action to be performed when the button is triggered.
     *
     * @param text    The text to display on the button.
     * @param icon    The resource path of the icon to display on the button.
     * @param consumer The action to be performed, defined as a {@link BiConsumer} that accepts
     *                 an {@link ObjectEditor} of type {@code T} and an object of type {@code T}.
     */
    public ObjectViewerButton(String text, String icon, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(text, icon, null);
        this.consumer = consumer;
    }

    /**
     * Creates a new ObjectViewerButton with the specified text, icon, and action.
     *
     * @param text the text to be displayed on the button
     * @param icon the icon to be displayed on the button
     * @param consumer the action to be executed when the button is used, which takes an {@link ObjectEditor}
     *                 instance and an object of type {@code T} as parameters
     */
    public ObjectViewerButton(String text, Component icon, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(text, icon, null);
        this.consumer = consumer;
    }

    /**
     * Creates a button with an icon, designed to interact with {@link ObjectEditor}
     * for objects of type {@code T}.
     *
     * @param icon The {@link VaadinIcon} to display on the button.
     * @param consumer A {@link BiConsumer} that defines the action to be performed
     *                 with the {@link ObjectEditor} and the object of type {@code T}.
     */
    public ObjectViewerButton(VaadinIcon icon, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(icon, null);
        this.consumer = consumer;
    }

    /**
     * Creates a new ObjectViewerButton with specified text, icon, and consumer.
     * This button is designed to work with an {@link ObjectEditor} and an object of type {@code T},
     * allowing for additional actions to be defined in the context of viewing objects.
     *
     * @param text The text label to display on the button.
     * @param icon The {@link VaadinIcon} to display on the button.
     * @param consumer A {@link BiConsumer} defining an action to be executed with the {@link ObjectEditor}
     *                 and an object of type {@code T} when the button is triggered.
     */
    public ObjectViewerButton(String text, VaadinIcon icon, BiConsumer<ObjectEditor<T>, T> consumer) {
        super(text, icon, null);
        this.consumer = consumer;
    }
}
