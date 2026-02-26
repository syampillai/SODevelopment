package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.ui.util.ObjectListLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.html.Div;

import java.util.List;
import java.util.function.Function;

/**
 * A specialized dashboard for managing and displaying cards representing objects of a specific type.
 * This class extends the functionality of {@link CardDashboard} and integrates with an {@link ObjectLoader}
 * for loading and managing lists of objects of type {@code T}.
 *
 * @param <T> The type of objects managed by this dashboard. It must extend {@link StoredObject}.
 *
 * @author Syam
 */
public class ObjectCardDashboard<T extends StoredObject> extends CardDashboard<T> implements ObjectLoader<T> {

    private final ObjectListLoader<T> loader;
    private Function<T, ObjectCard<T>> cardCreator = null;

    /**
     * Constructs an ObjectCardDashboard instance to manage and display object cards.
     *
     * @param objectClass The class type of the objects that will be represented and managed
     *                    on the dashboard.
     */
    public ObjectCardDashboard(Class<T> objectClass) {
        this(objectClass, false);
    }

    /**
     * Constructs a new {@code ObjectCardDashboard} instance for managing object-specific cards
     * within the dashboard. This constructor initializes the dashboard based on the provided
     * object class by dynamically creating card instances and managing their lifecycle using
     * an associated loader.
     *
     * @param objectClass The class of the objects this dashboard will manage. Used for dynamically
     *                    resolving the object type and creating corresponding cards.
     * @param allowAny    Determines if the dashboard should allow managing any inherited object of the
     *                    specified type.
     */
    public ObjectCardDashboard(Class<T> objectClass, boolean allowAny) {
        super(Application.getLogicCaption(() -> StringUtility.makeLabel(objectClass)), grid(objectClass));
        Class<?> c = LogicParser.createLogicClass(objectClass, "Card");
        if(c != null && ObjectCard.class.isAssignableFrom(c) && c != ObjectCard.class) {
            cardCreator = o -> {
                try {
                    //noinspection unchecked
                    return (ObjectCard<T>) c.getConstructor().newInstance();
                } catch (Exception e) {
                    return null;
                }
            };
        }
        loader = new ObjectListLoader<>(objectClass, this::newCard, this::clearInt, this::cardsLoadedInt, allowAny);
        addCardSelectedListener(card -> message("Selected: " + card.isSelected() + ": " + card.getObject().toDisplay()));
    }

