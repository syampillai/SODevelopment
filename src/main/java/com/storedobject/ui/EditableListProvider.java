package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.EditorAction;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class EditableListProvider<T> extends ListProvider<T> implements EditableProvider<T> {

    private final List<T> added = new ArrayList<>();
    private final List<T> edited = new ArrayList<>();
    private final List<T> deleted = new ArrayList<>();
    private final OldValue oldValue = new OldValue();
    private List<BiConsumer<AbstractListProvider<T>, Boolean>> trackers;
    private final BiFunction<T, Boolean, T> loader;

    public EditableListProvider(Class<T> objectClass, DataList<T> data, BiFunction<T, Boolean, T> loader) {
        super(objectClass, data);
        if(loader == null) {
            loader = (i, ignored) -> i;
        }
        this.loader = loader;
    }

    private List<T> getList() {
        //noinspection unchecked
        return (List<T>) getData();
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

    private int size() {
        return getData().size();
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

    public void reload() {
        List<T> items = getList();
        added.forEach(items::remove);
        clearInt();
        items.forEach(o -> loader.apply(o, true));
        refreshAll();
    }

    @Override
    public int reload(T item, boolean refresh) {
        if(isAdded(item)) {
            getList().remove(item);
            added.remove(item);
            if(refresh) {
                refreshAll();
            }
            return EditorAction.DELETE;
        }
        deleted.remove(item);
        edited.remove(item);
        loader.apply(item, true);
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
        if(deleted.remove(item)) {
            return true;
        }
        if(edited.remove(item)) {
            getList().remove(item);
        }
        getList().add(item);
        if(!append) {
            added.add(item);
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
        if(item == null || !getList().contains(item)) {
            return false;
        }
        if(deleted.contains(item)) {
            return false;
        }
        if(added.contains(item)) {
            added.remove(item);
            getList().remove(item);
            if(refresh) {
                refreshAll();
            }
            return true;
        }
        deleted.add(item);
        edited.remove(item);
        if(refresh) {
            refreshItem(item);
        }
        return true;
    }

    @Override
    public boolean undelete(T item, boolean refresh) {
        if(undeleteInt(item)) {
            if(refresh) {
                refreshItem(item);
            }
            return true;
        }
        return false;
    }

    private boolean undeleteInt(T item) {
        if(item == null) {
            return false;
        }
        return deleted.remove(item);
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
        if(item == null || !getList().contains(item)) {
            return false;
        }
        if(added.contains(item)) {
            return true;
        }
        if(edited.contains(item)) {
            return true;
        }
        deleted.remove(item);
        edited.add(item);
        return true;
    }

    /**
     * This method should be called to reset the status of all rows after all changes are saved.
     */
    @Override
    public void savedAll() {
        clearInt();
        refreshAll();
    }

    @Override
    public boolean isAdded(T item) {
        return item != null && added.contains(item);
    }

    @Override
    public boolean isDeleted(T item) {
        return item != null && deleted.contains(item);
    }

    @Override
    public boolean isEdited(T item) {
        return item != null && edited.contains(item);
    }

    @Override
    public Stream<T> streamAdded() {
        return added.stream();
    }

    @Override
    public Stream<T> streamEdited() {
        return edited.stream();
    }

    @Override
    public Stream<T> streamDeleted() {
        return deleted.stream();
    }

    EditableList<T> getOldValue() {
        return oldValue;
    }

    void removeDeleted() {
        deleted.forEach(item -> getList().remove(item));
        deleted.clear();
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
            return EditableListProvider.this.streamAll().filter(item -> !EditableListProvider.this.isAdded(item))
                    .map(item -> loader.apply(item, false));
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
            return EditableListProvider.this.size() - added.size();
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
