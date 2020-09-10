package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectComboField<T extends StoredObject> extends ComboBox<T> implements ObjectInput<T>, ResourceOwner {

    public ObjectComboField(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectComboField(Class<T> objectClass, boolean any) {
        this(objectClass, (String)null, any);
    }

    public ObjectComboField(Class<T> objectClass, boolean any, boolean allowAdd) {
        this(objectClass, null, any, allowAdd);
    }

    public ObjectComboField(Class<T> objectClass, String condition) {
        this(objectClass, condition, null, false);
    }

    public ObjectComboField(Class<T> objectClass, String condition, boolean any) {
        this(objectClass, condition, null, any);
    }

    public ObjectComboField(Class<T> objectClass, String condition, boolean any, boolean allowAdd) {
        this(objectClass, condition, null, any, allowAdd);
    }

    public ObjectComboField(Class<T> objectClass, String condition, String orderBy) {
        this(objectClass, condition, orderBy, false);
    }

    public ObjectComboField(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(null, objectClass, condition, orderBy, any);
    }

    public ObjectComboField(Class<T> objectClass, String condition, String orderBy, boolean any, boolean allowAdd) {
        this(null, objectClass, condition, orderBy, any, allowAdd);
    }

    public ObjectComboField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectComboField(String label, Class<T> objectClass, boolean any) {
        this(label, objectClass, (String)null, any);
    }

    public ObjectComboField(String label, Class<T> objectClass, boolean any, boolean allowAdd) {
        this(label, objectClass, null, any, allowAdd);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition) {
        this(label, objectClass, condition, null, false);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, boolean any) {
        this(label, objectClass, condition, null, any);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, boolean any, boolean allowAdd) {
        this(label, objectClass, condition, null, any, allowAdd);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy) {
        this(label, objectClass, condition, orderBy,false);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(label, objectClass, condition, orderBy, any,false);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy, boolean any, boolean allowAdd) {
        super(label);
    }

    public ObjectComboField(List<T> list) {
        super(null);
    }

    public ObjectComboField(String label, List<T> list) {
        super(label);
    }

    public ObjectComboField(List<T> list, boolean allowAdd) {
        super(null);
    }

    public ObjectComboField(String label, List<T> list, boolean allowAdd) {
        super(label);
    }

    public ObjectComboField(Class<T> objectClass, List<T> list) {
        this(null, objectClass, list);
    }

    public ObjectComboField(String label, Class<T> objectClass, List<T> list) {
        this(label, objectClass, list,false);
    }

    public ObjectComboField(Class<T> objectClass, List<T> list, boolean allowAdd) {
        this(null, objectClass, list, allowAdd);
    }

    public ObjectComboField(String label, Class<T> objectClass, List<T> list, boolean allowAdd) {
        super(label);
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public boolean isAllowAny() {
        return false;
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
        return 0;
    }

    public T getObject(int index) {
        return null;
    }

    public void setFirstValue() {
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
    }

    @Override
    public String getInternalLabel() {
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
    public void setFilter(String filterClause) {
    }

    @Override
    public void filterChanged() {
    }

    @Override
    public final AutoCloseable getResource() {
        return null;
    }

    @Override
    public void focus() {
    }
}