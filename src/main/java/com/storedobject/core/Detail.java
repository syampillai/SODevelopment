package com.storedobject.core;

public interface Detail {

    Id getId();

    default Id getUniqueId() {
        return getId();
    }

    default void copyValuesFrom(Detail detail) {
    }

    boolean isDetailOf(Class<? extends StoredObject> masterClass);
}
