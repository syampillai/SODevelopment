package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.ImageButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectComboField<T extends StoredObject> extends ComboField<T> implements ObjectInput<T>, ResourceOwner {

    private ObjectDataProvider<T, String> objectProvider;
    private String label;
    private ImageButton addButton;
    private AbstractObjectDataProvider.ObjectAdder<T> objectAdder;

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
        this(label, new ObjectSupplier<>(objectClass, condition, orderBy, any, true), allowAdd);
    }

    public ObjectComboField(List<T> list) {
        this((String)null, list);
    }

    public ObjectComboField(String label, List<T> list) {
        this(label, list,false);
    }

    public ObjectComboField(List<T> list, boolean allowAdd) {
        this((String)null, list, allowAdd);
    }

    public ObjectComboField(String label, List<T> list, boolean allowAdd) {
        this(label, null, list, allowAdd);
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
        this(label, new ObjectListProvider<>(objectClass, list), allowAdd);
    }

    private ObjectComboField(String label, ObjectDataProvider<T, String> objectProvider, boolean allowAdd) {
        super(label);
        setProvider(objectProvider);
        setItemLabelGenerator(StoredObject::toDisplay);
        if(allowAdd && !isAllowAny()) {
            ObjectField.checkDetailClass(this.objectProvider.getObjectClass(), label);
            addButton = new ImageButton("Add new", VaadinIcon.PLUS, e -> addNew());
            addToPrefix(addButton);
        }
        ResourceDisposal.register(this);
        setSpellCheck(false);
    }

    private void addNew() {
        if(objectAdder == null) {
            objectAdder = ObjectDataProvider.ObjectAdder.create(objectProvider,this);
        }
        getElement().callJsFunction("close").then(r -> objectAdder.add());
    }

    private void setProvider(DataProvider<T, String> dataProvider) {
        if(dataProvider instanceof ObjectDataProvider) {
            this.objectProvider = (ObjectDataProvider<T, String>) dataProvider;
            if(this.objectProvider instanceof BackEndDataProvider) {
                //noinspection unchecked
                setItems((BackEndDataProvider<T, String>)this.objectProvider);
            } else {
                setItems(this.objectProvider);
            }
        }
    }

    @Override
    public final AutoCloseable getResource() {
        return objectProvider.getResource();
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        ViewFilter<T> viewFilter = objectProvider.getViewFilter();
        if(viewFilter == null) {
            viewFilter = new ViewFilter<>(objectProvider);
        }
        if(itemLabelGenerator == null) {
            viewFilter.setObjectConverter(null);
        } else {
            viewFilter.setObjectConverter(itemLabelGenerator::apply);
        }
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
    public void setFilter(FilterProvider filterProvider) {
        objectProvider.setFilter(filterProvider);
    }

    @Override
    public void filter(Predicate<T> filter) {
        objectProvider.filter(filter);
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
        if(addButton != null) {
            addButton.setVisible(!isReadOnly() && enabled);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if(addButton != null) {
            addButton.setVisible(!readOnly && isEnabled());
        }
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
    public Predicate<T> getFilterPredicate() {
        return objectProvider.getFilterPredicate();
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
        objectProvider.setLoadFilter(filter);
        reget();
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return objectProvider.getLoadFilter();
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
        objectProvider.setFilter(filterProvider, extraFilterClause);
        reget();
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        objectProvider.setFilter(filter);
        reget();
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return objectProvider.getFilter(create);
    }

    @Override
    public void setFilter(String filterClause) {
        objectProvider.setFilter(filterClause);
        reget();
    }

    protected T filter(T value) {
        if(value == null) {
            return null;
        }
        Predicate<T> predicate = getFilterPredicate();
        if(predicate != null && !predicate.test(value)) {
            return null;
        }
        predicate = getLoadFilter();
        if(predicate != null && !predicate.test(value)) {
            return null;
        }
        ObjectSearchFilter filter = objectProvider.getFilter(false);
        if(filter == null) {
            return value;
        }
        String c = filter.getFilter(null);
        if(c == null || c.isEmpty()) {
            return value;
        }
        //noinspection unchecked
        return (T) StoredObject.get(value.getClass(), "T.Id=" + value.getId() + " AND (" + c + ")");
    }

    private void reget() {
        T v1 = getValue();
        if(v1 == null) {
            return;
        }
        T v2 = filter(v1);
        if(v2 == null) {
            setValue((T)null);
        }
    }

    @Override
    public void filterChanged() {
        objectProvider.filterChanged();
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        if(objects == null) {
            objects = ObjectIterator.create();
        }
        clear();
        setProvider(new ObjectListProvider<>(getObjectClass(), objects.filter(getLoadFilter()).toList()));
    }
}