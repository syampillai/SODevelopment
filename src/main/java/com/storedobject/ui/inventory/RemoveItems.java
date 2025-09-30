package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.QuantityField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ChangedValues;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.View;

import java.sql.Date;

class RemoveItems extends DataForm implements Transactional {

    static final StringList removalAction = StringList.create("Selling", "Renting out", "Scraping", "Booking Shortage");
    private final ELabelField statusField = new ELabelField("Status");
    private final ItemField<InventoryItem> itemField = new ItemField<>("Item", InventoryItem.class, true);
    private InventoryItem item;
    private final QuantityField qField = new QuantityField("Quantity");
    private final TextField refField = new TextField();
    private final InventoryTransaction inventoryTransaction;
    private final Entity entity;
    private final int action;

    public RemoveItems(InventoryLocation locationFrom, int removalAction, Entity entity, Date date) {
        super("Remove by " + RemoveItems.removalAction.get(removalAction), "Proceed", "Quit",false);
        this.entity = entity;
        this.action = removalAction;
        if(locationFrom instanceof InventoryStoreBin) {
            itemField.setStore(((InventoryStoreBin) locationFrom).getStore());
        } else {
            itemField.setLocation(locationFrom);
        }
        this.inventoryTransaction = new InventoryTransaction(getTransactionManager(), date);
        setButtonsAtTop(true);
        refField.setLabel("Reference (" + DateUtility.format(date) + ")");
        ELabelField ef = new ELabelField("Remove Item by");
        ef.append(RemoveItems.removalAction.get(removalAction));
        if(requiresEntity(action)) {
            ef.append(" to ").append(entity, Application.COLOR_SUCCESS);
        }
        ef.update();
        addField(new ELabelField("From", locationFrom.toDisplay()), statusField, ef, itemField, qField);
        setRequired(itemField);
        trackValueChange(itemField);
        setRequired(qField);
        addField(refField);
        setRequired(refField);
    }

    static boolean requiresEntity(int action) {
        return switch(action) {
            case 2, 3 -> false;
            default -> true;
        };
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.isChanged(itemField)) {
            item = itemField.getObject();
            if(item == null) {
                return;
            }
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
    protected void execute(View parent, boolean doNotLock) {
        if(requiresEntity(action) && entity == null) {
            error("Entity not set for action '" + removalAction.get(action) + "'");
        } else {
            super.execute(parent, doNotLock);
        }
    }

    @Override
    protected boolean process() {
        Quantity q = qField.getValue();
        if(q.isZero()) {
            return false;
        }
        inventoryTransaction.abandon();
        String reference = refField.getValue();
        switch(action) {
            case 0 -> inventoryTransaction.sale(item, q, reference, entity);
            case 1 -> inventoryTransaction.loanTo(item, q, reference, entity);
            case 2 -> inventoryTransaction.scrap(item, q, reference);
            case 3 -> inventoryTransaction.bookShortage(item, q, reference);
            default -> {
                warning("Not yet implemented!");
                return false;
            }
        }
        try {
            inventoryTransaction.save();
            message("Item moved successfully");
            qField.clear();
        } catch(Exception e) {
            error(e);
            return true;
        }
        return false;
    }

    @Override
    public void warning(Object message) {
        if(message instanceof String) {
            statusField.clearContent().append(message, Application.COLOR_ERROR).update();
        } else {
            super.warning(message);
        }
    }

    @Override
    public void message(Object message) {
        if(message instanceof String) {
            statusField.clearContent().append(message, Application.COLOR_SUCCESS).update();
        } else {
            super.message(message);
        }
    }
}
