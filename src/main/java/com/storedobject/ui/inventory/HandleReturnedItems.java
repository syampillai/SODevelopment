package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle items at external organization / custody that were sent to them earlier
 * (Sent for repairs, rented out, custody etc.)
 *
 * @author Syam
 */
public abstract class HandleReturnedItems extends DataForm implements Transactional {

    InventoryStoreBin storeBin;
    InventoryLocation eo; // External organization or custody
    final LocationField storeField;
    final LocationField eoField;
    final int type;
    private Button goToOld;

    public HandleReturnedItems(String caption, int type, InventoryStoreBin storeBin, InventoryLocation eo, boolean allowJumpToOld) {
        super(caption);
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
        storeField.setLabel("Items Sent from");
        if(eo == null) {
            eoField = LocationField.create(type);
            if(eoField.getLocationCount() == 1) {
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
        if(storeBin != null) {
            setFieldReadOnly(storeField);
        }
        if(eo != null) {
            setFieldReadOnly(eoField);
        }
        setRequired(storeField);
        setRequired(eoField);
        if(allowJumpToOld) {
            goToOld = new Button("Previous Entries", VaadinIcon.COG_O, e -> goToOld());
        }
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.remove(cancel);
        buttonPanel.add(goToOld, cancel);
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
        if(storeBin != null && eo != null) {
            proceed();
            return;
        }
        super.execute(parent, doNotLock);
    }

    private void goToOld() {
        set();
        clearAlerts();
        if(storeBin == null || eo == null) {
            message("Please select both the store and " + (type == 18 ? "custodian" : "organization") + "!");
            return;
        }
        close();
        processOld();
    }

    private void set() {
        if(storeBin == null) {
            storeBin = (InventoryStoreBin) storeField.getValue();
        }
        if(eo == null) {
            eo = eoField.getValue();
        }
    }

    @Override
    protected boolean process() {
        close();
        set();
        proceed();
        return true;
    }

    private void proceed() {
        List<Id> amends = new ArrayList<>();
        StoredObject.list(InventoryTransfer.class,
                        "Status=0 AND Amendment>0 AND ToLocation=" + eo.getId(), true)
                .forEach(it -> it.listLinks(InventoryTransferItem.class, true)
                        .forEach(iti -> amends.add(iti.getItemId())));
        AtomicBoolean amended = new AtomicBoolean(false);
        List<InventoryItem> items = StoredObject.list(InventoryItem.class, "Location=" + eo.getId(), true)
                .filter(i -> {
                    if(validLoc(i)) {
                        if(!amends.contains(i.getId())) {
                            return true;
                        }
                        amended.set(true);
                    }
                    return false;
                })
                .filter( i -> !amends.contains(i.getId()) && validLoc(i)).toList();
        if(items.isEmpty()) {
            processOld();
            message("For this store, no items pending to be received from:<BR/>" + eo.toDisplay());
            if(amended.get()) {
                warning("Note: Amended entries exist that require attention!");
            }
            return;
        }
        proceed(items);
    }

    protected abstract void processOld();

    protected abstract void proceed(List<InventoryItem> items);

    private boolean validLoc(InventoryItem ii) {
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
}