    /**
     * Constructs an instance of ObjectCardDashboard using the class name provided.
     * The constructor initializes the dashboard by determining the class type from the given name,
     * sets up the object loader, and loads the necessary data.
     *
     * @param className The name of the class to be used for creating the object cards.
     *                  The class name may end with "/any" to allow additional flexibility for specifying the inherited
     *                  object type.
     * @throws Exception If an error occurs during class loading or initialization.
     */
    public ObjectCardDashboard(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className(className)), className.toLowerCase().endsWith("/any"));
        load();
    }

    private static <O extends StoredObject> ObjectCardGrid<O> grid(Class<O> oClass) {
        Class<?> c = LogicParser.createLogicClass(oClass, "CardGrid");
        try {
            if(c == null) {
                throw new Exception();
            }
            //noinspection unchecked
            return c.getName().equals(ObjectCardGrid.class.getName()) ? new ObjectCardGrid<>()
                    : (ObjectCardGrid<O>) c.getConstructor().newInstance();
        } catch (Exception e) {
            throw new SORuntimeException("No card grid found for: " + oClass.getName(), e);
        }
    }

    private static String className(String className) {
        int p = className.lastIndexOf('/');
        return p < 0 ? className : className.substring(0, p);
    }

    /**
     * Indicates that all the cards have been successfully loaded into the dashboard.
     * This method is typically called once the card-loading process is complete,
     * ensuring that the dashboard and its associated components are properly updated
     * to reflect the current state of the loaded data.
     * It can be used to perform any post-loading operations required for
     * maintaining the integrity of the dashboard's structure and visual presentation.
     */
    public void cardsLoaded() {
    }

    /**
     * Notifies the dashboard that a single object card has been successfully loaded.
     * This method handles actions related to the loading of an individual object card
     * and may trigger other lifecycle updates in the dashboard.
     *
     * @param object The object whose card has been loaded and is ready to be added
     *               or displayed in the dashboard.
     */
    public void cardLoaded(T object) {
        cardsLoaded();
    }

    private void cardsLoadedInt() {
        cardsLoaded();
        getGrid().cardsLoaded();
    }

    private void cardLoadedInt(T object) {
        cardLoaded(object);
        getGrid().cardLoaded(object);
    }

    /**
     * Sets the card creator function for dynamically generating {@code ObjectCard} instances
     * based on objects of type {@code T}. The dashboard uses the provided function internally
     * to create and manage object-specific cards.
     *
     * @param cardCreator A {@code Function} that takes an object of type {@code T} as input and returns
     *                    an {@code ObjectCard<T>} instance. This function facilitates the custom generation
     *                    of cards for objects managed in the dashboard.
     */
    public void setCardCreator(Function<T, ObjectCard<T>> cardCreator) {
        this.cardCreator = cardCreator;
    }

    private void newCard(T o) {
        ObjectCard<T> card = cardCreator == null ? createCard(o) : cardCreator.apply(o);
        if(card == null) {
            card = createCard(o);
        }
        card.setObject(o);
        getGrid().add(card);
    }

    /**
     * Creates an {@code ObjectCard} instance for the specified object, adds a visual representation
     * of the object to the card, and returns the created card.
     *
     * @param object the object of type {@code T} for which the card is to be created. The object is
     *               used to generate a displayable representation within the card.
     * @return an instance of {@code ObjectCard<T>} containing the visual representation of the provided object.
     */
    protected ObjectCard<T> createCard(T object) {
        ObjectCard<T> c = new ObjectCard<>();
        c.add(new Div(object.toDisplay()));
        return c;
    }

    @Override
    public Class<T> getObjectClass() {
        return loader.getObjectClass();
    }

    /**
     * Retrieves the list of objects managed by the loader.
     *
     * @return a {@code List<T>} containing the objects managed by this dashboard.
     */
    public List<T> getList() {
        return loader.getList();
    }
    
    @Override
    public void clear() {
        loader.clear();
    }

    private void clearInt() {
        getGrid().removeAll();
        cardsLoadedInt();
    }

    /**
     * Refresh the dashboard by reloading all objects and updating cards.
     */
    @Override
    public void reload() {
        loader.getList().refresh();
        for(T object: loader.getList()) {
            updateCard(getGrid(), object);
        }
        cardsLoadedInt();
    }

    /**
     * Refreshes the state of the specified object in the dashboard.
     * The associated object is reloaded in the internal list, and its corresponding
     * card in the grid is updated.
     *
     * @param object The object to be refreshed in the dashboard.
     */
    public void reload(T object) {
        loader.getList().refresh(object);
        updateCard(getGrid(), object);
        cardLoadedInt(object);
    }

    /**
     * Refresh the dashboard
     */
    public void refresh() {
        for (T o : loader.getList()) {
            updateCard(getGrid(), o);
        }
        cardsLoadedInt();
    }

    /**
     * Refreshes the state of the specified object in the dashboard.
     * If the object exists in the internal list, its corresponding card is updated in the grid.
     *
     * @param object The object to be refreshed in the dashboard.
     */
    public void refresh(T object) {
        if(loader.getList().contains(object)) {
            updateCard(getGrid(), object);
            cardLoadedInt(object);
        }
    }

    /**
     * Retrieves the {@code ObjectCardGrid} associated with this dashboard.
     * The {@code ObjectCardGrid} is a specialized version of a {@code CardGrid},
     * designed to manage and display cards representing objects of type {@code T}.
     *
     * @return the {@code ObjectCardGrid<T>} instance linked to this dashboard,
     *         allowing interaction and management of object-specific cards.
     */
    public ObjectCardGrid<T> getGrid() {
        return (ObjectCardGrid<T>) super.getGrid();
    }

    private static <O extends StoredObject> boolean updateCard(Component c, O object) {
        switch (c) {
            case null -> {
                return true;
            }
            case ObjectCard<?> oc when oc.getObject().getId().equals(object.getId()) -> {
                //noinspection unchecked
                ((ObjectCard<O>) oc).setObject(object);
                return true;
            }
            case HasOrderedComponents hoc -> {
                return hoc.getChildren().anyMatch(cc -> updateCard(cc, object));
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public final ObjectLoader<T> getDelegatedLoader() {
        return loader;
    }
}
