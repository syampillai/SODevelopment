package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;

import java.util.List;

public interface AbstractObjectForestSupplier<T extends StoredObject> extends HierarchicalDataProvider<Object, String>, AbstractObjectDataProvider<T, Object> {

    interface ListLinks {
        ObjectIterator<? extends StoredObject> list(StoredObjectUtility.Link<?> link, StoredObject master);
    }

    List<T> listRoots();

    void setListLinks(ListLinks listLinks);
}
