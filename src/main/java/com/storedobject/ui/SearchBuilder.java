package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;

import java.util.function.Consumer;

@FunctionalInterface
public interface SearchBuilder<T extends StoredObject> {

    ObjectSearchBuilder<T> createSearchBuilder(Class<T> objectClass, StringList searchColumns,
                                                       Consumer<ObjectSearchBuilder<T>> changeConsumer);
}
