package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
public class ObjectCardTemplate<T extends StoredObject> extends TemplateComponent
        implements ObjectSetter<T>, SupportsConcurrentClick, CardContent<T> {

    private final ConcurrentClick concurrentClick = new ConcurrentClick();
    private T object;
    private Component menuAnchor;
    private ObjectCard<T> card;
    private final List<Consumer<T>> painters = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param templateCode Template containing HTML and CSS (within style tag).
     */
    public ObjectCardTemplate(String templateCode) {
        super(templateCode);
    }

    /**
     * Sets the object of type T for this instance.
     * <p>Note: This is set automatically by the card grid.</p>
     *
     * @param object the object to be stored may be null.
     */
    @Override
    public final void setObject(T object) {
        this.object = object;
        if(!isCreated()) build();
        painters.forEach(p -> p.accept(object));
        paint(object);
    }

    /**
     * This method is called by the card grid to paint the object values on the card. Typically, this
     * method is overridden by subclasses to provide custom painting logic.
     *
     * @param object Object to be painted.
     */
    public void paint(T object) {
    }

    /**
     * Retrieves the stored object of type T.
     *
     * @return the object of type T contained in this instance, or null if no object is set.
     */
    public final T getObject() {
        return object;
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

    @Override
    protected Component createComponentForId(String id, String tag) {
        return switch(tag) {
            case "div", "span" -> createPainter(id, tag);
            default -> super.createComponentForId(id, tag);
        };
    }

    private Component createPainter(String id, String tag) {
        try {
            StoredObjectUtility.MethodList m = StoredObjectUtility.createMethodList(object.getClass(), id);
            m.stringifyTail();
            return switch (tag) {
                case "div" -> {
                    Div div = new Div();
                    painters.add(o -> div.setText(StringUtility.toString(m.invoke(o))));
                    yield div;
                }
                case "span" -> {
                    Span span = new Span();
                    painters.add(o -> span.setText(StringUtility.toString(m.invoke(o))));
                    yield span;
                }
                default -> null;
            };
        } catch (Throwable ignored) {
        }
        return super.createComponentForId(id, tag);
    }
}
