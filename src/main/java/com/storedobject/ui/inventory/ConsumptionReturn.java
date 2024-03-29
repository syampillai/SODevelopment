package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Return items from a consumption location (return balance quantity after consumption).
 *
 * @author Syam
 */
public class ConsumptionReturn extends DataForm implements Transactional {

    private InventoryStoreBin storeBin;
    private InventoryLocation cl; // Consumption location
    private final LocationField storeField;
    private final LocationField clField;
    private Date date;

    public ConsumptionReturn() {
        this((InventoryStoreBin) null, null);
    }

    public ConsumptionReturn(String storeAndEOName) {
        this(LocationField.getStore(storeAndEOName), eoName(storeAndEOName));
    }

    public ConsumptionReturn(InventoryStore store) {
        this(store, null);
    }

    public ConsumptionReturn(InventoryStore store, InventoryLocation cl) {
        this(store.getStoreBin(), cl);
    }

    public ConsumptionReturn(InventoryStoreBin storeBin) {
        this(storeBin, null);
    }

    public ConsumptionReturn(InventoryStoreBin storeBin, InventoryLocation cl) {
        super("Consumption Returns");
        this.storeBin = storeBin;
        this.cl = cl;
        if(storeBin == null) {
            storeField = LocationField.create(0);
            if(storeField.getLocationCount() == 1) {
                this.storeBin = (InventoryStoreBin) storeField.getValue();
            }
        } else {
            storeField = LocationField.create(storeBin);
        }
        storeField.setLabel("Select Store");
        if(cl == null) {
            clField = LocationField.create(16);
            if(clField.getLocationCount() == 1) {
                this.cl = clField.getValue();
            }
        } else {
            if(cl.getType() != 16) {
                throw new SORuntimeException("Incorrect - " + cl.getTypeValue());
            }
            clField = LocationField.create(cl);
        }
        clField.setLabel("Consumption Location");
        addField(storeField, clField);
        if(storeBin != null) {
            setFieldReadOnly(storeField);
        }
        if(cl != null) {
            setFieldReadOnly(clField);
        }
        setRequired(storeField);
        setRequired(clField);
    }

    private static InventoryLocation eoName(String storeAndEOName) {
        if(storeAndEOName == null || !storeAndEOName.contains("|")) {
            return null;
        }
        storeAndEOName = storeAndEOName.substring(storeAndEOName.indexOf('|') + 1).trim();
        if(storeAndEOName.isEmpty()) {
            return null;
        }
        return LocationField.getLocation(storeAndEOName, 16);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(storeBin != null && cl != null) {
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
        if(cl == null) {
            cl = clField.getValue();
        }
        proceed();
        return true;
    }

    private void proceed() {
        List<InventoryItem> items = StoredObject.list(InventoryItem.class, "Location=" + cl.getId(), true)
                .filter(ii -> ii.getPreviousLocation() instanceof InventoryBin bin
                        && bin.getStoreId().equals(storeBin.getStoreId())).toList();
        if(items.isEmpty()) {
            processOld();
            message("For this store, no items pending to be received from:<BR/>" + cl.toDisplay());
            return;
        }
        new Select(items, true).execute();
    }

    private void process(List<InventoryItem> allItems, Set<InventoryItem> selectedItems, boolean confirm) {
        MaterialReturned returnedToProcess = null;
        InventoryItem item;
        List<MaterialReturnedItem> returnedItems;
        List<MaterialReturned> returns = StoredObject.list(MaterialReturned.class,
                "FromLocation=" + cl.getId() + " AND Status<2").toList();
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
            returnedToProcess.setFromLocation(cl);
            returnedToProcess.setToLocation(storeBin);
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
        ReceiveMaterialReturned v = new ReceiveMaterialReturned(storeBin, cl);
        v.execute();
        v.receive(returnedToProcess);
    }

    private void processOld() {
        new ReceiveMaterialReturned(storeBin, cl).execute();
    }

    private class Select extends MultiSelectGrid<InventoryItem> {

        private final boolean confirm;
        private final DateField dateField = new DateField();

        public Select(List<InventoryItem> items, boolean confirm) {
            super(InventoryItem.class, items,
                    StringList.create("PartNumber", "SerialNumberDisplay AS Serial/Batch Number", "Quantity", "Location"),
                    selectedSet -> ConsumptionReturn.this.process(items, selectedSet, confirm));
            this.confirm = confirm;
            if(confirm) {
                setCaption("Select Returned Items");
            } else {
                setCaption("Confirm Items");
            }
        }

        @Override
        public void addExtraButtons() {
            super.addExtraButtons();
            /*
            if(type != 3) {
                buttonLayout.add(new ELabel("Date: "), dateField);
            }
            */
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
            if(confirm) {
                return;
            }
            ELabel h = new ELabel(
                    "These items will be received, please double-check and confirm! Undo not possible after this step!!",
                    Application.COLOR_ERROR);
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
            if(getSelectedItems().isEmpty()) {
                warning("No items selected!");
                return false;
            }
            clearAlerts();
            return true;
        }
    }
}
