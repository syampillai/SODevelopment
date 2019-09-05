package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.util.IdInput;
import com.storedobject.ui.util.ObjectInput;
import com.storedobject.vaadin.CustomField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectField<T extends StoredObject> extends CustomField<Id> implements IdInput<T> {

    public enum Type { AUTO, CHOICE, GET, SEARCH, FORM, IMAGE, FILE, INVENTORY_ITEM, INVENTORY_BIN }

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
        this(label, objectClass, any, (ObjectInput<T>)null);
    }

    public ObjectField(String label, List<T> list) {
        this(label, list, false);
    }

    @SuppressWarnings("unchecked")
    public ObjectField(String label, List<T> list, boolean any) {
        this(label, (Class<T>)list.get(0).getClass(), any, new ObjectComboField<>(list));
    }

    public ObjectField(ObjectInput<T> field) {
        this(null, field);
    }

    public ObjectField(String label, ObjectInput<T> field) {
        this(label, field.getObjectClass(), field.isAllowAny(), field);
    }

    private ObjectField(String label, Class<T> objectClass, boolean any, ObjectInput<T> field) {
        super(null);
    }

    private static <O extends StoredObject> List<O> list(Iterable<O> iterable) {
        java.util.ArrayList<O> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public void setPlaceholder(String placeholder) {
    }

    @Override
    public T getCached() {
        return null;
    }

    @Override
    public void setCached(T object) {
    }

    @Override
    public final Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void setFilter(FilterProvider filterProvider) {
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void filter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return null;
    }

    @Override
    public void filterChanged() {
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

    @Override
    public boolean isAllowAny() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public ObjectInput<T> getField() {
        return null;
    }

    @Override
    protected Id generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(Id value) {
    }

    public Type getType() {
        return null;
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }
}