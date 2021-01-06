package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.DateField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiveRepairedItems extends DataForm implements Transactional {

    private InventoryLocation storeBin;
    private InventoryLocation ro;
    private final LocationField storeField;
    private final LocationField roField;
    private Date date;

    public ReceiveRepairedItems() {
        this((InventoryStoreBin) null, null);
    }

    public ReceiveRepairedItems(String storeAndROName) {
        this(LocationField.getStore(storeAndROName), roName(storeAndROName));
    }

    public ReceiveRepairedItems(InventoryStore store) {
        this(store, null);
    }

    public ReceiveRepairedItems(InventoryStore store, InventoryLocation ro) {
        this(store.getStoreBin(), ro);
    }

    public ReceiveRepairedItems(InventoryStoreBin storeBin) {
        this(storeBin, null);
    }

    public ReceiveRepairedItems(InventoryStoreBin storeBin, InventoryLocation ro) {
        super("Receive Repaired Items");
        this.storeBin = storeBin;
        this.ro = ro;
        if(storeBin == null) {
            storeField = LocationField.create(0);
            if(storeField.getLocationCount() == 1) {
                this.storeBin = storeField.getValue();
            }
        } else {
            storeField = LocationField.create(storeBin);
        }
        storeField.setLabel("Select Store");
        if(ro == null) {
            roField = LocationField.create(3);
            if(roField.getLocationCount() == 1) {
                this.ro = roField.getValue();
            }
        } else {
            if(ro.getType() != 3) {
                throw new SORuntimeException("Not a repair organization - " + ro.getName());
            }
            roField = LocationField.create(ro);
        }
        roField.setLabel("Repair Organization");
        addField(storeField, roField);
        if(storeBin != null) {
            setFieldReadOnly(storeField);
        }
        if(ro != null) {
            setFieldReadOnly(roField);
        }
        setRequired(storeField);
        setRequired(roField);
    }

    private static InventoryLocation roName(String storeAndROName) {
        if(storeAndROName == null || storeAndROName.isEmpty() || !storeAndROName.contains("|")) {
            return null;
        }
        storeAndROName = storeAndROName.substring(storeAndROName.indexOf('|') + 1).trim();
        if(storeAndROName.isEmpty()) {
            return null;
        }
        return LocationField.getLocation(storeAndROName, 3);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(storeBin != null && ro != null) {
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
        if(ro == null) {
            ro = roField.getValue();
        }
        proceed();
        return true;
    }

    private void proceed() {
        List<InventoryItem> items = StoredObject.list(InventoryItem.class, "Location=" + ro.getId(), true).
                toList();
        if(items.isEmpty()) {
            processOld();
            message("No items pending to be received from:<BR/>" + ro.toDisplay());
            return;
        }
        new Select(items, true).execute();
    }

    private void process(List<InventoryItem> allItems, Set<InventoryItem> selectedItems, boolean confirm) {
        InventoryItem item;
        List<MaterialReturnedItem> returnedItems;
        List<MaterialReturned> returns = StoredObject.list(MaterialReturned.class,
                "FromLocation=" + ro.getId() + " AND Status<2").toList();
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
        returned.setFromLocation(ro);
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
        ReceiveMaterialReturned v = new ReceiveMaterialReturned(storeBin, ro);
        v.execute();
        v.receive(returned);
    }

    private void processOld() {
        new ReceiveMaterialReturned(storeBin, ro).execute();
    }

    private class Select extends MultiSelectGrid<InventoryItem> {

        private final boolean confirm;
        private final DateField dateField = new DateField();

        public Select(List<InventoryItem> items, boolean confirm) {
            super(InventoryItem.class, items,
                    StringList.create("PartNumber", "SerialNumber", "Quantity", "Location"),
                    selectedSet -> ReceiveRepairedItems.this.process(items, selectedSet, confirm));
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
            if(date.before(DateUtility.addDay(DateUtility.today(), -2)) || date.after(DateUtility.today())) {
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
