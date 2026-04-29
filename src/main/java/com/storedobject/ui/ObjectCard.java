package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.LogicParser;
import com.vaadin.flow.component.Component;

import java.util.function.Supplier;

/**
 * Represents a generic card that holds a reference to an object of type T, where T is a subclass
 * of StoredObject. This class extends the functionality of the Card class.
 *
 * @param <T> the type of the object stored in the card, which must extend the StoredObject class
 *
 * @author Syam
 */
public class ObjectCard<T extends StoredObject> extends Card<T> {

    private ObjectSetter<T> objectConsumer;
    ObjectCardDashboard<T> dashboard;
    private boolean showMenu = true;

    /**
     * Constructs an instance of ObjectCard with no initial configuration or root component.
     * This constructor initializes the ObjectCard using the default behavior of its parent class.
     */
    public ObjectCard() {
        this(null);
    }

    /**
     * Constructs an ObjectCard with the specified root component.
     * The root component represents the content to be displayed within the card.
     * This constructor initializes the ObjectCard by invoking the parent Card class's constructor,
     * applying default styles and layout settings.
     *
     * @param root the content component to be displayed within the ObjectCard.
     *             It is passed to the parent Card class as its content.
     */
    public ObjectCard(Component root) {
        super(root);
        if(root instanceof ObjectSetter<?>) {
            //noinspection unchecked
            objectConsumer = (ObjectSetter<T>) root;
        }
        registerClick(this, this::showMenu);
    }

    /**
     * Refreshes the content of the card.
     */
    public void refresh() {
        setObject(getObject());
    }

    /**
     * Reloads the object and refreshes the card with updated data.
     */
    public void reload() {
        T object = getObject();
        if(object != null) {
            object.reload();
            setObject(object);
        }
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        if(objectConsumer != null) {
            objectConsumer.accept(object);
        }
    }

    static <O extends StoredObject> Supplier<ObjectCard<O>> creator(Class<O> objectClass) {
        Class<?> c = LogicParser.createLogicClass(objectClass, "Card");
        if(c != null && ObjectCard.class.isAssignableFrom(c) && c != ObjectCard.class) {
            return () -> {
                try {
                    //noinspection unchecked
                    return (ObjectCard<O>) c.getConstructor().newInstance();
                } catch (Exception e) {
                    return null;
                }
            };
        }
        String t = objectClass.getName() + "CardTemplate";
        TextContent textContent = TextContent.get(t);
        if(textContent != null && textContent.getName().equals(t)) {
            t = textContent.getContent();
            Class<?> tc = LogicParser.createLogicClass(objectClass, "CardTemplate");
            if(tc == null || !ObjectCardTemplate.class.isAssignableFrom(tc)) {
                tc = ObjectCardTemplate.class;
            }
            try {
                tc.getConstructor(String.class);
            } catch (Exception ignored) {
                tc = ObjectCardTemplate.class;
            }
            Class<?> finalTc = tc;
            String finalT = t;
            return () -> {
                try {
                    @SuppressWarnings("unchecked") var template = (ObjectCardTemplate<O>) finalTc
                            .getConstructor(String.class).newInstance(finalT);
                    return new ObjectCard<>(template);
                } catch (Exception e) {
                    return null;
                }
            };
        }
        return null;
    }

    /**
     * Sets whether the object-menu should be displayed for the ObjectCard.
     *
     * @param showMenu a boolean indicating whether to show the menu.
     *                 If true, the menu will be displayed; if false, it will be hidden.
     */
    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    /**
     * Displays the menu associated with the Object currently set on the card.
     */
    public final void showMenu() {
        if(showMenu) {
            T object = getObject();
            if (object == null) return;
            Application.message("Object: " + object.toDisplay());
        }
    }
}
