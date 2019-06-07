package com.storedobject.ui;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;

public interface ObjectTreeBuilder {

    ObjectTreeBuilder DEFAULT = new ObjectTreeBuilder() {
    };

    default int getLinkType() {
        return 0;
    }

    default boolean isAllowAny() {
        return false;
    }

    default String getCondition() {
        return null;
    }

    default String getOrderBy() {
        return null;
    }

    default <T extends StoredObject> ObjectIterator<T> listChildren(T parent) {
        return null;
    }

    default <T extends StoredObject> T getParent(T child) {
        return null;
    }

    static ObjectTreeBuilder create(int linkType, boolean any) {
        return null;
    }

    static ObjectTreeBuilder create(int linkType) {
        return null;
    }

    static ObjectTreeBuilder create(boolean any) {
        return null;
    }

    static ObjectTreeBuilder create() {
        return DEFAULT;
    }
}