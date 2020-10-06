package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.CustomTextField;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectCodeField<T extends StoredObject> extends CustomTextField<T> implements ObjectInput<T> {

    public ObjectCodeField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectCodeField(String label, Class<T> objectClass) {
        this(label, objectClass, "Code", 0);
    }

    public ObjectCodeField(Class<T> objectClass, int maxLength) {
        this(null, objectClass, maxLength);
    }

    public ObjectCodeField(String label, Class<T> objectClass, int maxLength) {
        this(label, objectClass, "Code", maxLength);
    }

    public ObjectCodeField(Class<T> objectClass, String codeAttribute) {
        this(null, objectClass, codeAttribute, 0);
    }

    public ObjectCodeField(String label, Class<T> objectClass, String codeAttribute) {
        this(label, objectClass, codeAttribute, 0);
    }

    public ObjectCodeField(Class<T> objectClass, String codeAttribute, int maxLength) {
        this(null, objectClass, codeAttribute, maxLength);
    }

    public ObjectCodeField(String label, Class<T> objectClass, String codeAttribute, int maxLength) {
        super(null);
    }

    @Override
    public void setInternalLabel(String label) {
    }

    @Override
    public String getInternalLabel() {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        //noinspection unchecked
        return (Class<T>) Person.class;
    }

    @Override
    public T getCached() {
        return null;
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
    public void setPrefixFieldControl(boolean prefixFieldControl) {
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
    public void filterChanged() {
    }

    @Override
    protected T getModelValue(String string) {
        return null;
    }
}
