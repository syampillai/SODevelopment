package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectsCached<T extends StoredObject, M> {

    ObjectsCached(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
    }

    void load(ObjectIterator<T> objects) {
    }

    void reload() {
    }

    boolean nullCond(String condition) {
        return false;
    }

    void unload() {
    }

    boolean filter(Predicate<T> filter) {
        return true;
    }

    T getItem(int index) {
        return null;
    }

    int size() {
        return -1;
    }

    void added(T item) {
    }

    void deleted(T item) {
    }

    class Fetcher implements CallbackDataProvider.FetchCallback<M, String> {

        @Override
        public Stream<M> fetch(Query<M, String> query) {
            return null;
        }
    }

    class Counter implements CallbackDataProvider.CountCallback<M, String> {

        @Override
        public int count(Query<M, String> query) {
            return 0;
        }
    }
}
