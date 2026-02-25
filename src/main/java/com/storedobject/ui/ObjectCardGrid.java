package com.storedobject.ui;

import com.storedobject.core.StoredObject;

public class ObjectCardGrid<T extends StoredObject> extends CardGrid {

    public void cardsLoaded() {
    }

    public void cardLoaded(T object) {
        cardsLoaded();
    }
}
