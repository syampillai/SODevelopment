package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;

public class AssemblyDataPickup<T extends InventoryItem, C extends InventoryItem> extends Assembly<T, C> {

    private static final String REF = "Initial assembly";
    public AssemblyDataPickup(Class<T> itemClass) {
        this(null, null, itemClass, null);
    }

    public AssemblyDataPickup(T item) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), null);
    }

    public AssemblyDataPickup(InventoryStore store, Class<T> itemClass) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, null);
    }

    public AssemblyDataPickup(InventoryLocation location, Class<T> itemClass) {
        this(location, null, itemClass, null);
    }

    public AssemblyDataPickup(Class<T> itemClass, Class<C> componentClass) {
        this(null, null, itemClass, componentClass);
    }

    public AssemblyDataPickup(T item, Class<C> componentClass) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), componentClass);
    }

    public AssemblyDataPickup(InventoryStore store, Class<T> itemClass, Class<C> componentClass) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, componentClass);
    }

    public AssemblyDataPickup(InventoryLocation location, Class<T> itemClass, Class<C> componentClass) {
        this(location, null, itemClass, componentClass);
    }

    AssemblyDataPickup(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass) {
        super(location, item, itemClass, componentClass);
        setCaption("Inventory Assembly - Data Pick-up");
    }

    public AssemblyDataPickup(String itemClass) {
        //noinspection unchecked
        this((Class<T>) ParameterParser.itemClass(itemClass), (Class<C>) ParameterParser.itemClass(1, itemClass));
    }

    @Override
    boolean canSet(InventoryItem item) {
        if(!item.wasDataPicked()) {
            warning("Not from the initial inventory: " + item.toDisplay());
            return false;
        }
        return true;
    }

    @Override
    FitItem createFitItem(Class<C> itemClass) {
        return new ItemFit(itemClass);
    }

    @Override
    RemoveItem createRemoveItem() {
        return new ItemRemove();
    }

    private class ItemFit extends AbstractAssemblyFit {

        private final MoneyField costField = new MoneyField("Cost");
        private final BooleanField previousData = new BooleanField("Select from Previously Picked-up Data");
        private final TextField snField = new TextField("Serial Number");
        @SuppressWarnings("rawtypes")
        private ObjectEditor editor;

        public ItemFit(Class<C> itemClass) {
            super(itemClass);
            setButtonsAtTop(true);
            addField(partNumbersField, previousData, snField, (HasValue<?, ?>) itemField,
                    requiredQuantityField, availableQuantityField, toFitQuantityField, costField);
            trackValueChange(previousData);
            InventoryLocation location = locationField.getValue();
            if(location instanceof InventoryBin) {
                itemField.setStore(((InventoryBin) location).getStore());
            } else {
                itemField.setLocation(locationField);
            }
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.isFromClient() && changedValues.getChanged() == partNumbersField) {
                itemField.fixPartNumber(partNumbersField.getValue());
                return;
            }
            if(changedValues.getChanged() == itemField) {
                InventoryItem item = itemValueChanged();
                if(item != null) {
                    costField.setLabel("Cost of " + item.getQuantity());
                    costField.setValue(item.getCost());
                }
                return;
            }
            if(changedValues.getChanged() == previousData) {
                boolean p = previousData.getValue();
                itemField.setEnabled(p);
                snField.setEnabled(!p);
                costField.setLabel("Unit Cost");
                costField.setEnabled(!p);
                costField.setValue(itemType.getUnitCost());
                if(p) {
                    snField.focus();
                } else {
                    itemField.focus();
                }
            }
        }

        @Override
        protected boolean process() {
            Quantity qFit = toFitQuantityField.getValue();
            if(qFit.isGreaterThan(quantityRequired)) {
                warning("Only " + quantityRequired + " is required");
                toFitQuantityField.focus();
                return false;
            }
            if(inventoryTransaction == null) {
                inventoryTransaction = InventoryTransaction.forDataPickup(getTransactionManager(), REF);
            } else {
                inventoryTransaction.abandon();
            }
            if(previousData.getValue()) {
                item = itemField.getValue();
                if(item == null) {
                    warning("Select item");
                    itemField.focus();
                    return false;
                }
                if(!item.wasDataPicked(1)) {
                    warning("This item doesn't belong to previously picked-up data");
                    return false;
                }
                if(!item.isSerialized()) {
                    if(qFit.isGreaterThan(item.getQuantity())) {
                        warning("Only " + item.getQuantity() + " is available, can't take out " + qFit);
                        toFitQuantityField.focus();
                        return false;
                    }
                }
                inventoryTransaction.moveTo(item, qFit, reference(null), fitmentPosition);
                if(transact(t -> inventoryTransaction.save(t))) {
                    refresh();
                    return true;
                }
                return false;
            }
            String sn = snField.getValue().trim();
            if(itemType.isSerialized()) {
                if(sn.isEmpty()) {
                    warning("Please enter Serial Number of the item");
                    return false;
                }
                if((item = InventoryItem.listStock(itemType, sn).findFirst()) != null) {
                    warning("Item already exists at location: " + item.getLocation().toDisplay());
                    return false;
                }
            }
            item = partNumbersField.getValue().createItem();
            item.setSerialNumber(sn);
            item.setQuantity(qFit);
            item.setCost(costField.getValue().multiply(qFit));
            inventoryTransaction.dataPickup(item, locationField.getValue(), fitmentPosition);
            if(editor != null && editor.getObjectClass() != item.getClass()) {
                editor = null;
            }
            if(editor == null) {
                editor = ObjectEditor.create(item.getClass());
                editor.setCaption("Item Details");
                editor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
            }
            //noinspection unchecked
            editor.setSaver(e -> moveItem());
            close();
            //noinspection unchecked
            editor.editObject(item, AssemblyDataPickup.this.getView());
            return true;
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity quantityAlreadyFitted) {
            super.setAssemblyPosition(fitmentPosition, quantityAlreadyFitted);
            availableQuantityField.setValue(quantityRequired.zero());
            toFitQuantityField.setEnabled(!itemType.isSerialized());
            toFitQuantityField.setValue(itemType.isSerialized() ? Count.ONE : quantityRequired.zero());
            previousData.setValue(false);
            snField.setValue("");
            costField.setLabel("Unit Cost");
            costField.setValue(itemType.getUnitCost());
        }
    }

    private class ItemRemove extends AbstractItemRemove {

        private final QuantityField qToRemoveField = new QuantityField("Quantity to Remove");

        public ItemRemove() {
            addField(itemField, fittedQuantityField, qToRemoveField);
            fittedQuantityField.setEnabled(false);
            setRequired(remarksField);
            setRequired(qToRemoveField);
        }

        @Override
        protected boolean process() {
            Quantity qToRemove = qToRemoveField.getValue();
            if(!item.isSerialized()) {
                if(qToRemove.isGreaterThan(item.getQuantity())) {
                    warning("Fitted quantity is only " + item.getQuantity() + ", can't remove " + qToRemove);
                    return false;
                }
            }
            if(inventoryTransaction == null) {
                inventoryTransaction = InventoryTransaction.forDataPickup(getTransactionManager(), REF);
            } else {
                inventoryTransaction.abandon();
            }
            inventoryTransaction.thrash(item, qToRemove, reference(null));
            if(transact(t -> inventoryTransaction.save(t))) {
                refresh();
                return true;
            }
            return false;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(item.wasDataPicked(1)) {
                super.execute(parent, doNotLock);
            } else {
                warning("This item is not from initial inventory");
            }
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition) {
            super.setAssemblyPosition(fitmentPosition);
            Quantity q = item.getQuantity();
            qToRemoveField.setValue(q);
            qToRemoveField.setEnabled(!item.isSerialized() && !item.isConsumable());
        }
    }
}
