package com.storedobject.ui;

import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.vaadin.ListField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObjectListField<T extends StoredObject> extends ListField<T>
        implements ObjectInput<T>, ResourceOwner, ObjectLoader<T> {

    private final ObjectListProvider<T> objectProvider;
    private String label;

    public ObjectListField(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectListField(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectListField(Class<T> objectClass, String condition) {
        this(objectClass, condition, null, false);
    }

    public ObjectListField(Class<T> objectClass, String condition, boolean any) {
        this(objectClass, condition, null, any);
    }

    public ObjectListField(Class<T> objectClass, String condition, String orderBy) {
        this(objectClass, condition, orderBy, false);
    }

    public ObjectListField(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(null, objectClass, condition, orderBy, any);
    }

    public ObjectListField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectListField(String label, Class<T> objectClass, boolean any) {
        this(label, objectClass, null, any);
    }

    public ObjectListField(String label, Class<T> objectClass, String condition) {
        this(label, objectClass, condition, null, false);
    }

    public ObjectListField(String label, Class<T> objectClass, String condition, boolean any) {
        this(label, objectClass, condition, null, any);
    }

    public ObjectListField(String label, Class<T> objectClass, String condition, String orderBy) {
        this(label, objectClass, condition, orderBy,false);
    }

    public ObjectListField(String label, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(label, objectClass, condition, orderBy, any,false);
    }

    public ObjectListField(String label, Class<T> objectClass, String condition, String orderBy, boolean any,
                            boolean allowAdd) {
        this(label, new ObjectCacheList<>(objectClass, condition, orderBy, any));
    }

    public ObjectListField(List<T> list) {
        this((String)null, list);
    }

    public ObjectListField(String label, List<T> list) {
        this(label, null, list);
    }

    public ObjectListField(Class<T> objectClass, List<T> list) {
        this(null, objectClass, list);
    }

    public ObjectListField(String label, Class<T> objectClass, List<T> list) {
        this(label, new ObjectMemoryList<>(checkClass(objectClass, list), true));
        objectProvider.load(list);
    }

    protected ObjectListField(String label, ObjectList<T> objectCache) {
        super(label, new ArrayList<>());
        this.objectProvider = new ObjectListProvider<>(objectCache);
        setItems(objectProvider);
        setItemLabelGenerator(StoredObject::toDisplay);
        ResourceDisposal.register(this);
        setSpellCheck(false);
    }

    static <O extends StoredObject> Class<O> checkClass(Class<O> objectClass, List<O> list) {
        if(objectClass == null) {
            if(list != null && !list.isEmpty()) {
                //noinspection unchecked
                objectClass = (Class<O>) list.getFirst().getClass();
            }
        }
        if(objectClass == null) {
            throw new SORuntimeException("Can't determine Object's class!");
        }
        return objectClass;
    }

    @Override
    public final AutoCloseable getResource() {
        return objectProvider.getResource();
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        ViewFilter<T> viewFilter = objectProvider.getViewFilter();
        viewFilter.setObjectConverter(itemLabelGenerator);
        super.setItemLabelGenerator(itemLabelGenerator);
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
    }

    @Override
    public Component getDetailComponent() {
        return null;
    }

    @Override
    public void setDisplayDetail(Consumer<T> displayDetail) {
    }

    @Override
    public Consumer<T> getDisplayDetail() {
        return null;
    }

    @Override
    public void setPrefixFieldControl(boolean searchFieldControl) {
    }

    public int getObjectCount() {
        return objectProvider.getObjectCount();
    }

    @Override
    public T get(int index) {
        return objectProvider.get(index);
    }

    @Override
    public int indexOf(T object) {
        return objectProvider.indexOf(object);
    }

    public T getObject(int index) {
        return objectProvider.get(index);
    }

    public void setFirstValue() {
        if(getObjectCount() > 0) {
            setValue(getObject(0));
        }
        setPlaceholder("");
    }

    @Override
    public Class<T> getObjectClass() {
        return objectProvider.getObjectClass();
    }

    @Override
    public T getCached() {
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

    @Override
    public void setInternalLabel(String label) {
        this.label = label;
    }

    @Override
    public String getInternalLabel() {
        return label;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        objectProvider.load(objects);
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void setFilter(String filterClause) {
        ObjectLoader.super.setFilter(filterClause);
    }

    @Override
    public final void applyFilterPredicate() {
        objectProvider.applyFilterPredicate();
    }

    @Override
    public final ObjectListProvider<T> getDelegatedLoader() {
        return objectProvider;
    }

    @Override
    public boolean isAllowAny() {
        return objectProvider.isAllowAny();
    }

    @Override
    public void reload() {
        T v = getValue();
        load();
        if(!canContain(v)) {
            clear();
        }
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequired(required);
    }
}