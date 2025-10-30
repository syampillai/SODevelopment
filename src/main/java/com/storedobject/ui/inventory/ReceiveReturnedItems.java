package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
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
public class ReceiveReturnedItems extends HandleReturnedItems {

    private final Application application;
    private Date date, invoiceDate;
    private String invoiceRef;
    private Runnable cancelAction;

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
        this(type, storeBin, eo, true);
    }

    public ReceiveReturnedItems(int type, InventoryStoreBin storeBin, InventoryLocation eo, boolean autoMode) {
        super(caption(type), type, storeBin, eo, true, autoMode);
        this.application = getApplication();
    }

    public void setCancelAction(Runnable cancelAction) {
        this.cancelAction = cancelAction;
    }

    @Override
    protected void cancel() {
        super.cancel();
        if(cancelAction != null) {
            getApplication().access(() -> cancelAction.run());
        }
    }

    private static String caption(int type) {
        String caption = switch(type) {
            case 3 -> "Receive Repaired Items";
            case 8 -> "Receive Loan/Rent/Lease Returns";
            case 18 -> "Receive Tools Returned";
            default -> null;
        };
        if(caption == null) {
            throw new SORuntimeException("Invalid type: " + InventoryLocation.getTypeValue(type));
        }
        return caption;
    }

    @Override
    protected void proceed(List<InventoryItem> items) {
        new Select(items, true).execute();
    }

    private void process(List<InventoryItem> allItems, Set<InventoryItem> selectedItems, boolean confirm) {
        MaterialReturned returnedToProcess = null;
        InventoryItem item;
        List<MaterialReturnedItem> returnedItems;
        List<MaterialReturned> returns = StoredObject.list(MaterialReturned.class,
                "FromLocation=" + eo.getId() + " AND Status<2", true).toList();
        for(MaterialReturned returned: returns) {
            returnedItems = returned.listLinks(MaterialReturnedItem.class, true).toList();
            for(MaterialReturnedItem mri: returnedItems) {
                item = mri.getItem();
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
        recRet().receive(returnedToProcess);
    }

    private ReceiveMaterialReturned recRet() {
        ReceiveMaterialReturned v = new ReceiveMaterialReturned(storeBin, eo);
        v.setExitAction(() -> new ReceiveReturnedItems(type, storeBin, eo, false).execute());
        v.execute();
        return v;
    }

    protected void processOld() {
        recRet();
    }

    private class Select extends MultiSelectGrid<InventoryItem> {

        private final boolean confirm;
        private final DateField dateField = new DateField();
        private final TextField invoiceRefField = new TextField();
        private final DateField invoiceDateField = new DateField();

        public Select(List<InventoryItem> items, boolean confirm) {
            super(InventoryItem.class, items,
                    StringList.create("PartNumber", "SerialNumberDisplay AS Serial/Batch Number", "Quantity"),
                    selectedSet -> ReceiveReturnedItems.this.process(items, selectedSet, confirm));
            addConstructedListener(o -> con());
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
            setWidth("75vw");
        }

        private void con() {
            getColumn("Quantity").setFlexGrow(0).setWidth("100%");
            getColumn("SerialNumberDisplay").setFlexGrow(0).setWidth("100px");
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
            proceed.asSmall();
            cancel.asSmall();
            Button b = new Button("Show Previous Entries", VaadinIcon.COG_O, e -> {
                clearAlerts();
                close();
                processOld();
            }).asSmall();
            Button eoButton = new Button(((InventoryVirtualLocation)eo).getEntity().toDisplay(), (String)null, e -> cancel())
                    .asSmall();
            eoButton.getElement().setAttribute("title", "Click to change");
            buttonLayout.add(new ELabel("Location: "),
                    eoButton,
                    new ELabel(" | ", Application.COLOR_INFO),
                    new ELabel("Process:"),
                    b);
        }

        @Override
        public void createHeaders() {
            SearchField searchField = new SearchField(this::search).toUpperCase().trim();
            searchField.setPlaceholder("P/N or S/N");
            if(type == 3) { // RO
                prependHeader().join().setComponent(new ButtonLayout(new ELabel("Search: "), searchField,
                        new ELabel("Date: "), dateField,
                        new ELabel(" Invoice No.: "), invoiceRefField,
                        new ELabel(" Invoice Date: "),
                        invoiceDateField));
            } else {
                prependHeader().join().setComponent(new ButtonLayout(new ELabel("Search: "), searchField));
            }
            ELabel warn = new ELabel("If you are receiving replacements or lesser quantities, please define the", Application.COLOR_ERROR);
            warn.newLine().append("replacement items or input consumption details before going ahead with this.",
                    Application.COLOR_ERROR).update();
            prependHeader().join()
                    .setComponent(new ButtonLayout(
                            warn,
                            new Button("Define Replacements / Mark Consumption", VaadinIcon.EXCHANGE, e -> defineReplacements()).asSmall()
                    ));
            if(confirm) return;
            ELabel h = new ELabel(
                    "Warning: These items will be received, please double-check and confirm! Undo not possible after this step!!",
                    Application.COLOR_ERROR);
            prependHeader().join().setComponent(h);
        }

        private void defineReplacements() {
            close();
            new DefineReplacementItems.ReplacementItems(type, storeBin, eo).execute();
        }

        private void search(String text) {
            if(text.isEmpty()) {
                clearViewFilters();
                return;
            }
            setViewFilter(ii -> ii.getSerialNumber().contains(text)
                    || ii.getPartNumber().getPartNumber().contains(text));
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

        @Override
        protected void cancel() {
            super.cancel();
            if(cancelAction != null) {
                application.access(() -> cancelAction.run());
            } else {
                application.access(() -> new ReceiveReturnedItems(type, storeBin, eo, false).execute());
            }
        }
    }
}
