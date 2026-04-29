package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.Component;

public class ObjectCardTemplate<T extends StoredObject> extends TemplateComponent
        implements ObjectSetter<T>, SupportsConcurrentClick, CardContent<T> {

    private final ConcurrentClick concurrentClick = new ConcurrentClick();
    private T object;
    private Component menuAnchor;
    private ObjectCard<T> card;

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
    public void setObject(T object) {
        this.object = object;
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
    public void setMenuAnchor(Component menuAnchor) {
        this.menuAnchor = menuAnchor == null ? this : menuAnchor;
        if(card != null) {
            card.setMenuAnchor(menuAnchor);
        }
    }

    @Override
    public void setCard(Card<T> card) {
        this.card = (ObjectCard<T>) card;
        if(menuAnchor != null) {
            this.card.setMenuAnchor(menuAnchor);
        }
    }
}
