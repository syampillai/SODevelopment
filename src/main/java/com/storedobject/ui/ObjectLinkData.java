package com.storedobject.ui;

import com.storedobject.core.Detail;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.Transaction;

import java.util.Collection;
import java.util.stream.Stream;

public interface ObjectLinkData<L extends StoredObject> extends EditableList<L> {

    ObjectLinkData EMPTY = new ObjectLinkData() {
        @Override
        public StoredObject getMaster() {
            return null;
        }

        @Override
        public int getLinkType() {
            return 0;
        }

        @Override
        public String getFieldName() {
            return null;
        }

        @Override
        public boolean contains(Object item) {
            return false;
        }

        @Override
        public boolean isAdded(Object item) {
            return false;
        }

        @Override
        public boolean isDeleted(Object item) {
            return false;
        }

        @Override
        public boolean isEdited(Object item) {
            return false;
        }

        @Override
        public Stream streamAll() {
            return Stream.of();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean append(Object item) {
            return false;
        }

        @Override
        public boolean add(Object item) {
            return false;
        }

        @Override
        public boolean delete(Object item) {
            return false;
        }

        @Override
        public boolean undelete(Object item) {
            return false;
        }

        @Override
        public boolean update(Object item) {
            return false;
        }
    };

    StoredObject getMaster();

    int getLinkType();

    String getFieldName();

    default boolean isDetail(L object) {
        if(object instanceof Detail) {
            return ((Detail) object).isDetailOf(getMaster().getClass());
        }
        return false;
    }

    default void save(Transaction transaction) throws Exception {
        StoredObject master = getMaster();
        try {
            streamAll().forEach(o -> {
                try {
                    if (isDeleted(o)) {
                        if (isDetail(o)) {
                            o.delete(transaction);
                        } else {
                            master.removeLink(transaction, o, getLinkType());
                        }
                    } else if (isAdded(o)) {
                        if (isDetail(o)) {
                            o.save(transaction);
                        }
                        master.addLink(transaction, o, getLinkType());
                    } else if (isEdited(o)) {
                        if (isDetail(o)) {
                            o.save(transaction);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            throw (Exception)e.getCause();
        }
    }

    static <O extends StoredObject> ObjectLinkData<O> create(StoredObjectUtility.Link<O> link, StoredObject master) {
        if(master == null) {
            //noinspection unchecked
            return EMPTY;
        }
        return new ObjectLinkData<O>() {

            private Collection<O> objects = link.list(master).collectAll();

            private Collection<O> objects() {
                if(objects == null) {
                    objects = link.list(master).collectAll();
                }
                return objects;
            }

            @Override
            public StoredObject getMaster() {
                return master;
            }

            @Override
            public int getLinkType() {
                return link.getType();
            }

            @Override
            public String getFieldName() {
                return link.getName() + ".l";
            }

            @Override
            public boolean contains(O item) {
                return objects().contains(item);
            }

            @Override
            public boolean isAdded(O item) {
                return false;
            }

            @Override
            public boolean isDeleted(O item) {
                return false;
            }

            @Override
            public boolean isEdited(O item) {
                return false;
            }

            @Override
            public Stream<O> streamAll() {
                return objects().stream();
            }

            @Override
            public int size() {
                return objects().size();
            }

            @Override
            public boolean append(O item) {
                return false;
            }

            @Override
            public boolean add(O item) {
                return false;
            }

            @Override
            public boolean delete(O item) {
                return false;
            }

            @Override
            public boolean undelete(O item) {
                return false;
            }

            @Override
            public boolean update(O item) {
                return false;
            }
        };
    }
}
