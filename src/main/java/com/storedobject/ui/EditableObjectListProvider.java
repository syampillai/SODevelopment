package com.storedobject.ui;

import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class EditableObjectListProvider<T extends StoredObject> extends ObjectListProvider<T>
        implements EditableProvider<T> {

    private final Map<Id, T> added = new HashMap<>();
    private final Map<Id, T> edited = new HashMap<>();
    private final List<Id> deleted = new ArrayList<>();
    private final OldValue oldValue = new OldValue();
    private List<BiConsumer<AbstractListProvider<T>, Boolean>> trackers;

    public EditableObjectListProvider(Class<T> objectClass, DataList<T> data) {
        super(objectClass, data);
        ((ObjectMemoryList<T>)getItems().getData()).setLoader(this::loadItem);
    }

    public EditableObjectListProvider(ObjectList<T> cache) {
        super(cache);
    }

    private T loadItem(Id id) {
        T object = added.get(id);
        return object == null ? loadItemFromDB(id) : object;
    }

    private T loadItemFromDB(Id id) {
        return StoredObject.get(getObjectClass(), id, isAllowAny());
    }

    public boolean isChanged() {
        return !added.isEmpty() || !edited.isEmpty() || !deleted.isEmpty();
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
        super.refreshItem(item);
        fireChanges(false);
    }

    @Override
    public void refreshAll() {
        super.refreshAll();
        fireChanges(false);
    }

    void fireChanges(boolean fromClient) {
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

    @Override
    public int reload(T item, boolean refresh) {
        if(item == null) {
            return 0;
        }
        Id id = item.getId();
        if(added.containsKey(id)) {
            getData().remove(item);
            added.remove(id);
            if(refresh) {
                refreshAll();
            }
            return EditorAction.DELETE;
        }
        deleted.remove(id);
        edited.remove(id);
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
        if(item.created() || item.isVirtual()) {
            if(!item.isVirtual()) {
                item.makeVirtual();
            }
            getData().add(item);
            added.put(item.getId(), item);
            return true;
        }
        Id id = item.getId();
        if(deleted.remove(id)) {
            return true;
        }
        if(edited.remove(id) != null) {
            getData().remove(item);
        }
        getData().add(item);
        if(!append) {
            added.put(id, item);
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
        if(added.containsKey(id)) {
            getData().remove(item);
            added.remove(id);
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        if(deleted.contains(id)) {
            return false;
        }
        deleted.add(id);
        edited.remove(id);
        item = getData().refresh(item);
        if(refresh) {
            refreshItem(item);
        }
        return true;
    }

    @Override
    public boolean undelete(T item, boolean refresh) {
        return reload(item, refresh) > 0;
    }

    @Override
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
        Id id = item.getId();
        if(added.containsKey(id)) {
            return true;
        }
        if(edited.containsKey(id)) {
            return true;
        }
        deleted.remove(id);
        edited.put(id, item);
        return true;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        clearInt();
        objects.forEach(o -> append(o, false));
        refreshAll();
    }

    /**
     * This method should be called to reset the status of all rows after all changes are saved.
     */
    public void savedAll() {
        List<T> items = new ArrayList<>();
        getData().stream().filter(item -> !isDeleted(item)).map(item -> loadItemFromDB(item.getId()))
                .forEach(items::add);
        clearInt();
        getData().load(items);
        refreshAll();
    }

    void reloadAll() {
        List<T> items = new ArrayList<>();
        getData().stream().filter(item -> !isAdded(item)).map(item -> loadItemFromDB(item.getId())).forEach(items::add);
        getData().load(items);
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
        return item != null && edited.containsKey(item.getId());
    }

    @Override
    public Stream<T> streamAdded() {
        return added.values().stream();
    }

    @Override
    public Stream<T> streamEdited() {
        return edited.values().stream();
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
            return EditableObjectListProvider.this.size() - added.size();
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
