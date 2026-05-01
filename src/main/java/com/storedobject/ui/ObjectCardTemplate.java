package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.LogicParser;
import com.vaadin.flow.component.Component;

import java.util.function.Supplier;

/**
 * A template-based component for displaying and managing object cards in a UI.
 * This class provides functionality to manage a card's object, set menu anchors,
 * and handle concurrent click events. It extends {@code TemplateComponent} for
 * HTML and CSS-based templated designs and implements {@code ObjectSetter},
 * {@code SupportsConcurrentClick}, and {@code CardContent} for integration with object data,
 * thread-safe click handling, and card management, respectively.
 *
 * @param <T> The type of {@code StoredObject} this template can manage.
 *
 * @author Syam
 */
public class ObjectCardTemplate<T extends StoredObject> extends ObjectTemplate<T>
        implements SupportsConcurrentClick, CardContent<T> {

    private final ConcurrentClick concurrentClick = new ConcurrentClick();
    private Component menuAnchor;
    private ObjectCard<T> card;

    /**
     * Constructor.
     *
     * @param objectClass The class type of the objects that will be represented and managed.
     * @param templateCode Template containing HTML and CSS (within style tag).
     */
    public ObjectCardTemplate(Class<T> objectClass, String templateCode) {
        super(objectClass, templateCode);
    }

    /**
     * Constructor.
     *
     * @param objectClass The class type of the objects that will be represented and managed.
     */
    public ObjectCardTemplate(Class<T> objectClass) {
        super(objectClass, ObjectDashboard.tc(objectClass, "CardTemplate"));
    }

    /**
     * Creates a supplier for an {@code ObjectCardTemplate} instance of the specified type.
     * This method determines the template class dynamically based on the provided object type
     * and generates the appropriate {@code ObjectCardTemplate} with an associated template code or
     * error message if the instantiation fails.
     *
     * @param objectClass The class object representing the type that the {@code ObjectCardTemplate}
     *                    will manage and represent.
     * @return A supplier that provides instances of {@code ObjectCardTemplate} for the given type.
     *         The supplier may return an instance containing an error message in case of an invalid
     *         template class or instantiation error.
     * @param <O> The type of the objects managed by the {@code ObjectCardTemplate}.
     */
    public static <O extends StoredObject> Supplier<ObjectCardTemplate<O>> createSupplier(Class<O> objectClass) {
        Class<?> tc = LogicParser.createLogicClass(objectClass, "CardTemplate");
        if (tc != null && !ObjectCardTemplate.class.isAssignableFrom(tc)) {
            return () -> new ObjectCardTemplate<>(objectClass, "<span>Invalid template class: " + tc.getName() + "</span>");
        }
        if(tc != null && tc != ObjectCardTemplate.class) {
            try {
                tc.getConstructor().newInstance();
            } catch (Throwable e) {
                Application.get().log(e);
                String error = "<span>Error creating template: " + tc.getName() + "</span>";
                return () -> new ObjectCardTemplate<>(objectClass, error);
            }
            return () -> {
                try {
                    //noinspection unchecked
                    return  (ObjectCardTemplate<O>) tc.getConstructor().newInstance();
                } catch (Exception e) {
                    return null;
                }
            };
        }
        String templateCode = ObjectDashboard.tc(objectClass, "CardTemplate");
        return () -> new ObjectCardTemplate<>(objectClass, templateCode);
    }

    @Override
    public final ConcurrentClick getConcurrentClick() {
        return concurrentClick;
    }

    /**
     * Sets the anchor component for the menu associated with the ObjectCard.
     * The menu will use this component as its reference point for positioning.
     * If the provided menuAnchor is null, the current ObjectCard instance will
     * be used as the default anchor.
     *
     * @param menuAnchor the component to be used as the menu anchor. If null,
     *                   the ObjectCard itself will be set as the anchor.
     */
    public final void setMenuAnchor(Component menuAnchor) {
        this.menuAnchor = menuAnchor == null ? this : menuAnchor;
        if(card != null) {
            card.setMenuAnchor(menuAnchor);
        }
    }

    @Override
    public final void setCard(Card<T> card) {
        this.card = (ObjectCard<T>) card;
        if(menuAnchor != null) {
            this.card.setMenuAnchor(menuAnchor);
        }
    }
}
