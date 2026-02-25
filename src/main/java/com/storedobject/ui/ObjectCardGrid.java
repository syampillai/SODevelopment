package com.storedobject.ui;

import com.storedobject.core.StoredObject;

import java.util.Collections;
import java.util.List;

public class ObjectCardGrid<T extends StoredObject> extends CardGrid {

    public void cardsLoaded() {
    }

    public void cardLoaded(T object) {
        cardsLoaded();
    }

    public List<T> getList() {
        CardDashboard dashboard = getDashboard();
        if(dashboard instanceof ObjectCardDashboard<?> d) {
            //noinspection unchecked
            return ((ObjectCardDashboard<T>) d).getList();
        }
        return Collections.emptyList();
    }
}
