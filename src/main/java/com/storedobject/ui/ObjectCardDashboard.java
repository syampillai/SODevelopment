package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.ui.util.ObjectListLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.html.Div;

import java.util.function.Function;

public class ObjectCardDashboard<T extends StoredObject> extends CardDashboard implements ObjectLoader<T> {

    private final ObjectListLoader<T> loader;
    private Function<T, ObjectCard<T>> cardCreator = null;

    public ObjectCardDashboard(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectCardDashboard(Class<T> objectClass, boolean allowAny) {
        super(Application.getLogicCaption(() -> StringUtility.makeLabel(objectClass)), grid(objectClass));
        loader = new ObjectListLoader<>(objectClass, this::newCard, allowAny);
    }

    public ObjectCardDashboard(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className(className)), className.toLowerCase().endsWith("/any"));
        Class<?> c = LogicParser.createLogicClass(loader.getObjectClass(), "Card");
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

    public void cardsLoaded() {
    }

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


    public void setCardCreator(Function<T, ObjectCard<T>> cardCreator) {
        this.cardCreator = cardCreator;
    }

    private void newCard(T o) {
        if(o == null) {
            cardsLoadedInt();
            return;
        }
        ObjectCard<T> card = cardCreator == null ? createCard(o) : cardCreator.apply(o);
        if(card == null) {
            card = createCard(o);
        }
        card.setObject(o);
        getGrid().add(card);
    }

    protected ObjectCard<T> createCard(T object) {
        ObjectCard<T> c = new ObjectCard<>();
        c.add(new Div(object.toDisplay()));
        return c;
    }

    @Override
    public Class<T> getObjectClass() {
        return loader.getObjectClass();
    }
    
    @Override
    public void clear() {
        getGrid().removeAll();
        loader.getList().clear();
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

    public ObjectCardGrid<T> getGrid() {
        //noinspection unchecked
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
