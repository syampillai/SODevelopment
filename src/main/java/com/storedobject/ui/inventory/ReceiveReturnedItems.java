package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Return items from an external organization / custody that were sent to them earlier
 * (Sent for repairs, rented out, custody etc.)
 * 
 * @author Syam
 */
public class ReceiveReturnedItems extends DataForm implements Transactional {

    private InventoryStoreBin storeBin;
    private InventoryLocation eo; // External organization or custody
    private final LocationField storeField;
    private final LocationField eoField;
    private final int type;
    private Date date, invoiceDate;
    private String invoiceRef;

    public ReceiveReturnedItems(int type) {
        this(type, (InventoryStoreBin) null, null);
    }

    public ReceiveReturnedItems(int type, String storeAndEOName) {
        this(type, LocationField.getStore(storeAndEOName), eoName(storeAndEOName, type));
    }

    public ReceiveReturnedItems(int type, InventoryStore store) {
        this(type, store, null);
    }

    public ReceiveReturnedItems(int type, InventoryStore store, InventoryLocation eo) {
        this(type, store.getStoreBin(), eo);
    }

    public ReceiveReturnedItems(int type, InventoryStoreBin storeBin) {
        this(type, storeBin, null);
    }

    public ReceiveReturnedItems(int type, InventoryStoreBin storeBin, InventoryLocation eo) {
        super(caption(type));
        this.type = type;
        this.storeBin = storeBin;
        this.eo = eo;
        if(storeBin == null) {
            storeField = LocationField.create(0);
            if(storeField.getLocationCount() == 1) {
                this.storeBin = (InventoryStoreBin) storeField.getValue();
            }
        } else {
            storeField = LocationField.create(storeBin);
        }
        storeField.setLabel("Select Store");
        if(eo == null) {
            eoField = LocationField.create(type);
            if(eoField.getLocationCount() == 1) {
                this.eo = eoField.getValue();
            }
        } else {
            if(eo.getType() != type) {
                throw new SORuntimeException("Incorrect - " + eo.getTypeValue());
            }
            eoField = LocationField.create(eo);
        }
        eoField.setLabel(type == 18 ? "Custodian" : "Organization");
        addField(storeField, eoField);
        if(storeBin != null) {
            setFieldReadOnly(storeField);
        }
        if(eo != null) {
            setFieldReadOnly(eoField);
        }
        setRequired(storeField);
        setRequired(eoField);
    }

    private static String caption(int type) {
        switch(type) {
            case 3:
                return "Receive Repaired Items";
            case 8:
                return "Receive Lease Returns";
            case 18:
                return "Receive Tools Returned";
        }
        throw new SORuntimeException("Invalid type: " + InventoryLocation.getTypeValue(type));
    }

