package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;

import java.util.List;
import java.util.function.Predicate;

public interface AbstractObjectForestSupplier<T extends StoredObject> extends HierarchicalDataProvider<Object, String>, AbstractObjectDataProvider<T, Object> {
    List<T> listRoots();
}
