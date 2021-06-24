package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.AbstractObjectForest;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.Collections;
import java.util.List;

public interface AbstractObjectForestSupplier<T extends StoredObject, F> extends
        HierarchicalDataProvider<Object, F>, AbstractObjectDataProvider<T, Object, F> {

    interface ListLinks {
        ObjectIterator<? extends StoredObject> list(StoredObjectUtility.Link<?> link, StoredObject master);
    }

    List<T> listRoots();

    Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener);

    AbstractObjectForest.Customizer getCustomizer();

    void setListLinks(ListLinks listLinks);

    static <O> List<O> subList(List<O> list, Query<?, ?> query) {
        if(query == null) {
            return list;
        }
        if(query.getOffset() >= list.size()) {
            return Collections.emptyList();
        }
        return list.subList(query.getOffset(), Math.min(query.getOffset() + query.getLimit(), list.size()));
    }

    static int subListSize(List<?> list, Query<?, ?> query) {
        if(query == null) {
            return list.size();
        }
        if(query.getOffset() >= list.size()) {
            return 0;
        }
        return Math.min(query.getOffset() + query.getLimit(), list.size()) - query.getOffset();
    }
}