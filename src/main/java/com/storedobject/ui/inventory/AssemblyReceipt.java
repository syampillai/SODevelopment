package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;

import java.util.ArrayList;
import java.util.List;

public class AssemblyReceipt<T extends InventoryItem, C extends InventoryItem> extends Assembly<T, C> {

    private final InventoryGRN grn;

    @SuppressWarnings("unchecked")
    public AssemblyReceipt(InventoryGRNItem grnItem) {
        this(grnItem, (T) grnItem.getItem());
    }

    private AssemblyReceipt(InventoryGRNItem grnItem, T item) {
        //noinspection unchecked
        super(null, item, (Class<T>) item.getClass(), null);
        grn = grnItem.getMaster(InventoryGRN.class);
        reference = "Assembly - GRN " + grn.getReferenceNumber();
        setCaption(reference);
    }

    @Override
    boolean canSet(InventoryItem item) {
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

        private final ELabelField grnField = new ELabelField("GRN");
        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final ObjectListField<InventoryItemType> partNumbersField = new ObjectListField<>("Part Number", InventoryItemType.class);
        private final ItemField<C> itemField;
        private final QuantityField qRequiredField = new QuantityField("Quantity Required");
        private Quantity qRequired;
        private InventoryTransaction inventoryTransaction;
        private InventoryFitmentPosition fitmentPosition;
        private final TextField snField = new TextField("Serial Number");
        private InventoryItemType itemType;
        private InventoryItem item;
        @SuppressWarnings("rawtypes")
        private ObjectEditor editor;

        public ItemFit(Class<C> itemClass) {
            super("Select Item to Fit", false);
            setButtonsAtTop(true);
            itemField = new ItemField<>(StringUtility.makeLabel(itemClass), itemClass, true);
            addField(grnField, assemblyDetails, partNumbersField, snField, itemField, qRequiredField);
            itemField.setEnabled(false);
            qRequiredField.setEnabled(false);
            trackValueChange(partNumbersField);
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.isFromClient() && changedValues.getChanged() == partNumbersField) {
                itemField.fixPartNumber(partNumbersField.getValue());
            }
        }

        @Override
        protected boolean process() {
            if(inventoryTransaction == null) {
                inventoryTransaction = new InventoryTransaction(getTransactionManager(), grn.getDate(), reference);
            } else {
                inventoryTransaction.abandon();
            }
            String sn = snField.getValue().trim();
            if(itemType.isSerialized()) {
                if(sn.isEmpty()) {
                    warning("Please enter Serial Number of the item");
                    return false;
                }
                if((item = InventoryItem.listStock(itemType, sn).findFirst()) != null) {
                    if(item.getLocation().getType() != 15) { // Not recycle?
                        warning("Item already exists at location: " + item.getLocation().toDisplay());
                        return false;
                    }
                }
            }
            if(item == null) {
                item = partNumbersField.getValue().createItem();
                item.setSerialNumber(sn);
                item.setQuantity(qRequired);
            } else {
                if(!partNumbersField.getValue().getId().equals(item.getPartNumberId())) {
                    error("State error while recovering thrash for '" + item + "'");
                    return false;
                }
            }
            inventoryTransaction.purchase(item, reference, fitmentPosition, grn.getSupplier());
            if(editor != null && editor.getObjectClass() != item.getClass()) {
                editor = null;
            }
            if(editor == null) {
                editor = ObjectEditor.create(item.getClass());
                editor.setCaption("Item Details");
                editor.setFieldHidden("Location", "Cost");
                editor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
            }
            //noinspection unchecked
            editor.setSaver(e -> move());
            close();
            //noinspection unchecked
            editor.editObject(item, AssemblyReceipt.this.getView());
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
            grnField.clearContent().append(grn.getReferenceNumber()).append(" dated ").append(grn.getDate()).update();
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
            itemField.fixPartNumber(itemType);
            List<InventoryItemType> pns = new ArrayList<>(itemType.listAPNs());
            pns.add(0, itemType);
            partNumbersField.setItems(pns);
            partNumbersField.setValue(itemType);
            partNumbersField.setEnabled(pns.size() > 1);
            snField.setValue("");
        }
    }

    private class ItemRemove extends DataForm implements RemoveItem {

        private final ELabelField grnField = new ELabelField("GRN");
        private final ELabelField assemblyDetails = new ELabelField("Assembly Position");
        private final ItemField<InventoryItem> itemField;
        private final QuantityField qFittedField = new QuantityField("Fitted Quantity");
        private InventoryTransaction inventoryTransaction;
        private InventoryItem item;

        public ItemRemove() {
            super("Item Removal", false);
            itemField = new ItemField<>("To remove", InventoryItem.class, true);
            itemField.setEnabled(false);
            qFittedField.setEnabled(false);
            addField(grnField, assemblyDetails, itemField, qFittedField);
            qFittedField.setEnabled(false);
        }

        @Override
        protected boolean process() {
            Quantity qToRemove = qFittedField.getValue();
            if(!item.isSerialized()) {
                if(!qToRemove.equals(item.getQuantity())) {
                    warning("Quantity mismatch " + item.getQuantity() + " \u2260 " + qToRemove);
                    return false;
                }
            }
            if(inventoryTransaction == null) {
                inventoryTransaction = new InventoryTransaction(getTransactionManager(), grn.getDate(), reference);
            } else {
                inventoryTransaction.abandon();
            }
            inventoryTransaction.thrash(item, reference);
            if(transact(t -> inventoryTransaction.save(t))) {
                refresh();
                return true;
            }
            return false;
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition) {
            grnField.append(grn.getReferenceNumber()).append(" dated ").append(grn.getDate()).update();
            assemblyDetails.clearContent().append(fitmentPosition.toDisplay()).update();
            this.item = fitmentPosition.getFittedItem();
            itemField.setValue(item);
            Quantity q = item.getQuantity();
            qFittedField.setValue(q);
        }
    }
}
