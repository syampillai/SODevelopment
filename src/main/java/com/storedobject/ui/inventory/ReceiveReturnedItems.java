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
 * Return items from an external organization that were sent to them earlier (Sent for repairs, rented out etc.)
 * 
 * @author Syam
 */
public class ReceiveReturnedItems extends DataForm implements Transactional {

    private InventoryLocation storeBin;
    private InventoryLocation eo;
    private final LocationField storeField;
    private final LocationField eoField;
    private Date date;

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
        this.storeBin = storeBin;
        this.eo = eo;
        if(storeBin == null) {
            storeField = LocationField.create(0);
            if(storeField.getLocationCount() == 1) {
                this.storeBin = storeField.getValue();
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
        eoField.setLabel("Organization");
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
        }
        throw new SORuntimeException("Invalid type: " + InventoryLocation.getTypeValue(type));
    }

    private static InventoryLocation eoName(String storeAndEOName, int type) {
        if(storeAndEOName == null || storeAndEOName.isEmpty() || !storeAndEOName.contains("|")) {
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
            storeBin = storeField.getValue();
        }
        if(eo == null) {
            eo = eoField.getValue();
        }
        proceed();
        return true;
    }

    private void proceed() {
        List<InventoryItem> items = StoredObject.list(InventoryItem.class, "Location=" + eo.getId(), true).
                toList();
        if(items.isEmpty()) {
            processOld();
            message("No items pending to be received from:<BR/>" + eo.toDisplay());
            return;
        }
        new Select(items, true).execute();
    }

    private void process(List<InventoryItem> allItems, Set<InventoryItem> selectedItems, boolean confirm) {
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
                    warning("Item already in Return Reference " + returned.getReferenceNumber() +
                            " (To " + returned.getToLocation().toDisplay() +
                            "), Item = " + item.toDisplay());
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
        MaterialReturned returned = new MaterialReturned();
        returned.setDate(date);
        returned.setFromLocation(eo);
        returned.setToLocation(storeBin);
        if(!transact(t -> {
            returned.save(t);
            MaterialReturnedItem returnedItem;
            for(InventoryItem ii: selectedItems) {
                returnedItem = new MaterialReturnedItem();
                returnedItem.setItem(ii);
                returnedItem.setQuantity(ii.getQuantity());
                returnedItem.save(t);
                returned.addLink(t, returnedItem);
            }
        })) {
            return;
        }
        returned.reload();
        if(!transact(returned::send)) {
            return;
        }
        ReceiveMaterialReturned v = new ReceiveMaterialReturned(storeBin, eo);
        v.execute();
        v.receive(returned);
    }

    private void processOld() {
        new ReceiveMaterialReturned(storeBin, eo).execute();
    }

    private class Select extends MultiSelectGrid<InventoryItem> {

        private final boolean confirm;
        private final DateField dateField = new DateField();

        public Select(List<InventoryItem> items, boolean confirm) {
            super(InventoryItem.class, items,
                    StringList.create("PartNumber", "SerialNumber", "Quantity", "Location"),
                    selectedSet -> ReceiveReturnedItems.this.process(items, selectedSet, confirm));
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
            buttonLayout.add(new ELabel("Date: "), dateField);
            if(!confirm) {
                return;
            }
            proceed.setText("Selected Entries");
            Button b = new Button("Previous Entries", VaadinIcon.COG_O, e -> {
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
            ELabel h = new ELabel("These items will be received, please double-check and confirm! Undo not possible after this step!!", "red");
            prependHeader().join().setComponent(h);
        }

        @Override
        protected boolean validate() {
            clearAlerts();
            date = dateField.getValue();
            if(date.before(DateUtility.addDay(DateUtility.today(), -2)) || date.after(DateUtility.endOfToday())) {
                warning("Please check the receipt date: " + DateUtility.formatWithTime(date));
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
