package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class ChangePartNumber extends DataForm {

    private final ItemField<?> itemField = new ItemField<>("Item", InventoryItem.class, true);
    private InventoryItem item;
    private final TextField currentPNField = new TextField("Current P/N"),
            newPNField = new TextField("Modified P/N");
    private Runnable refresher;

    public ChangePartNumber() {
        this(null);
    }

    public ChangePartNumber(InventoryItem item) {
        super("Change Part Number");
        currentPNField.setReadOnly(true);
        itemField.setLoadFilter(InventoryItem::isSerialized);
        itemField.addValueChangeListener(e -> {
            this.item = itemField.getObject();
            if(this.item == null) {
                currentPNField.setValue("");
                newPNField.setValue("");
                itemField.focus();
            } else {
                String pn = this.item.getPartNumber().getPartNumber();
                currentPNField.setValue(pn);
                newPNField.setValue(pn);
                newPNField.focus();
            }
        });
        newPNField.uppercase();
        newPNField.addValueChangeListener(e -> newPNField.setValue(StoredObject.toCode(e.getValue())));
        if(item != null) {
            this.item = item;
            itemField.setValue(item.getId());
            itemField.setReadOnly(true);
        }
        addField(itemField, currentPNField, newPNField);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(item == null) {
            return false;
        }
        String c = currentPNField.getValue(), n = newPNField.getValue();
        if(c.equals(n) || n.isEmpty()) {
            return false;
        }
        InventoryItemType itemType = InventoryItemType.get(n);
        if(itemType != null) {
            if(itemType.getClass() != item.getPartNumber().getClass()) {
                warning("The new P/N doesn't match the type of the item!");
                return false;
            }
            if(itemType.isBlocked()) {
                warning("The new P/N is a blocked P/N!");
                return false;
            }
        }
        close();
        new CPN(item, n, itemType, refresher).execute();
        return true;
    }

    public void setRefresher(Runnable refresher) {
        this.refresher = refresher;
    }

    private static class CPN extends DataForm implements Transactional {

        private final InventoryItem item;
        private final String pn;
        private InventoryItemType itemType;
        private BooleanField addAPN;
        private final Runnable refresher;

        public CPN(InventoryItem item, String pn, InventoryItemType itemType, Runnable refresher) {
            super("Change Part Number");
            this.item = item;
            this.pn = pn;
            this.itemType = itemType;
            this.refresher = refresher;
            TextField tf = new TextField("Item");
            tf.setValue(item.toDisplay());
            addField(tf);
            setFieldReadOnly(tf);
            tf = new TextField("New P/N");
            tf.setValue(itemType == null ? (pn + " (Will be created!)") : itemType.toDisplay());
            addField(tf);
            setFieldReadOnly(tf);
            if(itemType == null || !itemType.isAPN(item)) {
                addAPN = new BooleanField("Add P/N " + pn + " as an APN to "
                        + item.getPartNumber().getPartNumber() + "?");
                addField(addAPN);
            } else {
                tf = new TextField("Note");
                tf.setValue(pn + " is an APN of " + item.getPartNumber().getPartNumber());
                addField(tf);
                setFieldReadOnly(tf);
            }
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Confirm");
        }

        @Override
        protected boolean process() {
            if(transact(this::change)) {
                message("P/N converted successfully for the item: " + item.toDisplay());
                if(refresher != null) {
                    try {
                        refresher.run();
                    } catch(Throwable ignored) {
                    }
                }
                return true;
            }
            return false;
        }

        private void change(Transaction t) throws Exception {
            Id oldPNId = item.getPartNumberId();
            if(itemType == null) {
                itemType = item.getPartNumber();
                itemType.makeNew();
                itemType.setPartNumber(pn);
                itemType.save(t);
            }
            item.changePartNumber(t, itemType);
            if(addAPN != null && addAPN.getValue()) {
                InventoryAPN apn = new InventoryAPN();
                apn.setPartNumber(itemType);
                apn.save(t);
                StoredObject.get(itemType.getClass(), oldPNId).addLink(t, apn);
            }
        }
    }
}
