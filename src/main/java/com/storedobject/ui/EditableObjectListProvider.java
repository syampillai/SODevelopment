package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.core.ObjectList;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class EditableObjectListProvider<T extends StoredObject> extends ObjectListProvider<T>
        implements EditableList<T> {

    private final Map<Id, T> added = new HashMap<>();
    private final List<Id> edited = new ArrayList<>();
    private final List<Id> deleted = new ArrayList<>();
    private final OldValue oldValue = new OldValue();
    private boolean fromClient = true;
    private List<BiConsumer<EditableObjectListProvider<T>, Boolean>> trackers;

    public EditableObjectListProvider(Class<T> objectClass, DataList<T> data) {
        super(objectClass, data);
        ((ObjectMemoryList<T>)getItems().getData()).setLoader(this::load);
    }

    public EditableObjectListProvider(ObjectList<T> cache) {
        super(cache);
    }

    private T load(Id id) {
        T object = added.get(id);
        return object == null ? StoredObject.get(getObjectClass(), id, isAllowAny()) : object;
    }

    @Override
    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return getData().contains(o);
    }

    public boolean isChanged() {
        return !added.isEmpty() || !edited.isEmpty() || !deleted.isEmpty();
    }

    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    public boolean isFromClient() {
        return fromClient;
    }

    public Registration addValueChangeTracker(BiConsumer<EditableObjectListProvider<T>, Boolean> tracker) {
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
        super.refreshItem(item);
        fireChanges();
    }

    @Override
    public void refreshAll() {
        super.refreshAll();
        fireChanges();
    }

    void fireChanges() {
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
        edited.clear();
    }

    @Override
    public void reload() {
        List<T> items = getData();
        added.values().forEach(items::remove);
        clearInt();
        super.reload();
    }

    public void reload(T item) {
        if(isAdded(item)) {
            getData().remove(item);
            added.remove(item.getId());
            refreshAll();
        } else {
            Id id = item.getId();
            deleted.remove(id);
            edited.remove(id);
            getData().refresh(item);
            refreshItem(item);
        }
    }

    @Override
    public boolean append(T item) {
        return append(item, true);
    }

    public boolean append(T item, boolean refresh) {
        if(addInt(item, true, -1)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    private boolean addInt(T item, boolean append, int index) {
        if(item == null) {
            return false;
        }
        if(item.created()) {
            item.makeVirtual();
            if(index < 0) {
                getData().add(item);
            } else {
                getData().add(index, item);
            }
            added.put(item.getId(), item);
            return true;
        }
        Id id = item.getId();
        if(deleted.remove(id)) {
            return true;
        }
        if(edited.remove(id)) {
            getData().remove(item);
        }
        if(index < 0) {
            getData().add(item);
        } else {
            getData().add(index, item);
        }
        if(!append) {
            added.put(id, item);
        }
        return true;
    }

    @Override
    public boolean add(T item) {
        return add(item, true);
    }

    public boolean add(T item, boolean refresh) {
        return add(-1, item, refresh);
    }

    public boolean add(int index, T item, boolean refresh) {
        if(addInt(item, false, index)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(T item) {
        return delete(item, true);
    }

    public boolean delete(T item, boolean refresh) {
        if(deleteInt(item)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    private boolean deleteInt(T item) {
        if(item == null || item.created() || !contains(item)) {
            return false;
        }
        Id id = getId(item);
        if(deleted.contains(id)) {
            return false;
        }
        deleted.add(id);
        return true;
    }

    public boolean delete(int index) {
        return delete(index, true);
    }

    public boolean delete(int index, boolean refresh) {
        return delete(get(index), refresh);
    }

    @Override
    public boolean undelete(T item) {
        return undelete(item, true);
    }

    public boolean undelete(T item, boolean refresh) {
        if(undeleteInt(item)) {
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        return false;
    }

    private boolean undeleteInt(T item) {
        if(item == null || item.created()) {
            return false;
        }
        return deleted.remove(item.getId());
    }

    public boolean undelete(int index) {
        return delete(index, true);
    }

    public boolean undelete(int index, boolean refresh) {
        return delete(get(index), refresh);
    }

    @Override
    public boolean update(T item) {
        return update(item, true);
    }

    public boolean update(T item, boolean refresh) {
        if(updateInt(item)) {
            if(refresh) {
                refreshItem(item);
            }
            return true;
        }
        return false;
    }

    private boolean updateInt(T item) {
        if(item == null || item.created() || !contains(item)) {
            return false;
        }
        Id id = getId(item);
        if(added.containsKey(id)) {
            return true;
        }
        if(edited.contains(id)) {
            return true;
        }
        deleted.remove(id);
        edited.add(id);
        return true;
    }

    public boolean update(int index) {
        return update(index, true);
    }

    public boolean update(int index, boolean refresh) {
        return update(get(index), refresh);
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
        return item != null && added.containsKey(item.getId());
    }

    @Override
    public boolean isDeleted(T item) {
        return item != null && deleted.contains(item.getId());
    }

    @Override
    public boolean isEdited(T item) {
        return item != null && edited.contains(item.getId());
    }

    @Override
    public Stream<T> streamAdded() {
        return added.values().stream();
    }

    public EditableList<T> getValue() {
        return this;
    }

    public EditableList<T> getOldValue() {
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
            return EditableObjectListProvider.this.streamAll().filter(item -> !EditableObjectListProvider.this.isAdded(item))
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
            return EditableObjectListProvider.this.size();
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
