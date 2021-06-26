package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

import java.util.List;
import java.util.function.Consumer;

/**
 * Field to accept an instance of the {@link InventoryItem} with provision to input part number.
 *
 * @param <I> Type of inventory item to accept.
 * @author Syam
 */
public class ItemField<I extends InventoryItem> extends ObjectGetField<I> implements ItemInput<I> {

    final static StringList COLUMNS = StringList.create(
            "PartNumber.Name AS Item",
            "PartNumber.PartNumber AS Part/Model Number",
            "SerialNumberDisplay AS Serial/Batch",
            "Quantity", "InTransit", "Serviceable", "LocationDisplay as Location", "Owner");
    private final ItemTypeField<?> typeField;
    private ObjectProvider<? extends InventoryStore> storeField;
    private ObjectProvider<? extends InventoryLocation> locationField;
    private FilterProvider extraFilterProvider;
    private boolean pnEnabled = false;
    private final InventoryFilterProvider filterProvider;

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public ItemField(Class<I> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public ItemField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ItemField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ItemField(String label, Class<I> objectClass, boolean allowAny) {
        super(objectClass, allowAny);
        super.setDisplayDetail(this::displayDetail);
        typeField = new ItemTypeField<>(typeClass(), allowAny, null);
        setFilter(filterProvider = new InventoryFilterProvider());
        setLabel(label);
    }

    /**
     * Fix the part number (an instance of {@link InventoryItemType}). If a non-null value is set,
     * "part number" portion will be locked with that value. A <code>null</code> value may be set later
     * to unlock it.
     *
     * @param partNumber Part number to set.
     */
    public void fixPartNumber(InventoryItemType partNumber) {
        typeField.setObject(partNumber);
        pnEnabled = partNumber == null;
        typeField.setEnabled(pnEnabled);
    }

    /**
     * Set the part number (an instance of {@link InventoryItemType}) portion.
     *
     * @param partNumber Part number to set.
     */
    public void setPartNumber(InventoryItemType partNumber) {
        typeField.setObject(partNumber);
    }

    /**
     * Get the current value of the part number.
     *
     * @return Current value of the part number. It may be <code>null</code>.
     */
    public InventoryItemType getPartNumber() {
        return typeField.getObject();
    }

    /**
     * Get {@link Id} of the current part number value.
     *
     * @return {@link Id} of the current of the part number value. It may be <code>null</code>.
     */
    public Id getPartNumberId() {
        return typeField.getValue();
    }

    @Override
    protected ButtonLayout initComponent() {
        ButtonLayout c = super.initComponent();
        c.removeAll();
        c.add(typeField, 0);
        c.add(getPrefixComponent(), getDetailComponent());
        getSearchField().setPlaceholder(getSNName());
        getSearchField().addFocusListener(e -> {
            if(typeField.getValue() == null) {
                typeField.focus();
            }
        });
        return c;
    }

    private String getSNName() {
        try {
            return getObjectClass().getDeclaredConstructor().newInstance().getSerialNumberShortName();
        } catch(Throwable ignored) {
        }
        return "S/N";
    }

    @Override
    protected void setPresentationValue(I value) {
        super.setPresentationValue(value);
        if(value != null) {
            getSearchField().setValue(value.getSerialNumber());
            if (!value.getPartNumberId().equals(typeField.getObjectId())) {
                typeField.setValue(value.getPartNumberId());
            }
        } else {
            typeField.focus();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        typeField.setReadOnly(readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        typeField.setEnabled(enabled && pnEnabled);
    }

    @Override
    protected GetProvider<I> createGetProvider() {
        return new InventoryGetProvider();
    }

    private Class<? extends InventoryItemType> typeClass() {
        try {
            return getObjectClass().getDeclaredConstructor().newInstance().getItemType();
        } catch(Throwable ignored) {
        }
        return InventoryItemType.class;
    }

    private void displayDetail(I item) {
        if(item != null) {
            detailValue(item.toDisplay());
            return;
        }
        displayDetail(typeField.getObject());
    }

    private void displayDetail(InventoryItemType itemType) {
        if(itemType != null) {
            I item = getValue();
            if(item != null && item.getPartNumberId().equals(itemType.getId())) {
                displayDetail(item);
                return;
            }
        }
        detailValue(itemType == null ? " " : itemType.toDisplay());
    }

    private void detailValue(String text) {
        Component dc = getDetailComponent();
        if(dc instanceof HasText) {
            ((HasText) dc).setText(text);
        }
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
    }

    @Override
    public void setDisplayDetail(Consumer<I> displayDetail) {
    }

    @Override
    public void setPrefixFieldControl(boolean searchFieldControl) {
    }

    @Override
    public void setStore(ObjectProvider<? extends InventoryStore> storeField) {
        this.storeField = storeField;
    }

    @Override
    public void setStore(InventoryStore store) {
        this.storeField = store == null ? null : () -> store;
    }

    @Override
    public void setLocation(ObjectProvider<? extends InventoryLocation> locationField) {
        this.locationField = locationField;
    }

    @Override
    public void setLocation(InventoryLocation location) {
        this.locationField = location == null ? null : () -> location;
    }

    @Override
    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
        this.extraFilterProvider = extraFilterProvider;
    }

    @Override
    protected void doSearch() {
        if(Id.isNull(typeField.getValue())) {
            typeField.focus();
        } else {
            super.doSearch();
        }
    }

    @Override
    protected boolean doSearchLoadAll() {
        return doSearchLoadAll(true);
    }

    boolean doSearchLoadAll(boolean fromClient) {
        getSearcher().load();
        if(getSearcher().size() == 1) {
            I object = getSearcher().getItem(0);
            I current = getValue();
            if(!object.equals(current)) {
                setModelValue(object, fromClient);
                return true;
            }
        }
        return false;
    }

    @Override
    protected ObjectBrowser<I> createSearcher() {
        ObjectBrowser<I> s = ObjectBrowser.create(getObjectClass(), COLUMNS,
                EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0),
                null);
        s.setFilter(filterProvider);
        return s;
    }

    private class ItemTypeField<T extends InventoryItemType> extends ObjectField<T> {

        public ItemTypeField(Class<T> objectClass, boolean allowAny, GetProvider<T> getProvider) {
            super(null, objectClass, allowAny, getProvider, false);
            this.setDetailComponent(null);
            this.setDisplayDetail(ItemField.this::displayDetail);
            addValueChangeListener(e -> {
                InventoryItemType itemType = getObject();
                if(itemType == null) {
                    focus();
                } else {
                    sSet(itemType.getPartNumber());
                    doSearchLoadAll(e.isFromClient());
                    ItemField.this.getSearchField().focus();
                }
            });
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            setPlaceholder(getPNName());
        }

        private String getPNName() {
            try {
                return getObjectClass().getDeclaredConstructor().newInstance().getPartNumberShortName();
            } catch(Throwable ignored) {
            }
            return "P/N";
        }

        private void sSet(String text) {
            ObjectInput<T> field = getField();
            if(field instanceof ObjectGetField) {
                ((ObjectGetField<T>)field).getSearchField().setValue(text);
            }
        }
    }

    private class InventoryGetProvider extends GetTypedSupplier<I> implements ObjectConverter<I, I> {

        public InventoryGetProvider() {
            super(typeField, getObjectClass());
        }

        @Override
        public I getTextObject(SystemEntity systemEntity, String value) throws Exception {
            return convert(super.getTextObject(systemEntity, value));
        }

        @Override
        public ObjectIterator<I> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            return super.listTextObjects(systemEntity, value).convert(this);
        }

        @Override
        public I convert(I object) {
            if(object == null || (locationField == null && storeField == null)) {
                return object;
            }
            Id id;
            if(locationField != null) {
                id = locationField.getObjectId();
                if(Id.isNull(id) || !id.equals(object.getLocationId())) {
                    return null;
                }
                return object;
            }
            id = storeField.getObjectId();
            if(Id.isNull(id) || !id.equals(object.getStoreId())) {
                return null;
            }
            return object;
        }
    }

    private class InventoryFilterProvider implements FilterProvider {

        @Override
        public String getFilterCondition() {
            Id pnId = typeField.getValue();
            List<InventoryItemType> apns = InventoryItemType.listAPNs(pnId);
            final StringBuilder f = new StringBuilder("T.PartNumber");
            if(apns.isEmpty()) {
                f.append('=').append(pnId);
            } else {
                f.append(" IN (").append(pnId);
                apns.forEach(pn -> f.append(',').append(pn.getId()));
                f.append(')');
            }
            if(extraFilterProvider != null) {
                f.append(" AND (").append(extraFilterProvider.getFilterCondition()).append(')');
            }
            if(locationField != null) {
                f.append(" AND Location=").append(locationField.getObjectId());
            } else if(storeField != null) {
                f.append(" AND Store=").append(storeField.getObjectId());
            }
            f.append(" AND (Quantity).Quantity>0");
            return f.toString();
        }
    }
}
