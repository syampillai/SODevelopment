package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.ui.util.ObjectDataLoadedListener;
import com.storedobject.ui.util.ObjectForestSupplier;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractObjectForest<T extends StoredObject> extends DataTreeGrid<Object> implements ObjectsSetter<T>, Transactional {

    private final List<ObjectDataLoadedListener> dataLoadedListeners = new ArrayList<>();
    private static final StringList NAME = StringList.create("_Name");
    private AbstractObjectForestSupplier<T, Void> dataProvider;
    private final Class<T> objectClass;
    private String orderBy;
    private boolean keepCache = false;
    Logic logic;
    private Registration loadedIndicator;

    public AbstractObjectForest(Class<T> objectClass) {
        this(objectClass, null);
    }

    public AbstractObjectForest(Class<T> objectClass, Iterable<String> columns) {
        super(Object.class, NAME.concat(columns == null ? StoredObjectUtility.browseColumns(objectClass) : StringList.create(columns)));
        this.objectClass = objectClass;
        addDetachListener(e -> {
            if(!keepCache) {
                dataProvider.close();
            }
        });
    }

    @Override
    public void setDataProvider(DataProvider<Object, ?> dataProvider) {
    }

    @Override
    public void setDataProvider(HierarchicalDataProvider<Object, ?> hierarchicalDataProvider) {
    }

    public void setDataSupplier(AbstractObjectForestSupplier<T, Void> dataProvider) {
        if(loadedIndicator != null) {
            loadedIndicator.remove();
        }
        this.dataProvider = dataProvider;
        loadedIndicator = dataProvider.addObjectDataLoadedListener(this::loadedInt);
        super.setDataProvider(dataProvider);
    }

    public AbstractObjectForestSupplier<T, Void> getDataSupplier() {
        return dataProvider;
    }

    @Override
    public String getCaption() {
        String c = super.getCaption();
        return "Object".equals(c) ? StringUtility.makeLabel(objectClass) : c;
    }

    public void setKeepCache(boolean keepCache) {
        this.keepCache = keepCache;
        if(getParent().isEmpty() && !keepCache) {
            dataProvider.close();
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

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public Object unwrap(Object object) {
        if(object instanceof ObjectForestSupplier.LinkObject) {
            object = ((ObjectForestSupplier.LinkObject) object).getObject();
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

    @Override
    public boolean isAllowAny() {
        return dataProvider.isAllowAny();
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void load() {
        load(null, getOrderBy());
    }

    public void load(String filterClause) {
        load(filterClause, getOrderBy());
    }

    public void load(String filterClause, String orderBy) {
        deselectAll();
        dataProvider.load(filterClause, orderBy);
    }

    public void load(StoredObject master, String filterClause, String orderBy) {
        load(0, master, filterClause, orderBy);
    }

    public void load(int linkType, StoredObject master, String filterClause, String orderBy) {
        deselectAll();
        dataProvider.load(linkType, master, filterClause, orderBy);
    }

    public void load(Stream<T> objects) {
        load(objects.collect(Collectors.toList()));
    }

    public void load(Iterator<T> objects) {
        load(ObjectIterator.create(objects));
    }

    public void load(ObjectIterator<T> objects) {
        deselectAll();
        dataProvider.load(objects);
    }

    public void load(Iterable<T> objects) {
        load(objects.iterator());
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
        dataLoadedListeners.forEach(ObjectDataLoadedListener::dataLoaded);
    }

    public void clear() {
        dataProvider.clear();
    }

    boolean isFullyLoaded() {
        return dataProvider.isFullyLoaded();
    }

    public void setFilter(String filterClause) {
        setFilter(null, filterClause);
    }

    public void setFilter(FilterProvider filterProvider) {
        setFilter(filterProvider, null);
    }

    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
        dataProvider.setFilter(filterProvider);
        dataProvider.setFilter(extraFilterClause);
    }

    public void filter(Predicate<T> filter) {
        dataProvider.filter(filter);
    }

    public ObjectSearchFilter getFilter() {
        return dataProvider.getFilter();
    }

    public void scrollTo(T object) {
        if(object != null) {
            scrollToIndex(dataProvider.indexOf(object));
        }
    }

    public T getRoot() {
        List<T> roots = dataProvider.listRoots();
        return roots.size() == 1 ? roots.get(0) : null;
    }

    public List<T> listRoots() {
        return dataProvider.listRoots();
    }

    public T getItem(int index) {
        return dataProvider.getItem(index);
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
        deselectAll();
        ObjectIterator<T> oi = ObjectIterator.create(objects.iterator()).filter(Objects::nonNull);
        dataProvider.load(oi.map(this::convert).filter(Objects::nonNull));
    }

    @SuppressWarnings("unchecked")
    private T convert(StoredObject so) {
        if(so == null || !getObjectClass().isAssignableFrom(so.getClass())) {
            return null;
        }
        if(!isAllowAny() && getObjectClass() != so.getClass()) {
            return null;
        }
        return (T)so;
    }
    public final boolean isFullyCached() {
        return dataProvider.isFullyCached();
    }

    public int size() {
        return dataProvider.getObjectCount();
    }

    public Set<StoredObject> getSelectedObjects() {
        Set<StoredObject> set = new HashSet<>();
        getSelectedItems().forEach(o -> {
            if(o instanceof StoredObject) {
                set.add((StoredObject)o);
            } else if(o instanceof ObjectForestSupplier.LinkObject) {
                set.add(((ObjectForestSupplier.LinkObject) o).getObject());
            }
        });
        return set;
    }
}