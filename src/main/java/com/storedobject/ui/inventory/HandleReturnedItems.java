package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle items at external organization / custody that were sent to them earlier
 * (Sent for repairs, rented out, custody etc.)
 *
 * @author Syam
 */
public abstract class HandleReturnedItems extends DataForm implements Transactional {

    private final String SPECIFIC = "Select items from specific ";
    InventoryStoreBin storeBin;
    InventoryLocation eo; // External organization or custody
    final LocationField storeField;
    final LocationField eoField;
    final int type;
    private Button goToOld;
    private boolean defineReplacements = false;
    final boolean autoMode;
    private final Checkbox selectSpecific, includeOnlyForStore;
    private final Set<Id> specificItems = new HashSet<>();

    public HandleReturnedItems(String caption, int type, InventoryStoreBin storeBin, InventoryLocation eo, boolean allowJumpToOld) {
        this(caption, type, storeBin, eo, allowJumpToOld, true);
    }

    public HandleReturnedItems(String caption, int type, InventoryStoreBin storeBin, InventoryLocation eo, boolean allowJumpToOld, boolean autoMode) {
        super(caption);
        this.type = type;
        this.autoMode = autoMode;
        this.storeBin = storeBin;
        this.eo = eo;
        if(storeBin == null || !autoMode) {
            storeField = LocationField.create(0);
            if(storeBin != null) {
                storeField.setValue(storeBin);
            } else if(storeField.getLocationCount() == 1) {
                this.storeBin = (InventoryStoreBin) storeField.getValue();
            }
        } else {
            storeField = LocationField.create(storeBin);
        }
        storeField.setLabel("Items Sent from");
        if(eo == null || !autoMode) {
            eoField = LocationField.create(type);
            if(eo != null) {
                eoField.setValue(eo);
            } else if(eoField.getLocationCount() == 1) {
                this.eo = eoField.getValue();
            } else {
                eoField.setValue((Id)null);
            }
        } else {
            if(eo.getType() != type) {
                throw new SORuntimeException("Incorrect - " + eo.getTypeValue());
            }
            eoField = LocationField.create(eo);
        }
        eoField.setLabel(type == 18 ? "Custodian" : "Organization");
        addField(storeField, eoField);
        if(storeBin != null && autoMode) {
            setFieldReadOnly(storeField);
        }
        if(eo != null && autoMode) {
            setFieldReadOnly(eoField);
        }
        setRequired(storeField);
        setRequired(eoField);
        if(allowJumpToOld) {
            goToOld = new Button("Previous Entries", VaadinIcon.COG_O, e -> goToOld());
        }
        selectSpecific = new Checkbox(SPECIFIC + label());
        switch (type) {
            case 3, 8 -> {
                add(selectSpecific);
                selectSpecific.addValueChangeListener(e -> selectSpecific());
            }
        }
        includeOnlyForStore = new Checkbox("Include only items sent from the selected store and replacements");
        add(includeOnlyForStore);
        storeField.addValueChangeListener(e -> this.storeBin = (InventoryStoreBin) e.getValue());
        eoField.addValueChangeListener(e -> {
            this.eo = e.getValue();
            selectSpecific.setValue(false);
        });
    }

    private String label() {
        return switch(type) {
            case 3 -> "RO";
            case 8 -> "LO";
            default -> "R";
        } + "s";
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        if(goToOld == null) {
            return;
        }
        buttonPanel.remove(cancel);
        buttonPanel.add(goToOld,
                new Button("Define Replacements / Mark Consumption", VaadinIcon.EXCHANGE, e -> defineReplacements()),
                cancel);
    }

    @Override
    public int getMinimumContentWidth() {
        return 55;
    }

    static InventoryLocation eoName(String storeAndEOName, int type) {
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
        if(autoMode && storeBin != null && eo != null) {
            proceed();
            return;
        }
        super.execute(parent, doNotLock);
    }

    private void goToOld() {
        if(set()) {
            processOld();
        }
    }

    private boolean set() {
        return set(true);
    }

    private boolean set(boolean close) {
        if(storeBin == null) {
            storeBin = (InventoryStoreBin) storeField.getValue();
        }
        if(eo == null) {
            eo = eoField.getValue();
        }
        clearAlerts();
        if(storeBin == null || eo == null) {
            message("Please select both the store and " + (type == 18 ? "custodian" : "organization") + "!");
            return false;
        }
        if(close) close();
        return true;
    }

    private void defineReplacements() {
        if(set()) {
            defineReplacements = true;
            proceed();
        }
    }

