package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectListField;
import com.storedobject.ui.QuantityField;
import com.storedobject.vaadin.ChangedValues;
import com.storedobject.vaadin.DateField;
import com.vaadin.flow.component.HasValue;

public class Assembly<T extends InventoryItem, C extends InventoryItem> extends AbstractAssembly<T, C> {

    public Assembly(Class<T> itemClass) {
        this(null, null, itemClass, null);
    }

    public Assembly(T item) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), null);
    }

    public Assembly(InventoryStore store, Class<T> itemClass) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, null);
    }

    public Assembly(InventoryLocation location, Class<T> itemClass) {
        this(location, null, itemClass, null);
    }

    public Assembly(Class<T> itemClass, Class<C> componentClass) {
        this(null, null, itemClass, componentClass);
    }

    public Assembly(T item, Class<C> componentClass) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), componentClass);
    }

    public Assembly(InventoryStore store, Class<T> itemClass, Class<C> componentClass) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, componentClass);
    }

    public Assembly(InventoryLocation location, Class<T> itemClass, Class<C> componentClass) {
        this(location, null, itemClass, componentClass);
    }

    Assembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass) {
        super(location, item, itemClass, componentClass);
        if(item == null) {
            Application a = Application.get();
            if(a != null) {
                String caption = a.getLogicTitle(null);
                if(caption != null) {
                    setCaption(caption);
                }
            }
        }
    }

    public Assembly(String itemClass) {
        //noinspection unchecked
        this((Class<T>) ParameterParser.itemClass(itemClass), (Class<C>) ParameterParser.itemClass(1, itemClass));
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

        private final DateField dateField = new DateField("Date");

        public ItemFit(Class<C> itemClass) {
            super(itemClass);
            itemField.setEnabled(true);
            addField(dateField, partNumbersField, (HasValue<?, ?>) itemField, requiredQuantityField,
                    availableQuantityField, toFitQuantityField);
            setRequired(remarksField);
            setRequired((HasValue<?, ?>) itemField);
            itemField.setStore(() -> {
                InventoryLocation loc = locationField.getValue();
                return loc instanceof InventoryBin ? ((InventoryBin) loc).getStore() : null;
            });
        }

        @Override
        protected boolean process() {
            return process(dateField.getValue(), reference(null));
        }

        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity quantityAlreadyFitted) {
            super.setAssemblyPosition(fitmentPosition, quantityAlreadyFitted);
            availableQuantityField.setValue(quantityRequired.zero());
            toFitQuantityField.setEnabled(!itemType.isSerialized());
            toFitQuantityField.setValue(itemType.isSerialized() ? Count.ONE : quantityRequired.zero());
            dateField.setValue(date);
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.isFromClient() && changedValues.getChanged() == partNumbersField) {
                itemField.fixPartNumber(partNumbersField.getValue());
                return;
            }
            if(changedValues.getChanged() == itemField) {
                itemValueChanged();
            }
        }
    }

    private class ItemRemove extends AbstractItemRemove {

        private final DateField dateField = new DateField("Date");
        private final QuantityField qToRemoveField = new QuantityField("Quantity to Remove");
        private final InventoryLocation scrap = new InventoryStoreBin(), specificBin = new InventoryStoreBin();
        private final ObjectListField<InventoryLocation> binField =
                new ObjectListField<>("Remove to", InventoryLocation.class);
        private boolean binPopulated = false;
        private final BinField specificBinField = new BinField("Specific Bin");

        public ItemRemove() {
            addField(dateField, itemField, fittedQuantityField, qToRemoveField, binField,
                    specificBinField);
            setFieldHidden(specificBinField);
            binField.addValueChangeListener(e -> {
               if(e.isFromClient()) {
                   if(e.getValue() == null) {
                       binField.focus();
                       return;
                   }
                   boolean v = e.getValue().equals(specificBin);
                   setFieldVisible(v, specificBinField);
                   if(v) {
                       specificBinField.focus();
                   }
               }
            });
            fittedQuantityField.setEnabled(false);
            setRequired(remarksField);
            setRequired(qToRemoveField);
            scrap.setName("<Scrap>");
            scrap.makeVirtual();
            specificBin.setName("<Specific Bin>");
            specificBin.makeVirtual();
            setRequired(binField);
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
            InventoryLocation location = binField.getValue();
            if(location == specificBin) {
                location = specificBinField.getValue();
                if(location == null) {
                    warning("Please select a specific bin");
                    return false;
                }
            }
            if(location == scrap && !item.isExpendable() && !item.isConsumable()) {
                warning("This sort of items can't be scrapped directly from the assembly");
                return false;
            }
            date = dateField.getValue();
            if(inventoryTransaction == null || !inventoryTransaction.getDate().equals(date)) {
                inventoryTransaction = new InventoryTransaction(getTransactionManager(), dateField.getValue());
            } else {
                inventoryTransaction.abandon();
            }
            if(location == scrap) {
                inventoryTransaction.scrap(item, qToRemove, reference(null));
            } else {
                inventoryTransaction.moveTo(item, qToRemove, reference(null), location);
            }
            if(transact(t -> inventoryTransaction.save(t))) {
                refresh();
                return true;
            }
            return false;
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition) {
            super.setAssemblyPosition(fitmentPosition);
            Quantity q = item.getQuantity();
            qToRemoveField.setValue(q);
            qToRemoveField.setEnabled(!item.isSerialized() && !item.isConsumable());
            dateField.setValue(date);
            if(!binPopulated) {
                binPopulated = true;
                InventoryLocation location = item.getRealLocation();
                if(location instanceof InventoryBin) {
                    InventoryStore store = ((InventoryBin) location).getStore();
                    specificBinField.setStore(store);
                    if(!(location instanceof InventoryStoreBin)) {
                        location = store.getStoreBin();
                        specificBinField.setStore(store);
                    }
                }
                binField.setItems(location, scrap, location instanceof InventoryStoreBin ? specificBin : null);
            }
            if(item.isConsumable()) {
                binField.setIndex(1);
                binField.setEnabled(false);
            } else {
                binField.setIndex(0);
                binField.setEnabled(true);
            }
        }
    }
}
