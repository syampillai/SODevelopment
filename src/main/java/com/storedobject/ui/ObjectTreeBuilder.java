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

    @SuppressWarnings("unchecked")
    default <T extends StoredObject> ObjectIterator<T> listChildren(T parent) {
        return (ObjectIterator<T>) parent.listLinks(getLinkType(), parent.getClass(), getCondition(), getOrderBy(), isAllowAny());
    }

    @SuppressWarnings("unchecked")
    default <T extends StoredObject> T getParent(T child) {
        return (T) child.getMaster(getLinkType(), child.getClass());
    }

    static ObjectTreeBuilder create(int linkType, boolean any) {
        if(linkType == 0 && !any) {
            return DEFAULT;
        }
        return new ObjectTreeBuilder() {
            @Override
            public int getLinkType() {
                return linkType;
            }

            @Override
            public boolean isAllowAny() {
                return any;
            }
        };
    }

    static ObjectTreeBuilder create(int linkType) {
        return create(linkType, false);
    }

    static ObjectTreeBuilder create(boolean any) {
        return create(0, any);
    }

    static ObjectTreeBuilder create() {
        return DEFAULT;
    }
}