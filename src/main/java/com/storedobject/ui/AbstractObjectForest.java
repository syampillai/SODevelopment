package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectForest;
import com.storedobject.core.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractObjectForest<T extends StoredObject> extends DataTreeGrid<Object>
        implements ObjectGridData<T, Object> {

    private static final StringList NAME = StringList.create("_Name");
    private final Class<T> objectClass;
    private boolean keepCache = false;
    Logic logic;

    public AbstractObjectForest(boolean large, Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(Object.class, NAME.concat(columns == null ? StoredObjectUtility.browseColumns(objectClass) : StringList.create(columns)));
        this.objectClass = objectClass;
        setDataProvider(new ObjectForestProvider<>(new ObjectForest<>(large, 0, objectClass, any)));
        addDataLoadedListener(this::loadedInt);
        addDetachListener(e -> {
            if(!keepCache) {
                getDataProvider().close();
            }
        });
    }

    @Override
    public final ObjectForestProvider<T> getDelegatedLoader() {
        return getDataProvider();
    }

    @Override
    public final ObjectForestProvider<T> getDataProvider() {
        return (ObjectForestProvider<T>) super.getDataProvider();
    }

    @Override
    public String getCaption() {
        String c = super.getCaption();
        return "Object".equals(c) ? StringUtility.makeLabel(objectClass) : c;
    }

    public void setKeepCache(boolean keepCache) {
        this.keepCache = keepCache;
        if(getParent().isEmpty() && !keepCache) {
            getDataProvider().close();
        }
    }

    public String get_Name(Object object) {
        return StringUtility.toString(object);
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("_Name".equals(columnName)) {
            return "";
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    public final boolean isColumnSortable(String columnName) {
        return false;
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public Object unwrap(Object object) {
        if(object instanceof ObjectForest.LinkObject lo) {
            object = lo.getObject();
        }
        return object;
    }

    void protect() {
    }

    @Override
    public final Logic getLogic() {
        return logic;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    public void setRoot(T root) {
        load(ObjectIterator.create(root));
    }

    /**
     * This will be invoked whenever a new set of rows are loaded via one of the "load" methods.
     */
    public void loaded() {
    }

    private void loadedInt() {
        loaded();
    }

    public void scrollTo(T object) {
        if(object != null) {
            scrollToIndex(getDataProvider().indexOf(object));
        }
    }

    public T getRoot() {
        List<T> roots = listRoots();
        return roots.size() == 1 ? roots.get(0) : null;
    }

    public List<T> listRoots() {
        return getDataProvider().getRoots();
    }

    public T getItem(int index) {
        return getDataProvider().get(index);
    }

    @Override
    public void setObject(T object) {
        deselectAll();
        if(object == null || !getObjectClass().isAssignableFrom(object.getClass())) {
            return;
        }
        select(object);
        scrollTo(object);
    }

    @Override
    public void setObjects(Iterable<T> objects) {
        load(ObjectIterator.create(objects.iterator()));
    }

    @SafeVarargs
    public final void setRoots(T... roots) {
        setRoots(ObjectIterator.create(roots));
    }

    public final void setRoots(ObjectIterator<T> roots) {
        load(roots);
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
    }

    public Set<StoredObject> getSelectedObjects() {
        Set<StoredObject> set = new HashSet<>();
        getSelectedItems().forEach(o -> {
            if(o instanceof StoredObject) {
                set.add((StoredObject)o);
            } else if(o instanceof ObjectForest.LinkObject) {
                set.add(((ObjectForest.LinkObject) o).getObject());
            }
        });
        return set;
    }

    /**
     * Select all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void selectChildren(Object parent, boolean includeGrandChildren) {
        visitChildren(parent, this::select, includeGrandChildren);
    }

    /**
     * Deselect all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void deselectChildren(Object parent, boolean includeGrandChildren) {
        visitChildren(parent, this::deselect, includeGrandChildren);
    }

    /**
     * Visit the children of the parent item.
     *
     * @param parent Parent item.
     * @param consumer Consumer to consume the visit purpose.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void visitChildren(Object parent, Consumer<Object> consumer, boolean includeGrandChildren) {
        getDataProvider().visitChildren(parent, consumer, includeGrandChildren);
    }

    /**
     * Check whether to hide the specified link or not. By returning <code>true</code> from this method,
     * you can hide a child link.
     *
     * @param masterClass The master/parent class.
     * @param linkName Name of the link.
     * @param <M> Type of the master class.
     * @return True/false. (Default is false).
     */
    public <M extends StoredObject> boolean hideLink(Class<M> masterClass, String linkName) {
        return false;
    }
}