    private void selectSpecific() {
        if(!selectSpecific.getValue()) {
            specificItems.clear();
            selectSpecific.setLabel(SPECIFIC + label());
            return;
        }
        if(!set(false)) {
            selectSpecific.setValue(false);
            return;
        }
        String condition = "Status<2 AND Amendment>0 AND ToLocation=" + eo.getId();
        if(includeOnlyForStore.getValue()) {
            condition += " AND FromLocation=" + storeBin.getId();
        }
        ObjectIterator<InventoryReturn> irs;
        if(type == 3) {
            irs = StoredObject.list(InventoryRO.class, condition, true).map(r -> r);
        } else {
            irs = StoredObject.list(InventoryLoanOut.class, condition, true).map(r -> r);
        }
        List<InventoryReturn> list = irs.toList();
        if(list.isEmpty()) {
            message("No items pending to be received from:<BR/>" + eo.toDisplay());
            selectSpecific.setValue(false);
            return;
        }
        new SelectSpecificIR(list).execute();
    }

    @Override
    protected boolean process() {
        if(set()) {
            proceed();
            return true;
        }
        return false;
    }

    private void proceed() {
        List<Id> amends = new ArrayList<>();
        StoredObject.list(InventoryTransfer.class,
                        "Status=0 AND Amendment>0 AND ToLocation=" + eo.getId(), true)
                .forEach(it -> it.listLinks(InventoryTransferItem.class, true)
                        .forEach(iti -> amends.add(iti.getItemId())));
        AtomicBoolean amended = new AtomicBoolean(false);
        ObjectIterator<InventoryItem> itemIterator = StoredObject.list(InventoryItem.class, "Location=" + eo.getId(), true);
        if(selectSpecific.getValue()) {
            itemIterator = itemIterator.filter(i -> specificItems.contains(i.getId()));
        }
        boolean storeOnly = includeOnlyForStore.getValue();
        List<InventoryItem> items = itemIterator.filter(i -> {
                    if(validLoc(i, storeOnly)) {
                        if(!amends.contains(i.getId())) {
                            return true;
                        }
                        amended.set(true);
                    }
                    return false;
                })
                .filter( i -> !amends.contains(i.getId()) && validLoc(i, storeOnly)).toList();
        if(items.isEmpty()) {
            message("No items pending to be received from:<BR/>" + eo.toDisplay());
            if(amended.get()) {
                warning("Note: Amended entries exist that require attention!");
            }
            if(!defineReplacements) {
                processOld();
                return;
            }
        }
        if(defineReplacements) {
            new DefineReplacementItems.ReplacementGrid(items, eo, getTransactionManager(), storeBin).execute();
        } else {
            proceed(items);
        }
    }

    protected abstract void processOld();

    protected abstract void proceed(List<InventoryItem> items);

    private boolean validLoc(InventoryItem ii, boolean includeOnlyForStore) {
        if(!includeOnlyForStore) {
            return true;
        }
        InventoryLocation pLoc = ii.getPreviousLocation();
        if(pLoc == null) {
            return true; // Should not happen
        }
        // The previous location was an outside location:
        // Check if it was moved from there to the current location.
        // This can happen if the outside location was not correct and the entry was amended to correct it.
        int type = pLoc.getType();
        if(switch (type) {
            case 3, 8, 18 -> true; // RO, Loan out, Tools
            default -> false;
        }) {
            int step = 0;
            while (switch (type) {
                case 3, 8, 18 -> true; // RO, Loan out, Tools
                default -> false;
            }) {
                ++step;
                pLoc = ii.getPreviousLocation(step);
                if (pLoc == null) {
                    return false; // Should not happen
                }
                type = pLoc.getType();
            }
        }
        return pLoc instanceof InventoryBin bin && bin.getStoreId().equals(storeBin.getStoreId());
    }

    private void loadSpecificItems(Set<InventoryReturn> irs) {
        specificItems.clear();
        if(irs.isEmpty()) {
            selectSpecific.setValue(false);
            return;
        }
        StringBuilder label = new StringBuilder(SPECIFIC);
        label.append(label()).append(" - ");
        int count = 0;
        for(InventoryReturn ir: irs) {
            if(count++ > 0) {
                label.append(", ");
            }
            if(count < 4) {
                label.append(ir.getReference()).append(" (").append(DateUtility.format(ir.getDate())).append(")");
            }
            if(type == 3) {
                ir.listLinks(InventoryROItem.class, true).forEach(i -> specificItems.add(i.getItemId()));
            } else if(type == 8) {
                ir.listLinks(InventoryLoanOutItem.class, true).forEach(i -> specificItems.add(i.getItemId()));
            }
        }
        selectSpecific.setLabel(label.toString());
    }

    private class SelectSpecificIR extends MultiSelectGrid<InventoryReturn> {

        public SelectSpecificIR(List<InventoryReturn> items) {
            super(InventoryReturn.class, items, StringList.create("Date", "Reference", "FromLocation AS Sent from"),
                    HandleReturnedItems.this::loadSpecificItems);
        }

        @Override
        protected void cancel() {
            super.cancel();
            selectSpecific.setValue(false);
        }

        @Override
        public void createHeaders() {
            prependHeader().join().setComponent(new ButtonLayout(new ELabel("Location: "),
                    new ELabel(eo.toDisplay(), Application.COLOR_SUCCESS)));
        }
    }
}
