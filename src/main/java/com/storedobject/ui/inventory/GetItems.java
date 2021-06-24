package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.ChangedValues;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.View;

import java.sql.Date;
import java.util.function.Consumer;

class GetItems extends DataForm implements Transactional {

    private final ELabelField statusField = new ELabelField("Status");
    private final InventoryLocation locationTo;
    private final InventoryStore store;
    private final ObjectField<InventoryItemType> pnField = new ObjectField<>("Item", InventoryItemType.class, true);
    private InventoryItemType partNumber;
    private InventoryItem item;
    private InventoryLocation bin;
    private final TextField snField = new TextField("Serial Number");
    private String serialNumber;
    private final QuantityField qField = new QuantityField("Quantity");
    private final MoneyField costField = new MoneyField("Cost");
    private BinField binField;
    private int stage = 0;
    private final InventoryTransaction inventoryTransaction;
    @SuppressWarnings("rawtypes")
    private ObjectEditor editor;
    private final Consumer<GetItems> moveAction;
    private final String reference;

    GetItems(String caption, InventoryLocation locationTo, Date date, String reference, Consumer<GetItems> moveAction) {
        this(caption, locationTo, null, date, reference, moveAction);
    }

    GetItems(String caption, InventoryLocation locationTo, InventoryTransaction inventoryTransaction, Consumer<GetItems> moveAction) {
        this(caption, locationTo, inventoryTransaction, null, null, moveAction);
    }

    private GetItems(String caption, InventoryLocation locationTo, InventoryTransaction inventoryTransaction, Date date,
                     String reference, Consumer<GetItems> moveAction) {
        super(caption, "Proceed", "Quit", false);
        if(inventoryTransaction == null) {
            inventoryTransaction = new InventoryTransaction(getTransactionManager(), date, reference);
        }
        this.inventoryTransaction = inventoryTransaction;
        this.moveAction = moveAction;
        this.locationTo = locationTo;
        if(locationTo instanceof InventoryBin) {
            store = ((InventoryBin) locationTo).getStore();
            binField = new BinField("Move to");
            binField.setStore(store);
            setRequired(binField);
            binField.setValue((InventoryBin) locationTo);
        } else {
            store = null;
        }
        setButtonsAtTop(true);
        ELabelField ef = new ELabelField("For", locationTo, "blue");
        if(date != null) {
            ef.append(" (Date: ").append(date);
        }
        if(reference != null && reference.isEmpty()) {
            reference = null;
        }
        this.reference = reference;
        if(reference != null) {
            if(date != null) {
                ef.append(", ");
            } else {
                ef.append("(");
            }
            ef.append("Ref: ").append(reference);
        }
        if(date != null || reference != null) {
            ef.append(')');
        }
        ef.update();
        addField(statusField, ef, pnField, snField, qField, costField);
        snField.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                snField.setValue(StoredObject.toCode(snField.getValue()));
            }
        });
        if(binField != null) {
            addField(binField);
        }
        setRequired(pnField);
        trackValueChange(pnField);
        trackValueChange(qField);
        setRequired(qField);
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.isChanged(pnField)) {
            partNumber = pnField.getObject();
            if(partNumber == null) {
                return;
            }
            snField.setLabel(partNumber.getSerialNumberName());
            if(partNumber.isSerialized()) {
                costField.setValue(partNumber.getUnitCost());
                qField.setValue(Count.ONE);
                qField.setEnabled(false);
            } else {
                qField.setEnabled(true);
                costField.setValue(partNumber.getUnitCost());
                qField.setValue(partNumber.getUnitOfMeasurement());
            }
            if(binField == null) {
                return;
            }
            InventoryBin b = InventoryItem.listStock(partNumber, store).
                    map(s -> (InventoryBin)(s.getLocation())).
                    filter(loc -> !loc.getId().equals(locationTo.getId())).findFirst();
            binField.setValue(b == null ? (InventoryBin)locationTo : b);
            return;
        }
        if(changedValues.isFromClient() && changedValues.isChanged(qField)) {
            if(partNumber == null) {
                return;
            }
            Quantity old = (Quantity) changedValues.getOldValue();
            if(old.isZero() || partNumber.getCost(old).equals(costField.getValue())) {
                costField.setValue(partNumber.getCost(qField.getValue()));
            }
        }
    }

    @Override
    protected boolean process() {
        if(stage == 2) {
            doMove();
            return false;
        }
        partNumber = pnField.getObject();
        if(partNumber == null) {
            return false;
        }
        Quantity q = qField.getValue();
        if(q.isZero()) {
            return false;
        }
        try {
            partNumber.checkUnit(q);
        } catch(Invalid_State e) {
            warning(e);
            return false;
        }
        boolean serialized = partNumber.isSerialized();
        serialNumber = snField.getValue();
        if(serialized && serialNumber.isEmpty()) {
            warning("Serial number can not be empty");
            return false;
        }
        InventoryItem item;
        if(serialized && (item = InventoryItem.listStock(partNumber, serialNumber).findFirst()) != null) {
            warning("Item already exists at location: " + item.getLocationDisplay());
            return false;
        }
        stage = 2;
        doMove();
        return false;
    }

    @Override
    public void warning(Object message) {
        if(message instanceof String) {
            statusField.clearContent().append(message, "red").update();
        } else {
            super.warning(message);
        }
    }

    @Override
    public void message(Object message) {
        if(message instanceof String) {
            statusField.clearContent().append(message, "blue").update();
        } else {
            super.message(message);
        }
    }

    @SuppressWarnings("unchecked")
    private void doMove() {
        if(binField == null) {
            bin = locationTo;
        } else {
            bin = binField.getValue();
        }
        if(bin == null) {
            warning("Location/bin must be selected");
            return;
        }
        if(!partNumber.canBin(bin)) {
            warning("This item can not be moved to '" + bin + "'");
            pnField.focus();
            return;
        }
        item = partNumber.createItem();
        item.setSerialNumber(serialNumber);
        item.setQuantity(qField.getValue());
        item.setCost(costField.getValue());
        inventoryTransaction.abandon();
        moveAction.accept(this);
        if(editor != null && editor.getObjectClass() != item.getClass()) {
            editor = null;
        }
        if(editor == null) {
            editor = ObjectEditor.create(item.getClass());
        }
        editor.setCaption("Edit Details");
        editor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
        editor.setSaver(e -> move());
        editor.editObject(item, this);
    }

    @Override
    protected void cancel() {
        if(stage == 0) {
            super.cancel();
            return;
        }
        stage = 0;
        pnField.setEnabled(true);
        snField.setEnabled(true);
        snField.clear();
        InventoryItemType pn = pnField.getObject();
        if(pn == null || !pn.isSerialized()) {
            qField.setEnabled(true);
            qField.clear();
        } else {
            qField.setEnabled(false);
            qField.setValue(Count.ONE);
        }
        costField.clear();
    }

    @Override
    public void returnedFrom(View parent) {
        if(parent == editor) {
            if(parent.aborted()) {
                warning("Cancelled");
            } else {
                message("Item saved");
            }
            cancel();
        }
    }

    private boolean move() {
        try {
            moveTo();
            return true;
        } catch(Throwable error) {
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
        } catch(Throwable error) {
            t.rollback();
            throw error;
        }
    }

    public final InventoryItem getItem() {
        return item;
    }

    public final InventoryTransaction getTransaction() {
        return inventoryTransaction;
    }

    public final InventoryLocation getLocationTo() {
        return bin;
    }

    public final String getReference() {
        return reference;
    }
}
