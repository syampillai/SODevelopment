package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;

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
        reference = "Assembly - GRN " + grn.getReference();
        setCaption(reference);
    }

    @Override
    FitItem createFitItem(Class<C> itemClass) {
        return new ItemFit(itemClass);
    }

    @Override
    RemoveItem createRemoveItem() {
        return new ItemRemove();
    }

    private class ItemFit extends AbstractItemFit {

        private final ELabelField grnField = new ELabelField("GRN");
        private final TextField snField = new TextField("Serial Number");
        @SuppressWarnings("rawtypes")
        private ObjectEditor editor;

        public ItemFit(Class<C> itemClass) {
            super(itemClass);
            setButtonsAtTop(true);
            addField(grnField, partNumbersField, snField, (HasValue<?, ?>) itemField, requiredQuantityField);
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
            String sn = StoredObject.toCode(snField.getValue());
            if(sn.isEmpty() && itemType.isSerialized()) {
                warning("Please enter Serial Number of the item");
                return false;
            }
            InventoryItemType inventoryItemType = partNumbersField.getValue();
            item = inventoryItemType.createItem(sn);
            if(item == null) {
                warning("Duplicate item for S/N = " + sn + " exists. Item " + inventoryItemType.toDisplay());
            }
            item.setQuantity(quantityRequired);
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
            editor.setSaver(e -> moveItem());
            close();
            //noinspection unchecked
            editor.editObject(item, AssemblyReceipt.this.getView());
            return true;
        }

        @Override
        public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity quantityAlreadyFitted) {
            super.setAssemblyPosition(fitmentPosition, quantityAlreadyFitted);
            grnField.clearContent().append(grn.getReference()).append(" dated ").append(grn.getDate()).update();
            snField.setValue("");
        }
    }

    private class ItemRemove extends AbstractItemRemove {

        private final ELabelField grnField = new ELabelField("GRN");

        public ItemRemove() {
            addField(grnField, itemField, fittedQuantityField);
            fittedQuantityField.setEnabled(false);
        }

        @Override
        protected boolean process() {
            Quantity qToRemove = fittedQuantityField.getValue();
            if(!item.isSerialized()) {
                if(!qToRemove.equals(item.getQuantity())) {
                    warning("Quantity mismatch " + item.getQuantity() + " ≠ " + qToRemove);
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
            grnField.append(grn.getReference()).append(" dated ").append(grn.getDate()).update();
            super.setAssemblyPosition(fitmentPosition);
        }
    }
}
