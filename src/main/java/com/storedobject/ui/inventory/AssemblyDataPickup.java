package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;

import java.util.ArrayList;
import java.util.List;

public class AssemblyDataPickup<T extends InventoryItem, C extends InventoryItem> extends Assembly<T, C> {

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
        this(createClass(itemClass, false), createClass(itemClass, true));
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

    private class ItemFit extends DataForm implements FitItem {

        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final ObjectListField<InventoryItemType> partNumbersField = new ObjectListField<>("Part Number", InventoryItemType.class);
        private final ItemField<C> itemField;
        private final QuantityField qRequiredField = new QuantityField("Quantity Required");
        private final QuantityField qAvailableField = new QuantityField("Quantity Available");
        private final QuantityField qFitField = new QuantityField("Quantity to Fit");
        private Quantity qRequired;
        private InventoryTransaction inventoryTransaction;
        private InventoryFitmentPosition fitmentPosition;
        private final MoneyField costField = new MoneyField("Cost");
        private final BooleanField previousData = new BooleanField("Select from Previously Picked-up Data");
        private final TextField snField = new TextField("Serial Number");
        private InventoryItemType itemType;
        private InventoryItem item;
        @SuppressWarnings("rawtypes")
        private ObjectEditor editor;

        public ItemFit(Class<C> itemClass) {
            super("Select Item to Fit", false);
            setButtonsAtTop(true);
            itemField = new ItemField<>(StringUtility.makeLabel(itemClass), itemClass, true);
            addField(assemblyDetails, partNumbersField, previousData, snField, itemField,
                    qRequiredField, qAvailableField, qFitField, costField);
            itemField.setEnabled(false);
            qRequiredField.setEnabled(false);
            qAvailableField.setEnabled(false);
            //setRequired(refField);
            setRequired(qFitField);
            trackValueChange(previousData);
            trackValueChange(partNumbersField);
            trackValueChange(itemField);
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
                costField.setLabel("Cost of " + q);
                costField.setValue(item.getCost());
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
            Quantity qFit = qFitField.getValue();
            if(qFit.isGreaterThan(qRequired)) {
                warning("Only " + qRequired + " is required");
                qFitField.focus();
                return false;
            }
            if(inventoryTransaction == null) {
                inventoryTransaction = InventoryTransaction.forDataPickup(getTransactionManager());
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
                        qFitField.focus();
                        return false;
                    }
                }
                inventoryTransaction.moveTo(item, qFit, reference, fitmentPosition);
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
            editor.setSaver(e -> move());
            close();
            //noinspection unchecked
            editor.editObject(item, AssemblyDataPickup.this.getView());
            return true;
        }

        private boolean move() {
            try {
                moveTo();
                return true;
            } catch(Exception error) {
                error(error);
            }
            return false;
        }

        private void moveTo() throws Exception {
            Transaction t = getTransactionManager().createTransaction();
            try {
                item.save(t);
                inventoryTransaction.save(t);
                t.commit();
                refresh();
            } catch(Exception error) {
                t.rollback();
                throw error;
            }
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity qAlreadyFitted) {
            assemblyDetails.clearContent().append(fitmentPosition.toDisplay()).update();
            this.fitmentPosition = fitmentPosition;
            itemField.clear();
            InventoryAssembly assembly = fitmentPosition.getAssembly();
            itemType = assembly.getItemType();
            qRequired = assembly.getQuantity();
            if(qAlreadyFitted != null) {
                qRequired = qRequired.subtract(qAlreadyFitted);
            }
            qRequiredField.setValue(qRequired);
            qAvailableField.setValue(qRequired.zero());
            qFitField.setEnabled(!itemType.isSerialized());
            qFitField.setValue(itemType.isSerialized() ? Count.ONE : qRequired.zero());
            itemField.fixPartNumber(itemType);
            List<InventoryItemType> pns = new ArrayList<>(itemType.listAPNs());
            pns.add(0, itemType);
            partNumbersField.setItems(pns);
            partNumbersField.setValue(itemType);
            partNumbersField.setEnabled(pns.size() > 1);
            previousData.setValue(false);
            snField.setValue("");
            costField.setLabel("Unit Cost");
            costField.setValue(itemType.getUnitCost());
        }
    }

    private class ItemRemove extends DataForm implements RemoveItem {

        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final TextField refField = new TextField("Reference");
        private final ItemField<InventoryItem> itemField;
        private final QuantityField qFittedField = new QuantityField("Fitted Quantity");
        private final QuantityField qToRemoveField = new QuantityField("Quantity to Remove");
        private InventoryTransaction inventoryTransaction;
        private InventoryItem item;

        public ItemRemove() {
            super("Item Removal", false);
            itemField = new ItemField<>("To remove", InventoryItem.class, true);
            itemField.setEnabled(false);
            qFittedField.setEnabled(false);
            addField(assemblyDetails, refField, itemField, qFittedField, qToRemoveField);
            qFittedField.setEnabled(false);
            setRequired(refField);
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
                inventoryTransaction = InventoryTransaction.forDataPickup(getTransactionManager());
            } else {
                inventoryTransaction.abandon();
            }
            reference = refField.getValue().trim();
            inventoryTransaction.thrash(item, qToRemove, reference);
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
            assemblyDetails.clearContent().append(fitmentPosition.toDisplay()).update();
            this.item = fitmentPosition.getFittedItem();
            itemField.setValue(item);
            Quantity q = item.getQuantity();
            qFittedField.setValue(q);
            qToRemoveField.setValue(q);
            qToRemoveField.setEnabled(!item.isSerialized() && !item.isConsumable());
            refField.setValue(reference);
        }
    }
}
