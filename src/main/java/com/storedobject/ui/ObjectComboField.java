package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.ui.util.ObjectInput;
import com.storedobject.ui.util.ObjectListProvider;
import com.storedobject.ui.util.ObjectSupplier;
import com.storedobject.vaadin.ComboField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectComboField<T extends StoredObject> extends ComboField<T> implements ObjectInput<T> {

    private final ObjectDataProvider<T> objectProvider;
    private String label;

    public ObjectComboField(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectComboField(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectComboField(Class<T> objectClass, String condition) {
        this(objectClass, condition, null, false);
    }

    public ObjectComboField(Class<T> objectClass, String condition, boolean any) {
        this(objectClass, condition, null, any);
    }

    public ObjectComboField(Class<T> objectClass, String condition, String orderBy) {
        this(objectClass, condition, orderBy, false);
    }

    public ObjectComboField(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(null, objectClass, condition, orderBy, any);
    }

    public ObjectComboField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectComboField(String label, Class<T> objectClass, boolean any) {
        this(label, objectClass, null, any);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition) {
        this(label, objectClass, condition, null, false);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, boolean any) {
        this(label, objectClass, condition, null, any);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy) {
        this(label, objectClass, condition, orderBy, false);
    }

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(label, new ObjectSupplier<>(objectClass, condition, orderBy, any, true));
    }

    public ObjectComboField(List<T> list) {
        this(null, list);
    }

    public ObjectComboField(String label, List<T> list) {
        this(label, new ObjectListProvider<>((list)));
    }

    private ObjectComboField(String label, ObjectDataProvider<T> objectProvider) {
        super(label);
        this.objectProvider = objectProvider;
        addDetachListener(e -> this.objectProvider.close());
        setDataProvider(objectProvider);
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        objectProvider.setItemLabelGenerator(itemLabelGenerator);
        super.setItemLabelGenerator(itemLabelGenerator);
    }

    @Override
    public Class<T> getObjectClass() {
        return objectProvider.getObjectClass();
    }

    @Override
    public boolean isAllowAny() {
        return objectProvider.isAllowAny();
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

    public T getObject(int index) {
        return objectProvider.getItem(index);
    }

    public void setFirstValue() {
        if(getObjectCount() > 0) {
            setValue(getObject(0));
        }
        setPlaceholder("");
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
}