package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectListField;
import com.storedobject.ui.QuantityField;
import com.storedobject.vaadin.ChangedValues;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;

import java.util.ArrayList;
import java.util.List;

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

    private class ItemFit extends DataForm implements FitItem {

        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final DateField dateField = new DateField("Date");
        private final TextField refField = new TextField("Reference");
        private final ObjectListField<InventoryItemType> partNumbersField =
                new ObjectListField<>("Part Number", InventoryItemType.class);
        private final ItemField<C> itemField;
        private final QuantityField qRequiredField = new QuantityField("Quantity Required");
        private final QuantityField qAvailableField = new QuantityField("Quantity Available");
        private final QuantityField qFitField = new QuantityField("Quantity to Fit");
        private Quantity qRequired;
        private InventoryTransaction inventoryTransaction;
        private InventoryFitmentPosition fitmentPosition;

        public ItemFit(Class<C> itemClass) {
            super("Select Item to Fit", false);
            itemField = new ItemField<>(StringUtility.makeLabel(itemClass), itemClass, true);
            addField(assemblyDetails, dateField, refField, partNumbersField, itemField, qRequiredField,
                    qAvailableField, qFitField);
            qRequiredField.setEnabled(false);
            qAvailableField.setEnabled(false);
            setRequired(refField);
            setRequired(itemField);
            setRequired(qFitField);
            trackValueChange(partNumbersField);
            trackValueChange(itemField);
            itemField.setStore(() -> {
                InventoryLocation loc = locationField.getValue();
                return loc instanceof InventoryBin ? ((InventoryBin) loc).getStore() : null;
            });
        }

        @Override
        protected boolean process() {
            InventoryItem item = itemField.getValue();
            Quantity qFit = qFitField.getValue();
            if(!item.isSerialized()) {
                if(qFit.isGreaterThan(item.getQuantity())) {
                    warning("Only " + item.getQuantity() + " is available, can't take out " + qFit);
                    qFitField.focus();
                    return false;
                }
                if(qFit.isGreaterThan(qRequired)) {
                    warning("Only " + qRequired + " is required, should not take out more");
                    qFitField.focus();
                    return false;
                }
            }
            date = dateField.getValue();
            if(inventoryTransaction == null || !inventoryTransaction.getDate().equals(date)) {
                inventoryTransaction = new InventoryTransaction(getTransactionManager(), dateField.getValue());
            } else {
                inventoryTransaction.abandon();
            }
            reference = refField.getValue().trim();
            inventoryTransaction.moveTo(item, qFit, reference, fitmentPosition);
            if(transact(t -> inventoryTransaction.save(t))) {
                refresh();
                return true;
            }
            return false;
        }

        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity qAlreadyFitted) {
            this.fitmentPosition = fitmentPosition;
            InventoryAssembly assembly = fitmentPosition.getAssembly();
            assemblyDetails.clearContent().append(fitmentPosition.toDisplay()).update();
            itemField.clear();
            InventoryItemType iit = assembly.getItemType();
            qRequired = assembly.getQuantity();
            if(qAlreadyFitted != null) {
                qRequired = qRequired.subtract(qAlreadyFitted);
            }
            qRequiredField.setValue(qRequired);
            qAvailableField.setValue(qRequired.zero());
            qFitField.setEnabled(!iit.isSerialized());
            qFitField.setValue(iit.isSerialized() ? Count.ONE : qRequired.zero());
            itemField.fixPartNumber(iit);
            List<InventoryItemType> pns = new ArrayList<>(iit.listAPNs());
            pns.add(0, iit);
            partNumbersField.setItems(pns);
            partNumbersField.setValue(iit);
            partNumbersField.setEnabled(pns.size() > 1);
            dateField.setValue(date);
            refField.setValue(reference);
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.isFromClient() && changedValues.getChanged() == partNumbersField) {
                itemField.fixPartNumber(partNumbersField.getValue());
                return;
            }
            if(changedValues.getChanged() == itemField) {
                InventoryItem item = itemField.getValue();
                if(item == null) {
                    qAvailableField.clear();
                    return;
                }
                Quantity q = item.getQuantity();
                qAvailableField.setValue(q);
                if(item.isSerialized()) {
                    qFitField.setValue(q);
                } else {
                    if(q.isLessThan(qRequired)) {
                        qFitField.setValue(q);
                    } else {
                        qFitField.setValue(qRequired);
                    }
                }
            }
        }
    }

    private class ItemRemove extends DataForm implements RemoveItem {

        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final DateField dateField = new DateField("Date");
        private final TextField refField = new TextField("Reference");
        private final ItemField<InventoryItem> itemField;
        private final QuantityField qFittedField = new QuantityField("Fitted Quantity");
        private final QuantityField qToRemoveField = new QuantityField("Quantity to Remove");
        private InventoryTransaction inventoryTransaction;
        private InventoryItem item;
        private final InventoryLocation scrap = new InventoryStoreBin(), specificBin = new InventoryStoreBin();
        private final ObjectListField<InventoryLocation> binField =
                new ObjectListField<>("Remove to", InventoryLocation.class);
        private boolean binPopulated = false;
        private final BinField specificBinField = new BinField("Specific Bin");

        public ItemRemove() {
            super("Item Removal", false);
            itemField = new ItemField<>("To remove", InventoryItem.class, true);
            itemField.setEnabled(false);
            qFittedField.setEnabled(false);
            addField(assemblyDetails, dateField, refField, itemField, qFittedField, qToRemoveField, binField,
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
            qFittedField.setEnabled(false);
            setRequired(refField);
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
            reference = refField.getValue().trim();
            if(location == scrap) {
                inventoryTransaction.scrap(item, qToRemove, reference);
            } else {
                inventoryTransaction.moveTo(item, qToRemove, reference, location);
            }
            if(transact(t -> inventoryTransaction.save(t))) {
                refresh();
                return true;
            }
            return false;
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition) {
            assemblyDetails.clearContent().append(fitmentPosition.toDisplay()).update();
            this.item = fitmentPosition.getFittedItem();
            itemField.setValue(item);
            Quantity q = item.getQuantity();
            qFittedField.setValue(q);
            qToRemoveField.setValue(q);
            qToRemoveField.setEnabled(!item.isSerialized() && !item.isConsumable());
            dateField.setValue(date);
            refField.setValue(reference);
            if(!binPopulated) {
                binPopulated = true;
                InventoryLocation location = locationField.getValue();
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
