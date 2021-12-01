package com.storedobject.ui;

import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectAdder;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.ImageButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.icon.VaadinIcon;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

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
        this(label, new ObjectMemoryList<>(checkClass(objectClass, list)), allowAdd);
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
            addToPrefix(addButton);
        }
        ResourceDisposal.register(this);
        setSpellCheck(false);
    }

    private static <O extends StoredObject> Class<O> checkClass(Class<O> objectClass, List<O> list) {
        if(objectClass == null) {
            if(list != null && !list.isEmpty()) {
                //noinspection unchecked
                objectClass = (Class<O>) list.get(0).getClass();
            }
        }
        if(objectClass == null) {
            throw new SORuntimeException("Can't determine Object's class!");
        }
        return objectClass;
    }

    private void addNew() {
        if(objectAdder == null) {
            objectAdder = ObjectAdder.create(objectProvider,this);
        }
        getElement().callJsFunction("close").then(r -> objectAdder.add());
    }

    @Override
    public final AutoCloseable getResource() {
        return objectProvider.getResource();
    }

    @Override
    public ObjectListProvider<T> getObjectLoader() {
        return objectProvider;
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        ViewFilter<T> viewFilter = objectProvider.getViewFilter();
        viewFilter.setObjectConverter(itemLabelGenerator);
        super.setItemLabelGenerator(itemLabelGenerator);
    }

    @Override
    public void load(String condition, String orderBy) {
        objectProvider.load(condition, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        objectProvider.load(linkType, master, condition, orderBy);
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return objectProvider.getLoadFilter();
    }

    @Override
    public void applyFilter() {
        objectProvider.applyFilter();
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
        super.setFilter(filterClause);
    }

    @Override
    public boolean isAllowAny() {
        return objectProvider.isAllowAny();
    }
}