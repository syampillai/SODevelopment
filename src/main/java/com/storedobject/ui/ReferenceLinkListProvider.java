package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ReferenceLinkListProvider<T extends StoredObject> extends ObjectListProvider<T>
        implements EditableProvider<T> {

    private final List<Id> added = new ArrayList<>();
    private final List<Id> deleted = new ArrayList<>();
    private final OldValue oldValue = new OldValue();
    private List<BiConsumer<AbstractListProvider<T>, Boolean>> trackers;

    public ReferenceLinkListProvider(Class<T> objectClass, DataList<T> data) {
        super(objectClass, data);
        ((ObjectMemoryList<T>)getItems().getData()).setLoader(this::load);
    }

    private T load(Id id) {
        return StoredObject.get(getObjectClass(), id, isAllowAny());
    }

    public boolean isChanged() {
        return !added.isEmpty() || !deleted.isEmpty();
    }

    public Registration addValueChangeTracker(BiConsumer<AbstractListProvider<T>, Boolean> tracker) {
        if(tracker == null) {
            return null;
        }
        if(trackers == null) {
            trackers = new ArrayList<>();
        }
        trackers.add(tracker);
        return () -> trackers.remove(tracker);
    }

    @Override
    public void refreshItem(T item) {
        refreshItem(false, item);
    }

    void refreshItem(boolean fromClient, T item) {
        super.refreshItem(item);
        fireChanges(fromClient);
    }

    @Override
    public void refreshAll() {
        refreshAll(false);
    }

    void refreshAll(boolean fromClient) {
        super.refreshAll();
        fireChanges(fromClient);
    }

    private void fireChanges(boolean fromClient) {
        if(trackers != null) {
            trackers.forEach(t -> t.accept(this, fromClient));
        }
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void clear() {
        clearInt();
        super.clear();
    }

    private void clearInt() {
        added.clear();
        deleted.clear();
    }

    @Override
    public void reload() {
        clearInt();
        super.reload();
    }

    public int reload(T item, boolean refresh) {
        if(isAdded(item)) {
            getData().remove(item);
            added.remove(item.getId());
            if(refresh) {
                refreshAll();
            }
            return EditorAction.DELETE;
        }
        Id id = item.getId();
        deleted.remove(id);
        item = getData().refresh(item);
        if(refresh) {
            refreshItem(item);
        }
        return EditorAction.RELOAD;
    }

    @Override
    public boolean append(T item, boolean refresh) {
        if(addInt(item, true)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    private boolean addInt(T item, boolean append) {
        if(item == null) {
            return false;
        }
        if(item.created()) {
            item.makeVirtual();
            getData().add(item);
            added.add(item.getId());
            return true;
        }
        Id id = item.getId();
        if(deleted.remove(id)) {
            return true;
        }
        if(contains(item)) {
            return false;
        }
        getData().add(item);
        if(!append) {
            added.add(id);
        }
        return true;
    }

    @Override
    public boolean add(T item, boolean refresh) {
        if(addInt(item, false)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(T item, boolean refresh) {
        if(item == null || item.created() || !contains(item)) {
            return false;
        }
        Id id = getId(item);
        if(added.contains(id)) {
            added.remove(id);
            getData().remove(item);
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        if(deleted.contains(id)) {
            return false;
        }
        deleted.add(id);
        item = getData().refresh(item);
        if(refresh) {
            refreshItem(item);
        }
        return true;
    }

    @Override
    public boolean undelete(T item, boolean refresh) {
        if(item == null || item.created()) {
            return false;
        }
        Id id = item.getId();
        if(added.contains(id)) {
            return false;
        }
        deleted.remove(item.getId());
        item = getData().refresh(item);
        if(refresh) {
            refreshItem(item);
        }
        return true;
    }

    @Override
    public boolean update(T item, boolean refresh) {
        return false;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        clearInt();
        super.load(objects);
    }

    /**
     * This method should be called to reset the status of all rows after all changes are saved.
     */
    public void savedAll() {
        clearInt();
        refreshAll();
    }

    @Override
    public boolean isAdded(T item) {
        return item != null && added.contains(item.getId());
    }

    @Override
    public boolean isDeleted(T item) {
        return item != null && deleted.contains(item.getId());
    }

    @Override
    public boolean isEdited(T item) {
        return false;
    }

    @Override
    public Stream<T> streamAdded() {
        return getData().stream().filter(o -> added.contains(o.getId()));
    }

    @Override
    public Stream<T> streamEdited() {
        return Stream.of();
    }

    @Override
    public Stream<T> streamDeleted() {
        return getData().stream().filter(o -> deleted.contains(o.getId()));
    }

    EditableList<T> getOldValue() {
        return oldValue;
    }

    private class OldValue implements EditableList<T> {

        @Override
        public boolean contains(Object item) {
            return false;
        }

        @Override
        public boolean isAdded(T item) {
            return false;
        }

        @Override
        public boolean isDeleted(T item) {
            return false;
        }

        @Override
        public boolean isEdited(T item) {
            return false;
        }

        @Override
        public Stream<T> streamAll() {
            //noinspection unchecked
            return ReferenceLinkListProvider.this.streamAll()
                    .filter(item -> !ReferenceLinkListProvider.this.isAdded(item))
                    .map(item -> (T)StoredObject.get(item.getClass(), item.getId()));
        }

        @Override
        public Stream<T> stream() {
            return streamAll();
        }

        @Override
        public Stream<T> streamAdded() {
            return Stream.of();
        }

        @Override
        public Stream<T> streamDeleted() {
            return Stream.of();
        }

        @Override
        public Stream<T> streamEdited() {
            return Stream.of();
        }

        @Override
        public int size() {
            return ReferenceLinkListProvider.this.size() - added.size();
        }

        @Override
        public boolean append(T item) {
            return false;
        }

        @Override
        public boolean add(T item) {
            return false;
        }

        @Override
        public boolean delete(T item) {
            return false;
        }

        @Override
        public boolean undelete(T item) {
            return false;
        }

        @Override
        public boolean update(T item) {
            return false;
        }
    }
}