    private static InventoryLocation eoName(String storeAndEOName, int type) {
        if(storeAndEOName == null || !storeAndEOName.contains("|")) {
            return null;
        }
        storeAndEOName = storeAndEOName.substring(storeAndEOName.indexOf('|') + 1).trim();
        if(storeAndEOName.isEmpty()) {
            return null;
        }
        return LocationField.getLocation(storeAndEOName, type);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(storeBin != null && eo != null) {
            proceed();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        close();
        if(storeBin == null) {
            storeBin = (InventoryStoreBin) storeField.getValue();
        }
        if(eo == null) {
            eo = eoField.getValue();
        }
        proceed();
        return true;
    }

    private void proceed() {
        List<InventoryItem> items = StoredObject.list(InventoryItem.class, "Location=" + eo.getId(), true)
                .filter(ii -> ii.getPreviousLocation() instanceof InventoryBin bin
                        && bin.getStoreId().equals(storeBin.getStoreId())).toList();
        if(items.isEmpty()) {
            processOld();
            message("For this store, no items pending to be received from:<BR/>" + eo.toDisplay());
            return;
        }
        new Select(items, true).execute();
    }

    private void process(List<InventoryItem> allItems, Set<InventoryItem> selectedItems, boolean confirm) {
        MaterialReturned returnedToProcess = null;
        InventoryItem item;
        List<MaterialReturnedItem> returnedItems;
        List<MaterialReturned> returns = StoredObject.list(MaterialReturned.class,
                "FromLocation=" + eo.getId() + " AND Status<2").toList();
        for(MaterialReturned returned: returns) {
            returnedItems = returned.listLinks(MaterialReturnedItem.class).toList();
            for(InventoryTransferItem grnItem: returnedItems) {
                item = grnItem.getItem();
                if(item == null) {
                    continue;
                }
                if(selectedItems.contains(item)) {
                    if(returned.getStatus() == 0) {
                        returnedToProcess = returned;
                        break;
                    }
                    warning("Item already in Return Reference " + returned.getReference()
                            + " (To " + returned.getToLocation().toDisplay() + "), Item = " + item.toDisplay());
                    Select select = new Select(allItems, true);
                    select.execute();
                    for(InventoryItem ii: selectedItems) {
                        select.select(ii);
                    }
                    select.deselect(item);
                    return;
                }
            }
        }
        if(confirm || allItems.size() != selectedItems.size()) {
            Select select = new Select(new ArrayList<>(selectedItems), false);
            select.execute();
            selectedItems.forEach(select::select);
            return;
        }
        if(returnedToProcess == null) {
            returnedToProcess = new MaterialReturned();
            returnedToProcess.setDate(date);
            returnedToProcess.setFromLocation(eo);
            returnedToProcess.setToLocation(storeBin);
            if(type == 3) { // RO
                returnedToProcess.setInvoiceDate(invoiceDate);
                returnedToProcess.setInvoiceNumber(invoiceRef);
            }
            MaterialReturned finalReturnedToProcess = returnedToProcess;
            if(!transact(t -> {
                finalReturnedToProcess.save(t);
                MaterialReturnedItem returnedItem;
                for(InventoryItem ii : selectedItems) {
                    returnedItem = new MaterialReturnedItem();
                    returnedItem.setItem(ii);
                    returnedItem.setQuantity(ii.getQuantity());
                    returnedItem.save(t);
                    finalReturnedToProcess.addLink(t, returnedItem);
                }
            })) {
                return;
            }
            returnedToProcess.reload();
        }
        if(!transact(returnedToProcess::send)) {
            return;
        }
        ReceiveMaterialReturned v = new ReceiveMaterialReturned(storeBin, eo);
        v.execute();
        v.receive(returnedToProcess);
    }

    private void processOld() {
        new ReceiveMaterialReturned(storeBin, eo).execute();
    }

    private class Select extends MultiSelectGrid<InventoryItem> {

        private final boolean confirm;
        private final DateField dateField = new DateField();
        private final TextField invoiceRefField = new TextField();
        private final DateField invoiceDateField = new DateField();

        public Select(List<InventoryItem> items, boolean confirm) {
            super(InventoryItem.class, items,
                    StringList.create("PartNumber", "SerialNumber", "Quantity", "Location"),
                    selectedSet -> ReceiveReturnedItems.this.process(items, selectedSet, confirm));
            invoiceRefField.uppercase();
            invoiceRefField.addValueChangeListener(e -> {
               if(e.isFromClient()) {
                   invoiceRefField.setValue(StoredObject.toCode(invoiceRefField.getValue()));
               }
            });
            this.confirm = confirm;
            if(confirm) {
                setCaption("Select Returned Items");
            } else {
                setCaption("Confirm Items");
                if(type == 3) {
                    invoiceRefField.setValue(invoiceRef);
                    invoiceDateField.setValue(invoiceDate);
                }
            }
        }

        @Override
        public void addExtraButtons() {
            super.addExtraButtons();
            if(type != 3) {
                buttonLayout.add(new ELabel("Date: "), dateField);
            }
            if(!confirm) {
                return;
            }
            proceed.setText("Process Selected Entries");
            Button b = new Button("Show Previous Entries", VaadinIcon.COG_O, e -> {
                clearAlerts();
                close();
                processOld();
            });
            buttonLayout.add(new ELabel("Process: "), b);
        }

        @Override
        public void createHeaders() {
            if(type == 3) { // RO
                prependHeader().join().setComponent(new ButtonLayout(new ELabel("Date: "), dateField,
                        new ELabel(" Invoice No.: "), invoiceRefField, new ELabel(" Invoice Date: "),
                        invoiceDateField));
            }
            if(confirm) {
                return;
            }
            ELabel h = new ELabel(
                    "These items will be received, please double-check and confirm! Undo not possible after this step!!",
                    "red");
            prependHeader().join().setComponent(h);
        }

        @Override
        protected boolean validate() {
            clearAlerts();
            date = dateField.getValue();
            if(date.after(DateUtility.endOfToday())) {
                warning("Please check the receipt date: " + DateUtility.format(date));
                return false;
            }
            invoiceDate = invoiceDateField.getValue();
            invoiceRef = StoredObject.toCode(invoiceRefField.getValue());
            if(getSelectedItems().isEmpty()) {
                warning("No items selected!");
                return false;
            }
            clearAlerts();
            return true;
        }
    }
}
