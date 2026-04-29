package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a generic card that holds a reference to an object of type T, where T is a subclass
 * of StoredObject. This class extends the functionality of the Card class.
 *
 * @param <T> the type of the object stored in the card, which must extend the StoredObject class
 *
 * @author Syam
 */
public class ObjectCard<T extends StoredObject> extends Card<T> implements PrintButton.HasPrintButton {

    private ObjectSetter<T> objectConsumer;
    ObjectCardDashboard<T> dashboard;
    private Consumer<Component> printButton;
    private boolean showMenu = true, viewDetailsOnClick = true;
    private Component menuAnchor = this;

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
        setMenuAnchor(root);
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
        String t = objectClass.getName();
        int p = t.lastIndexOf('.');
        String name = t.substring(p + 1);
        t = t.substring(0, p);
        t += ".logic." + name + "CardTemplate";
        TextContent textContent = TextContent.get(t);
        if(textContent != null && textContent.getName().equals(t)) {
            t = textContent.getContent();
            Class<?> tc = LogicParser.createLogicClass(objectClass, "CardTemplate");
            if(tc != null && !ObjectCardTemplate.class.isAssignableFrom(tc)) {
                tc = null;
            }
            Constructor<?> constructor = null;
            if(tc != null) {
                try {
                    constructor = tc.getConstructor(String.class);
                } catch (Exception ignored) {
                }
            }
            String finalT = t;
            Constructor<?> finalConstructor = constructor;
            return () -> {
                try {
                    var template = finalConstructor == null ? new ObjectCardTemplate<>(finalT)
                            : finalConstructor.newInstance(finalT);
                    //noinspection unchecked
                    return new ObjectCard<>((ObjectCardTemplate<O>)template);
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
            if(printButton == null) {
                //noinspection unchecked
                PrintButton<T> b = PrintButton.create((Class<T>) object.getClass(), this, this::getObject);
                if(b == null) {
                    printButton = c -> {};
                } else {
                    printButton = b::execute;
                }
            }
            printButton.accept(menuAnchor);
        }
    }

    /**
     * Sets whether detailed information should be displayed when the ObjectCard is clicked.
     *
     * @param viewDetailsOnClick a boolean indicating whether to enable the display of
     *                            detailed information on click. If true, the detail view
     *                            will be displayed when the card is clicked; if false,
     *                            this behavior is disabled.
     */
    public void setViewDetailsOnClick(boolean viewDetailsOnClick) {
        if(viewDetailsOnClick != this.viewDetailsOnClick) {
            this.viewDetailsOnClick = viewDetailsOnClick;
            printButton = null;
        }
    }

    @Override
    public List<Component> listMorePrintButtons() {
        if(!viewDetailsOnClick) {
            return null;
        }
        return List.of(new Button("View Details", VaadinIcon.EYE, e -> viewObject()));
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
    }

    /**
     * Invokes the dashboard to display the object associated with this ObjectCard.
     * If the dashboard is not null, the object retrieved from {@code getObject()}
     * is passed to the dashboard's {@code viewObject} method for viewing.
     */
    public void viewObject() {
        if(dashboard != null) {
            dashboard.viewObject(getObject());
        }
    }
}
