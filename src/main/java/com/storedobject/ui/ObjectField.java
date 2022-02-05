package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.inventory.*;
import com.storedobject.ui.util.NoDisplayField;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewDependent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.ItemLabelGenerator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A field that can be used to accept {@link Id} values. It has an "internal field" of type {@link ObjectInput}
 * that actually accepts the object values.
 *
 * @param <T> Type of objects accepted. Only {@link Id} values of this type are valid.
 * @author Syam
 */
public class ObjectField<T extends StoredObject> extends CustomField<Id>
        implements IdInput<T>, ViewDependent, NoDisplayField {

    /**
     * Type of the object field.
     *
     * @author Syam
     */
    public enum Type {
        /**
         * Determine automatically.
         */
        AUTO,
        /**
         * Choice type.
         */
        CHOICE,
        /**
         * Object "get" type. (See {@link ObjectGetField}).
         */
        GET,
        /**
         * Search type. (See {@link ObjectSearchField}).
         */
        SEARCH,
        /**
         * Form type. (See {@link ObjectFormField}).
         */
        FORM,
        /**
         * Image type. (Used for uploading images).
         */
        IMAGE,
        /**
         * File type. (Used for uploading files).
         */
        FILE,
        /**
         * Inventory item type.
         */
        INVENTORY_ITEM,
        /**
         * Inventory bin type.
         */
        INVENTORY_BIN,
        /**
         * Form type - embedded as a block. (See {@link ObjectFormField}).
         */
        FORM_BLOCK,
        /**
         * Video type. (Used for uploading video files).
         */
        VIDEO,
        /**
         * Audio type. (Used for uploading audio files).
         */
        AUDIO,
        /**
         * Still-camera type. (Used for capturing an image via computer's camera).
         */
        STILL_CAMERA,
        /**
         * Video-camera type. (Used for capturing a video via computer's video camera).
         */
        VIDEO_CAMERA,
        /**
         * Microphone type. (Used for capturing an audio via computer's microphone).
         */
        MIC
    }
    private final Class<T> objectClass;
    private final boolean any;
    private final ObjectInput<T> field;
    private T cached;

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     */
    public ObjectField(Class<T> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param type Desired type of the field.
     */
    public ObjectField(Class<T> objectClass, Type type) {
        this(null, objectClass, type);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     */
    public ObjectField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param type Desired type of the field.
     */
    public ObjectField(String label, Class<T> objectClass, Type type) {
        this(label, objectClass, false, type);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     */
    public ObjectField(Class<T> objectClass, boolean any) {
        this(null, objectClass, any, Type.AUTO);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     */
    public ObjectField(String label, Class<T> objectClass, boolean any) {
        this(label, objectClass, any, Type.AUTO);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     */
    public ObjectField(Class<T> objectClass, boolean any, Type type) {
        this(null, objectClass, any, type);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     */
    public ObjectField(String label, Class<T> objectClass, boolean any, Type type) {
        this(label, objectClass, any, type, null, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param addAllowed Whether new object instances can be added via this field or not. (This is possible
     *                   only in certain types).
     */
    public ObjectField(Class<T> objectClass, boolean any, boolean addAllowed) {
        this(null, objectClass, any, Type.AUTO, addAllowed);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param addAllowed Whether new object instances can be added via this field or not. (This is possible
     *                   only in certain types).
     */
    public ObjectField(String label, Class<T> objectClass, boolean any, boolean addAllowed) {
        this(label, objectClass, any, Type.AUTO, addAllowed);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     * @param addAllowed Whether new object instances can be added via this field or not. (This is possible
     *                   only in certain types).
     */
    public ObjectField(Class<T> objectClass, boolean any, Type type, boolean addAllowed) {
        this(null, objectClass, any, type, addAllowed);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param type Desired type of the field.
     * @param addAllowed Whether new object instances can be added via this field or not. (This is possible
     *                   only in certain types).
     */
    public ObjectField(String label, Class<T> objectClass, boolean any, Type type, boolean addAllowed) {
        this(label, objectClass, any, type, null, addAllowed);
    }

    /**
     * Constructor.
     *
     * @param list Values will be allowed for this list only.
     */
    public ObjectField(List<T> list) {
        this(null, list);
    }

    /**
     * Constructor.
     *
     * @param list Values will be allowed for this list only.
     */
    public ObjectField(Iterable<T> list) {
        this(null, list);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param list Values will be allowed for this list only.
     */
    public ObjectField(String label, Iterable<T> list) {
        this(label, list(list));
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param any Whether subclasses should be allowed or not.
     * @param getProvider "Get" provider for searching the object instances.
     * @param addAllowed Whether new object instances can be added via this field or not. (This is possible
     *                   only in certain types).
     */
    protected ObjectField(String label, Class<T> objectClass, boolean any, ObjectGetField.GetProvider<T> getProvider, boolean addAllowed) {
        this(label, objectClass, any, createField(objectClass, Type.GET, any, getProvider, addAllowed));
    }

    private ObjectField(String label, Class<T> objectClass, boolean any, Type type, ObjectGetField.GetProvider<T> getProvider, boolean addAllowed) {
        this(label, objectClass, any, createField(objectClass, type, any, getProvider, addAllowed));
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param list Values will be allowed for this list only.
     */
    public ObjectField(String label, List<T> list) {
        this(label, list, false);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param list Values will be allowed for this list only.
     * @param any Whether subclasses should be allowed or not.
     */
    @SuppressWarnings("unchecked")
    public ObjectField(String label, List<T> list, boolean any) {
        this(label, (Class<T>)list.get(0).getClass(), any, new ObjectComboField<>(list));
    }

    /**
     * Constructor.
     *
     * @param field Object input field to be used internally.
     */
    public ObjectField(ObjectInput<T> field) {
        this(field.getLabel(), field);
        field.setLabel(null);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param field Object input field to be used internally.
     */
    public ObjectField(String label, ObjectInput<T> field) {
        this(label, field.getObjectClass(), field.isAllowAny(), field);
    }

    private ObjectField(String label, Class<T> objectClass, boolean any, ObjectInput<T> field) {
        super(null);
        this.objectClass = objectClass;
        this.any = any;
        this.field = field;
        field.setLabel(null);
        if(field instanceof HasSize) {
            ((HasSize) field).setWidth(null);
        }
        if(canDisplay()) {
            if(field instanceof HasSize) {
                ((HasSize) field).setWidthFull();
            }
            add((Component)field);
            this.setLabel(label);
        }
        if(field instanceof AbstractObjectField) {
            ((AbstractObjectField<T>) field).addValueChangeListener(e -> setModelValue(field.getObjectId(), true));
        } else if(field instanceof ObjectComboField) {
            ((ObjectComboField<T>) field).addValueChangeListener(e -> setModelValue(field.getObjectId(), true));
        }
        T object = field.getValue();
        if(object != null) {
            setObject(object);
        }
    }

    /**
     * Set a predicate that will be used for filtering the object after loading is done.
     *
     * @param predicate Filter to apply after loaded.
     * @deprecated Please use {@link #setViewFilter(Predicate)} instead.
     */
    @Deprecated
    public void filter(Predicate<T> predicate) {
        setViewFilter(predicate);
    }

    public void setFilter(Predicate<T> predicate) {
        setViewFilter(predicate);
    }

    /**
     * Set a filter predicate that will be used while loading.
     *
     * @param loadFilter Filter to apply while loading.
     */
    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        field.setLoadFilter(loadFilter);
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter, boolean apply) {
        field.setLoadFilter(loadFilter, apply);
    }

    private static <O extends StoredObject> List<O> list(Iterable<O> iterable) {
        ArrayList<O> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    static void checkDetailClass(Class<?> oClass, String label) {
        if(Detail.class.isAssignableFrom(oClass)) {
            String m = oClass.getName();
            if(label != null) {
                m += " (" + label + ")";
            }
            throw new Design_Error(null, "Can not create new instance of the Detail class: " + m);
        }
    }

    @Override
    public void focus() {
        field.focus();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if(field instanceof HasSize) {
            ((HasSize) field).setWidth(width);
        }
    }

    @Override
    public void setWidthFull() {
        super.setWidthFull();
        if(field instanceof HasSize) {
            ((HasSize) field).setWidthFull();
        }
    }

    @Override
    public void setMaxWidth(String width) {
        super.setMaxWidth(width);
        if(field instanceof HasSize) {
            ((HasSize) field).setMaxWidth(width);
        }
    }

    @Override
    public void setMinWidth(String width) {
        super.setMinWidth(width);
        if(field instanceof HasSize) {
            ((HasSize) field).setMinWidth(width);
        }
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        field.setInternalLabel(label);
    }

    @Override
    public boolean canDisplay() {
        if(field instanceof NoDisplayField) {
            return ((NoDisplayField) field).canDisplay();
        }
        return true;
    }

    @Override
    protected void updateValue() {
        if(field instanceof ObjectFormField) {
            return;
        }
        super.updateValue();
    }

    @Override
    public boolean isInvalid() {
        return field instanceof HasValidation && ((HasValidation) field).isInvalid();
    }

    @Override
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
    public void applyFilter() {
        field.applyFilter();
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return field.getLoadFilter();
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
        if((StoredObjectUtility.hints(objectClass) & 2) == 2 && StoredObjectUtility.howBig(objectClass, any) < 16) {
            return Type.CHOICE;
        }
        boolean tryGet = true;
        if(type == Type.GET && !ObjectGetField.canCreate(objectClass)) {
            type = Type.AUTO;
            tryGet = false;
        }
        if (type != Type.AUTO) {
            return type;
        }
        if(tryGet && ObjectGetField.canCreate(objectClass)) {
            return Type.GET;
        }
        return Type.SEARCH;
    }

    @SuppressWarnings("unchecked")
    private static <O extends StoredObject, IT extends InventoryItemType> ObjectInput<O> createField(
            Class<O> objectClass, Type type, boolean any, ObjectGetField.GetProvider<O> getProvider,
            boolean addAllowed) {
        if(StreamData.class.isAssignableFrom(objectClass)) {
            return (ObjectInput<O>) new FileField(type);
        }
        if(type == Type.AUTO && InventoryItemType.class.isAssignableFrom(objectClass) && getProvider == null) {
            if(ObjectComboField.lessRows(objectClass, any)) {
                return new ObjectComboField<>(objectClass, any, addAllowed);
            }
            Class<IT> itClass = (Class<IT>) objectClass;
            String pn;
            try {
                pn = itClass.getDeclaredConstructor().newInstance().getPartNumberShortName();
            } catch(Throwable e) {
                pn = "P/N";
            }
            ItemTypeGetField<IT> itField = new ItemTypeGetField<>(itClass, any, addAllowed);
            itField.setPlaceholder(pn);
            return (ObjectInput<O>) itField;
        }
        if(InventoryItem.class.isAssignableFrom(objectClass)) {
            Class<? extends InventoryItem> iClass = (Class<? extends InventoryItem>) objectClass;
            return (ObjectInput<O>) ItemInput.create(iClass, any);
        }
        if(InventoryBin.class.isAssignableFrom(objectClass)) {
            return (ObjectInput<O>) new BinField();
        }
        type = type(type, objectClass, any);
        return switch(type) {
            case GET -> new ObjectGetField<>(null, objectClass, any, addAllowed, getProvider);
            case CHOICE -> new ObjectComboField<>(objectClass, any, addAllowed);
            case FORM_BLOCK, FORM -> new ObjectFormField<>(objectClass, type);
            default -> new ObjectSearchField<>(objectClass, any, addAllowed);
        };
    }

    /**
     * Get the internal field.
     *
     * @return The internal field that is accepting the object values.
     */
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

    /**
     * Get the type of the internal field.
     *
     * @return Type of the internal field.
     */
    public Type getType() {
        if(field instanceof FileField) {
            return Type.FILE;
        }
        if(field instanceof ItemField || field instanceof ItemGetField) {
            return Type.INVENTORY_ITEM;
        }
        if(field instanceof BinField) {
            return Type.INVENTORY_BIN;
        }
        if(field instanceof ObjectGetField) {
            return Type.GET;
        }
        if(field instanceof ObjectComboField) {
            return Type.CHOICE;
        }
        if(field instanceof ObjectFormField) {
            return ((ObjectFormField<T>) field).formType();
        }
        return Type.SEARCH;
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        field.setItemLabelGenerator(itemLabelGenerator);
    }

    @Override
    public Id getValue() {
        if(field instanceof ObjectFormField) {
            if(((ObjectFormField<T>) field).fromClient){
                setModelValue(field.getObjectId(), true);
            }
            return field.getObjectId();
        }
        return super.getValue();
    }

    @Override
    public void setDependentView(View dependent) {
        if(field instanceof ViewDependent) {
            ((ViewDependent) field).setDependentView(dependent);
            T o = field.getValue();
            Id id = o == null ? null : o.getId();
            setValue(id);
        }
    }

    @Override
    public void setValue(Id value) {
        if(field instanceof ObjectFormField) {
            field.setValue(value);
        } else {
            super.setValue(value);
        }
    }

    @Override
    public void setValue(T object) {
        if(field instanceof ObjectFormField) {
            field.setValue(object);
        } else {
            IdInput.super.setValue(object);
        }
    }

    @Override
    public void setObject(Id objectId) {
        if(field instanceof ObjectFormField) {
            field.setValue(objectId);
        } else {
            IdInput.super.setObject(objectId);
        }
    }

    @Override
    public void setObject(StoredObject object) {
        if(field instanceof ObjectFormField) {
            T v = convert(object);
            if(v != null) {
                setCached(v);
            }
            field.setValue(v);
        } else {
            IdInput.super.setObject(object);
        }
    }

    @Override
    public View getDependentView() {
        return field instanceof ViewDependent ? ((ViewDependent) field).getDependentView() : null;
    }

    /**
     * Load allowed values from a list. Once invoked, only this list will be used for showing the allowed objects
     * that can be selected via this field.
     *
     * @param objects Objects to load.
     */
    public void load(ObjectIterator<T> objects) {
        if(objects == null) {
            objects = ObjectIterator.create();
        }
        getField().load(objects);
    }

    /**
     * Reload the allowed values by applying newly set filters.
     */
    public void reload() {
       getField().reload();
    }
}