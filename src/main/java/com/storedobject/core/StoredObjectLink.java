package com.storedobject.core;

import java.util.stream.Stream;

public interface StoredObjectLink<T extends StoredObject> extends EditableList<T> {

    StoredObject getMaster();

    default int getType() {
        return 0;
    }

    String getName();

    default boolean isAllowAny() {
        return false;
    }

    default boolean isDetail(T object) {
        return false;
    }

    default void attach() {
    }

    default void detach() {
    }

    default void checkForDuplicate() throws SOException {
    }

    default void save(Transaction transaction) throws Exception {
    }

    default StoredObjectLink<T> copy() {
        //noinspection unchecked
        return (StoredObjectLink<T>) EMPTY;
    }

    StoredObjectLink<?> EMPTY = new StoredObjectLink<>() {

        @Override
        public StoredObject getMaster() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean contains(StoredObject item) {
            return false;
        }

        @Override
        public boolean isAdded(StoredObject item) {
            return false;
        }

        @Override
        public boolean isDeleted(StoredObject item) {
            return false;
        }

        @Override
        public boolean isEdited(StoredObject item) {
            return false;
        }

        @Override
        public Stream<StoredObject> streamAll() {
            return Stream.of();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean append(StoredObject item) {
            return false;
        }

        @Override
        public boolean add(StoredObject item) {
            return false;
        }

        @Override
        public boolean delete(StoredObject item) {
            return false;
        }

        @Override
        public boolean undelete(StoredObject item) {
            return false;
        }

        @Override
        public boolean update(StoredObject item) {
            return false;
        }
    };

    static <O extends StoredObject> StoredObjectLink<O> create(StoredObjectUtility.Link<O> link, StoredObject master) {
        //noinspection unchecked
        return (StoredObjectLink<O>) EMPTY;
    }
}