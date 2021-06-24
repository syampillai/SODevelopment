package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectListField;
import com.storedobject.ui.QuantityField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ChangedValues;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

import java.sql.Date;

class IssueItems extends DataForm implements Transactional {

    private final ELabelField statusField = new ELabelField("Status");
    private final ItemField<InventoryItem> itemField = new ItemField<>("Item", InventoryItem.class, true);
    private InventoryItem item;
    private final QuantityField qField = new QuantityField("Quantity");
    private final ELabelField fromBinField = new ELabelField("From");
    private final ObjectListField<InventoryLocation> binField = new ObjectListField<>("To", InventoryLocation.class);
    private final TextField refField = new TextField();
    private final InventoryTransaction inventoryTransaction;
    private final InventoryStoreBin specificBin = new InventoryStoreBin();
    private final BinField specificBinField = new BinField("To Specific Bin");

    public IssueItems(String caption, InventoryLocation locationFrom, InventoryLocation locationTo, Date date) {
        super(caption, "Proceed", "Quit",false);
        if(locationFrom instanceof InventoryStoreBin) {
            itemField.setStore(((InventoryStoreBin) locationFrom).getStore());
        } else {
            itemField.setLocation(locationFrom);
        }
        this.inventoryTransaction = new InventoryTransaction(getTransactionManager(), date);
        specificBin.setName("<Specific Bin>");
        specificBin.makeVirtual();
        setButtonsAtTop(true);
        ELabelField ef = new ELabelField("Item Movement");
        if(locationFrom.getId().equals(locationTo.getId())) {
            ef.append("Within the ");
        } else {
            ef.append(locationFrom.toDisplay(), "blue");
            ef.append(" \u25BA ");
        }
        ef.append(locationTo.toDisplay(), "blue").update();
        refField.setLabel("Reference (" + DateUtility.formatDate(date) + ")");
        addField(statusField, ef, itemField, qField, fromBinField, binField);
        setRequired(itemField);
        trackValueChange(itemField);
        setRequired(qField);
        if(locationTo instanceof InventoryStoreBin) {
            binField.addItems(locationTo, specificBin);
            specificBinField.setStore(((InventoryStoreBin) locationTo).getStore());
            addField(specificBinField);
            binField.addValueChangeListener(e -> {
                boolean v = e.getValue().equals(specificBin);
                setFieldVisible(v, specificBinField);
                if(v) {
                    specificBinField.focus();
                }
            });
        } else {
            binField.addItems(locationTo);
        }
        addField(refField);
        binField.setValue(locationTo);
        setRequired(binField);
        setRequired(refField);
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.isChanged(itemField)) {
            item = itemField.getObject();
            if(item == null) {
                return;
            }
            fromBinField.clearContent().append(item.getLocation().toDisplay()).update();
            if(item.isSerialized()) {
                qField.setValue(Count.ONE);
                qField.setEnabled(false);
                message("S/N " + item.getSerialNumber() + " selected");
            } else {
                qField.setEnabled(true);
                qField.setValue(item.getPartNumber().getUnitOfMeasurement());
                message("Available quantity is " + item.getQuantity());
            }
        }
    }

    @Override
    protected boolean process() {
        Quantity q = qField.getValue();
        if(q.isZero()) {
            return false;
        }
        if(q.isGreaterThan(item.getQuantity())) {
            warning("Available quantity is only " + item.getQuantity());
            return false;
        }
        InventoryLocation bin = binField.getValue();
        if(bin == null) {
            warning("Location/bin must be selected");
            return false;
        }
        if(bin == specificBin) {
            bin = specificBinField.getObject();
            if(bin == null) {
                warning("Bin must be selected");
                return false;
            }
        }
        if(bin.getId().equals(item.getLocationId())) {
            warning("Location/bin not changed");
            return false;
        }
        if(!item.canBin(bin)) {
            warning("This item can not be moved to '" + bin + "'");
            itemField.focus();
            return false;
        }
        inventoryTransaction.abandon();
        inventoryTransaction.moveTo(item, q, refField.getValue(), bin);
        try {
            inventoryTransaction.save();
            message("Item moved successfully");
            qField.clear();
        } catch(Exception e) {
            error(e);
        }
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
}
