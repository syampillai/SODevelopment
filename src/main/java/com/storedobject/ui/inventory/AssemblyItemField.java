package com.storedobject.ui.inventory;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.vaadin.CustomField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Field to accept an item that is fitted on an assembly.
 *
 * @param <I> Type of inventory item to accept.
 * @author Syam
 */
public class AssemblyItemField<I extends InventoryItem> extends CustomField<I> implements ItemInput<I> {

    private InventoryItem assemblyParent;
    private final Class<?> typeClass;
    private final PNField<?> pnField;
    private final ObjectList<I> items;
    private final ObjectComboField<I> itemCombo;
    private boolean pnVisible = false;

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public AssemblyItemField(Class<I> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public AssemblyItemField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public AssemblyItemField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public AssemblyItemField(String label, Class<I> objectClass, boolean allowAny) {
        this(label, new ObjectMemoryList<>(objectClass, allowAny));
    }

    private <IT extends InventoryItemType> AssemblyItemField(String label, ObjectMemoryList<I> listProvider) {
        super(null);
        itemCombo = new ObjectComboField<>(listProvider.getObjectClass(), listProvider, false);
        items = new ObjectCacheList<>(listProvider.getObjectClass(), listProvider.isAllowAny());
        typeClass = typeClass();
        @SuppressWarnings("unchecked") Class<IT> pnType = (Class<IT>) typeClass;
        pnField = new PNField<>(pnType, listProvider.getAllowAny());
        pnField.setVisible(false);
        add(pnField, itemCombo);
        setLabel(label);
        setItemLabelGenerator(InventoryItem::getSerialNumber);
        itemCombo.addValueChangeListener(e -> setDetail(e.getValue()));
        itemCombo.setPlaceholder(getSNName());
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void fixPartNumber(InventoryItemType partNumber) {
    }

    private Class<? extends InventoryItemType> typeClass() {
        try {
            return getObjectClass().getDeclaredConstructor().newInstance().getItemType();
        } catch(Throwable ignored) {
        }
        return InventoryItemType.class;
    }

    private String getSNName() {
        try {
            return getObjectClass().getDeclaredConstructor().newInstance().getSerialNumberShortName();
        } catch(Throwable ignored) {
        }
        return "S/N";
    }

    /**
     * This method doesn't have any effect.
     * @param storeField Any provider that can supply an {@link InventoryStore} instance.
     */
    @Override
    public void setStore(ObjectProvider<? extends InventoryStore> storeField) {
    }

    /**
     * This method doesn't have any effect.
     * @param store An instance of {@link InventoryStore}.
     */
    @Override
    public void setStore(InventoryStore store) {
    }

    /**
     * This method doesn't have any effect.
     * @param locationField Any provider that can supply an {@link InventoryLocation} instance.
     */
    @Override
    public void setLocation(ObjectProvider<? extends InventoryLocation> locationField) {
    }

    /**
     * This method doesn't have any effect.
     * @param location An instance of an {@link InventoryLocation}.
     */
    @Override
    public void setLocation(InventoryLocation location) {
    }

    /**
     * This method doesn't have any effect.
     * @param extraFilterProvider Extra filter to be set.
     */
    @Override
    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
    }

    @Override
    public ObjectProvider<? extends InventoryItemType> getPartNumberProvider() {
        return pnField;
    }

    /**
     * Set the parent assembly.
     *
     * @param assemblyParent Parent assembly to set.
     * @param <IT> The item type.
     */
    public <IT extends InventoryItemType> void setAssembly(InventoryItem assemblyParent) {
        if(assemblyParent == null || Objects.equals(assemblyParent.getId(),
                this.assemblyParent == null ? null : this.assemblyParent.getId())) {
            return;
        }
        this.assemblyParent = assemblyParent;
        items.load(assemblyParent.listAssemblies(getObjectClass()));
        @SuppressWarnings("unchecked") PNField<IT> pnF = (PNField<IT>) pnField;
        @SuppressWarnings("unchecked") Class<IT> pnType = (Class<IT>) typeClass;
        ArrayListSet<Id> itemTypes = new ArrayListSet<>();
        items.streamAll(0, Integer.MAX_VALUE).map(InventoryItem::getPartNumberId).forEach(itemTypes::add);
        if(itemTypes.isEmpty()) {
            pnF.setVisible(false);
            pnVisible = false;
            itemCombo.load(ObjectIterator.create());
        } else {
            pnVisible = true;
            pnField.setVisible(!isReadOnly());
            pnF.load(ObjectIterator.create(itemTypes, id -> StoredObject.get(pnType, id, isAllowAny())));
            if(pnField.isVisible()) {
                pnF.focus();
            }
        }
    }

    @Override
    public void setInternalLabel(String label) {
        itemCombo.setInternalLabel(label);
    }

    @Override
    public String getInternalLabel() {
        return itemCombo.getInternalLabel();
    }

    @Override
    public void load(ObjectIterator<I> objects) {
        itemCombo.load(objects);
    }

    @Override
    public void reload() {
        itemCombo.reload();
    }

    @Override
    public Class<I> getObjectClass() {
        return itemCombo.getObjectClass();
    }

    @Override
    public I getCached() {
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
    public void setDisplayDetail(Consumer<I> displayDetail) {
    }

    @Override
    public Consumer<I> getDisplayDetail() {
        return null;
    }

    @Override
    public void setPrefixFieldControl(boolean prefixFieldControl) {
    }

    @Override
    public void applyFilter() {
        itemCombo.applyFilter();
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<I> getLoadFilter() {
        return itemCombo.getLoadFilter();
    }

    @Override
    protected I generateModelValue() {
        return itemCombo.getValue();
    }

    @Override
    protected void setPresentationValue(I item) {
        Id typeId = pnField.getValue();
        if((Id.isNull(typeId) && item == null) || (item != null && item.getPartNumberId().equals(typeId))) {
            return;
        }
        pnField.setValue(item == null ? null : item.getPartNumberId());
        setDetail(item);
    }

    private void setDetail(I item) {
        String name;
        if(item == null) {
            InventoryItemType it = pnField.getObject();
            name = it == null ? "" : it.toDisplay();
        } else {
            name = item.toDisplay();
        }
        setHelperText(name);
    }

    private class PNField<T extends InventoryItemType> extends ItemField.ItemTypeField<T> {

        public PNField(Class<T> objectClass, boolean allowAny) {
            super(objectClass, allowAny);
            setDisplayDetail(pn -> AssemblyItemField.this.setHelperText(pn == null ? "" : pn.toDisplay()));
            addValueChangeListener(e -> pn(getObject()));
        }

        private void pn(InventoryItemType pn) {
            if(pn == null) {
                itemCombo.clear();
                itemCombo.load(ObjectIterator.create());
                if(isVisible()) {
                    focus();
                }
                return;
            }
            Id pnId = pn.getId();
            itemCombo.load(items.stream().filter(i -> i.getPartNumberId().equals(pnId)));
            if(isVisible() && isEnabled()) {
                itemCombo.focus();
            }
        }
    }

    @Override
    public I getValue() {
        return itemCombo.getValue();
    }

    @Override
    public I getObject() {
        return itemCombo.getObject();
    }

    @Override
    public Id getObjectId() {
        I i = getValue();
        return i == null ? null : i.getId();
    }

    @Override
    public boolean isAllowAny() {
        return itemCombo.isAllowAny();
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<I> itemLabelGenerator) {
        itemCombo.setItemLabelGenerator(itemLabelGenerator);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        itemCombo.setReadOnly(readOnly);
        pnField.setVisible(!readOnly && isEnabled() && pnVisible);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        pnField.setVisible(!isReadOnly() && enabled && pnVisible);
    }
}
