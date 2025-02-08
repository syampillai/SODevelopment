package com.storedobject.ui;

import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectAdder;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.ImageButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectComboField<T extends StoredObject> extends ComboField<T>
        implements ObjectInput<T>, ResourceOwner, ObjectLoader<T> {

    private final ObjectListProvider<T> objectProvider;
    private String label;
    private ImageButton addButton;
    private ObjectAdder<T> objectAdder;

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

    public ObjectComboField(String label, Class<T> objectClass, String condition, String orderBy, boolean any,
                            boolean allowAdd) {
        this(label, new ObjectCacheList<>(objectClass, condition, orderBy, any), allowAdd);
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
        this(label, new ObjectMemoryList<>(ObjectListField.checkClass(objectClass, list), true), allowAdd);
        objectProvider.load(list);
    }

    protected ObjectComboField(String label, ObjectList<T> objectCache, boolean allowAdd) {
        super(label);
        this.objectProvider = new ObjectListProvider<>(objectCache);
        setItems(objectProvider);
        setItemLabelGenerator(StoredObject::toDisplay);
        if(allowAdd && !isAllowAny()) {
            ObjectField.checkDetailClass(this.objectProvider.getObjectClass(), label);
            addButton = new ImageButton("Add new", VaadinIcon.PLUS, e -> addNew());
            addButton.getElement().setAttribute("slot", "prefix");
            getElement().appendChild(addButton.getElement());
        }
        ResourceDisposal.register(this);
        setSpellCheck(false);
    }

    private void addNew() {
        if(objectAdder == null) {
            objectAdder = ObjectAdder.create(o -> {
                objectProvider.getData().add(o);
                objectProvider.refreshAll();
            },this);
        }
        getElement().callJsFunction("close").then(r -> objectAdder.add());
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
    public void load(ObjectIterator<T> objects) {
        clear();
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

    /**
     * Set the filter predicate.
     *
     * @param predicate Filter predicate.
     * @deprecated Please use {@link #setFilter(Predicate)} instead.
     */
    @Deprecated
    public void filter(Predicate<T> predicate) {
        ObjectInput.super.setViewFilter(predicate);
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

    public static <C extends StoredObject> boolean lessRows(Class<C> objectClass, boolean allowAny) {
        return (StoredObjectUtility.hints(objectClass) & 2) == 2
                && StoredObjectUtility.howBig(objectClass, allowAny) < 30;
    }

    @Override
    public void reload() {
        T v = getValue();
        load();
        if(!canContain(v)) {
            clear();
        }
    }
}