package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.EditorAction;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectFormField<T extends StoredObject> extends CustomField<T> implements ObjectInput<T> {

    public ObjectFormField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectFormField(String label, Class<T> objectClass) {
        this(label, ObjectEditor.create(objectClass, EditorAction.ALL, ""));
    }

    public ObjectFormField(ObjectEditor<T> editor) {
        this(null, editor);
    }

    public ObjectFormField(String label, ObjectEditor<T> editor) {
    }

    @Override
    protected T generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(T object) {
    }

    @Override
    protected boolean valueEquals(T value1, T value2) {
        return value1 == value2;
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
    public boolean isInvalid() {
        return getObjectId() == null;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public T getObject() {
        return getValue();
    }

    @Override
    public void setObject(StoredObject object) {
        setValue(convert(object));
    }

    @Override
    public Id getObjectId() {
        return null;
    }

    @Override
    public void setObject(Id objectId) {
        setValue(objectId);
    }

    @Override
    public T getObject(Id objectId) {
        return null;
    }

    @Override
    public void setCached(T cached) {
    }

    @Override
    public T getCached() {
        return null;
    }

    @Override
    public void setPlaceholder(String placeholder) {
    }

    @Override
    public Id getObjectId(T object) {
        return null;
    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public void setInternalLabel(String label) {
    }

    @Override
    public String getInternalLabel() {
        return null;
    }

    @Override
    public void setValue(Id id) {
    }

    @Override
    public void setValue(T object) {
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
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }
}