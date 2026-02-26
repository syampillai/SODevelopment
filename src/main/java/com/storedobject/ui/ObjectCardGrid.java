package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import java.util.Collections;
import java.util.List;

/**
 * ObjectCardGrid is a specialized {@code CardGrid} designed to work with objects
 * of type {@code StoredObject}. It provides additional methods to handle events
 * related to loading cards and facilitates retrieving a list of stored objects
 * from the associated {@code CardDashboard}.
 *
 * @param <T> the type of objects that extend {@code StoredObject}, which will
 *            be managed by this grid
 * @author Syam
 */
public class ObjectCardGrid<T extends StoredObject> extends CardGrid<T> {

    /**
     * This method is called when all cards in the grid have been successfully loaded.
     * It is intended to handle any post-loading logic or events that need to occur
     * after the cards have been fully initialized or populated.
     */
    public void cardsLoaded() {
    }

    /**
     * Marks a single card as loaded and triggers processing for when all cards are loaded.
     *
     * @param object the loaded card object of type {@code T}
     */
    public void cardLoaded(T object) {
        cardsLoaded();
    }

    /**
     * Retrieves the list of objects managed by the associated {@code ObjectCardDashboard}.
     * If the dashboard associated with this grid is an instance of {@code ObjectCardDashboard},
     * the method returns the list of objects. Otherwise, it returns an empty list.
     *
     * @return a {@code List<T>} containing the objects from the associated
     *         {@code ObjectCardDashboard}, or an empty list if the dashboard is
     *         not an instance of {@code ObjectCardDashboard} or no dashboard is set.
     */
    public List<T> getList() {
        CardDashboard<T> dashboard = getDashboard();
        if(dashboard instanceof ObjectCardDashboard<?> d) {
            //noinspection unchecked
            return ((ObjectCardDashboard<T>) d).getList();
        }
        return Collections.emptyList();
    }
}
