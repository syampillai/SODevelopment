package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectListProvider;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditableObjectList<T extends StoredObject> extends ObjectListProvider<T, Void> implements EditableList<T> {

    private final List<Id> added = new ArrayList<>();
    private final List<Id> edited = new ArrayList<>();
    private final List<Id> deleted = new ArrayList<>();
    private final OldValue oldValue = new OldValue();
    private final Map<Id, T> newIds = new HashMap<>();
    private boolean fromClient = true;
    private List<BiConsumer<EditableObjectList<T>, Boolean>> trackers;

    public EditableObjectList(Class<T> objectClass) {
        this(objectClass, false);
    }

    public EditableObjectList(Class<T> objectClass, boolean allowAny) {
        super(objectClass);
        setAllowAny(allowAny);
    }

    public boolean isChanged() {
        return !added.isEmpty() || !edited.isEmpty() || !deleted.isEmpty();
    }

    @Override
    public Id getId(T item) {
        if(!item.created()) {
            return item.getId();
        }
        return newIds.entrySet().stream().filter(e -> e.getValue() == item).map(Map.Entry::getKey).findAny().orElse(null);
    }

    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    public boolean isFromClient() {
        return fromClient;
    }

    public Registration addValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
        if(tracker == null) {
            return null;
        }
        if(trackers == null) {
            trackers = new ArrayList<>();
        }
        trackers.add(tracker);
        return () -> trackers.remove(tracker);
    }

    void fireChanges(T item) {
        fireChanges(item, false);
    }

    private void fireChanges() {
        fireChanges(null, true);
    }

    private void fireChanges(T item, boolean refreshAll) {
        if(item != null) {
            refreshItem(item);
        } else if(refreshAll) {
            refreshAll();
        }
        if(trackers != null) {
            trackers.forEach(t -> t.accept(this, fromClient));
        }
    }

    @Override
    public void clear() {
        super.clear(false);
        clearInt();
        fireChanges();
    }

    private void clearInt() {
        streamAdded().collect(Collectors.toList()).forEach(this::deleteCache);
        added.clear();
        newIds.clear();
        deleted.clear();
        edited.clear();
    }

    @Override
    public boolean append(int index, T item, boolean refresh) {
        return add(index, item, refresh, true);
    }

    @Override
    public boolean add(int index, T item, boolean refresh) {
        return add(index, item, refresh, false);
    }

    private boolean add(int index, T item, boolean refresh, boolean append) {
        if(item.created()) {
            Id id = getId(item);
            if(id != null) {
                if(refresh) {
                    refreshItem(item);
                }
                return false;
            }
            id = new Id();
            newIds.put(id, item);
            added.add(id);
        } else {
            if(!append) {
                added.add(item.getId());
            }
        }
        if(!super.add(index, item, refresh)) {
            Id id = getId(item);
            added.remove(id);
            newIds.remove(id);
            return false;
        }
        if(refresh) {
            fireChanges();
        }
        return true;
    }

    @Override
    public boolean delete(T item) {
        if(item == null) {
            return false;
        }
        Id id = getId(item);
        if(added.contains(id)) {
            if(!super.delete(item)) {
                return false;
            }
            added.remove(id);
            newIds.remove(id);
        } else if(!deleted.contains(id)) {
            edited.remove(id);
            deleted.add(id);
        } else {
            return false;
        }
        fireChanges();
        return true;
    }

    @Override
    public boolean delete(int index) {
        return delete(getItem(index));
    }

    @Override
    public boolean undelete(T item) {
        if(item == null) {
            return false;
        }
        Id id = getId(item);
        if(!deleted.contains(id)) {
            return false;
        }
        deleted.remove(id);
        fireChanges(item);
        return true;
    }

    public boolean undelete(int index) {
        return undelete(getItem(index));
    }

    @Override
    public boolean update(T item) {
        if(!super.update(item)) {
            return false;
        }
        Id id = getId(item);
        deleted.remove(id);
        if(!added.contains(id)) {
            if(!edited.contains(id)) {
                edited.add(id);
            }
        }
        fireChanges(item);
        return true;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        clearInt();
        super.load(objects);
        fireChanges(null);
    }

    @Override
    public void reload(T item) {
        if(item == null) {
            return;
        }
        Id id = getId(item);
        deleted.remove(id);
        edited.remove(id);
        added.remove(id);
        super.reload(item);
        fireChanges(null);
    }

    @Override
    public void reloadAll() {
        clearInt();
        super.reloadAll();
        fireChanges(null);
    }

    /**
     * This method should be called to reset the status of all rows after all changes are saved.
     */
    public void savedAll() {
        streamDeleted().collect(Collectors.toList()).forEach(this::deleteCache);
        deleted.clear();
        edited.clear();
        List<T> items = streamAdded().collect(Collectors.toList());
        items.forEach(this::deleteCache);
        items.forEach(StoredObject::reload);
        added.clear();
        newIds.clear();
        items.forEach(item -> append(item, false));
        fireChanges();
    }

    @Override
    public boolean isAdded(T item) {
        return added.contains(getId(item));
    }

    @Override
    public boolean isDeleted(T item) {
        return deleted.contains(getId(item));
    }

    @Override
    public boolean isEdited(T item) {
        return edited.contains(getId(item));
    }

    @Override
    public Stream<T> streamAdded() {
        return newIds.values().stream();
    }

    public EditableList<T> getValue() {
        return this;
    }

    public EditableList<T> getOldValue() {
        return oldValue;
    }

    private class OldValue implements EditableList<T> {

        @Override
        public boolean contains(T item) {
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
            return EditableObjectList.this.streamAll().filter(item -> !EditableObjectList.this.isAdded(item)).map(item -> (T)StoredObject.get(item.getClass(), item.getId()));
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
            return EditableObjectList.this.size();
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
