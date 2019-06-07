package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.util.IdInput;
import com.storedobject.ui.util.ObjectInput;
import com.storedobject.vaadin.CustomField;
import com.vaadin.flow.component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectField<T extends StoredObject> extends CustomField<Id> implements IdInput<T> {

    public enum Type { AUTO, CHOICE, GET, SEARCH, IMAGE }
    private final Class<T> objectClass;
    private final boolean any;
    private final ObjectInput<T> field;
    private T cached;

    public ObjectField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectField(Class<T> objectClass, Type type) {
        this(null, objectClass, type);
    }

    public ObjectField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectField(String label, Class<T> objectClass, Type type) {
        this(label, objectClass, false, type);
    }

    public ObjectField(Class<T> objectClass, boolean any) {
        this(null, objectClass, any, Type.AUTO);
    }

    public ObjectField(String label, Class<T> objectClass, boolean any) {
        this(label, objectClass, any, Type.AUTO);
    }

    public ObjectField(Class<T> objectClass, boolean any, Type type) {
        this(null, objectClass, any, type);
    }

    public ObjectField(String label, Class<T> objectClass, boolean any, Type type) {
        this(label, objectClass, any, type, null, false);
    }

    public ObjectField(Class<T> objectClass, boolean any, boolean addAllowed) {
        this(null, objectClass, any, Type.AUTO, addAllowed);
    }

    public ObjectField(String label, Class<T> objectClass, boolean any, boolean addAllowed) {
        this(label, objectClass, any, Type.AUTO, addAllowed);
    }

    public ObjectField(Class<T> objectClass, boolean any, Type type, boolean addAllowed) {
        this(null, objectClass, any, type, addAllowed);
    }

    public ObjectField(String label, Class<T> objectClass, boolean any, Type type, boolean addAllowed) {
        this(label, objectClass, any, type, null, addAllowed);
    }

    public ObjectField(List<T> list) {
        this(null, list);
    }

    public ObjectField(Iterable<T> list) {
        this(null, list);
    }

    public ObjectField(String label, Iterable<T> list) {
        this(label, list(list));
    }

    protected ObjectField(String label, Class<T> objectClass, boolean any, Type type, ObjectGetField.GetProvider<T> getProvider, boolean addAllowed) {
        this(label, objectClass, any, createField(objectClass, type, any, getProvider, addAllowed));
    }

    @SuppressWarnings("unchecked")
    public ObjectField(String label, List<T> list) {
        this(label, (Class<T>)list.get(0).getClass(), false, new ObjectComboField<>(list));
    }

    private ObjectField(String label, Class<T> objectClass, boolean any, ObjectInput<T> field) {
        super(null);
        this.objectClass = objectClass;
        this.any = any;
        this.field = field;
        add((Component)field);
        this.setLabel(label);
        if(field instanceof AbstractObjectField) {
            ((AbstractObjectField<T>) field).addValueChangeListener(e -> setModelValue(field.getObjectId(), true));
        }
    }

    private static <O extends StoredObject> List<O> list(Iterable<O> iterable) {
        java.util.ArrayList<O> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        field.setInternalLabel(label);
    }

    public void setPlaceholder(String placeholder) {
        field.setPlaceholder(placeholder);
    }

    @Override
    public T getCached() {
        return cached == null ? field.getCached() : cached;
    }

    @Override
    public void setCached(T object) {
        cached = object;
        field.setCached(object);
    }

    @Override
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public void setFilterProvider(FilterProvider filterProvider) {
        field.setFilterProvider(filterProvider);
    }

    @Override
    public void setFilter(Predicate<T> filter) {
        field.setFilter(filter);
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
        field.setDetailComponent(detailComponent);
    }

    @Override
    public Component getDetailComponent() {
        return field.getDetailComponent();
    }

    @Override
    public void setDisplayDetail(Consumer<T> displayDetail) {
        field.setDisplayDetail(displayDetail);
    }

    @Override
    public Consumer<T> getDisplayDetail() {
        return field.getDisplayDetail();
    }

    @Override
    public void setPrefixFieldControl(boolean searchFieldControl) {
        field.setPrefixFieldControl(searchFieldControl);
    }

    @Override
    public boolean isAllowAny() {
        return this.any;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        field.setReadOnly(readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        field.setEnabled(enabled);
    }

    private static <O extends StoredObject> Type type(Type type, Class<O> objectClass, boolean any) {
        if(StreamData.class.isAssignableFrom(objectClass)) {
            return Type.AUTO;
        }
        if(InventoryItem.class.isAssignableFrom(objectClass)) {
            return Type.AUTO;
        }
        boolean tryGet = true;
        if(type == Type.GET && !ObjectGetField.canCreate(objectClass)) {
            type = Type.AUTO;
            tryGet = false;
        }
        if (type != Type.AUTO) {
            return type;
        }
        if((StoredObjectUtility.hints(objectClass) & 2) == 2 && StoredObjectUtility.howBig(objectClass, any) < 16) {
            return Type.CHOICE;
        }
        if(tryGet && ObjectGetField.canCreate(objectClass)) {
            return Type.GET;
        }
        return Type.SEARCH;
    }

    @SuppressWarnings("unchecked")
    private static <O extends StoredObject> ObjectInput<O> createField(Class<O> objectClass, Type type, boolean any,
                                                                       ObjectGetField.GetProvider<O> getProvider, boolean addAllowed) {
        if(StreamData.class.isAssignableFrom(objectClass)) {
            return (ObjectInput<O>) new FileField(null, true, true, type == Type.IMAGE);
        }
        if(InventoryItem.class.isAssignableFrom(objectClass)) {
            Class<? extends InventoryItem> iClass = (Class<? extends InventoryItem>) objectClass;
            return new InventoryItemField(iClass, any, addAllowed);
        }
        switch (type(type, objectClass, any)) {
            case GET:
                return new ObjectGetField<>(null, objectClass, any, getProvider);
            case CHOICE:
                return new ObjectComboField<>(objectClass, any);
        }
        return new ObjectSearchField<>(objectClass, any);
    }

    public ObjectInput<T> getField() {
        return field;
    }

    @Override
    protected Id generateModelValue() {
        return field.getObjectId();
    }

    @Override
    protected void setPresentationValue(Id value) {
        field.setValue(value);
    }
}