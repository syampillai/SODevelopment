package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditorProvider;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

public abstract class SendItemsOut<R extends InventoryReturn, RI extends InventoryReturnItem> extends AbstractSendAndReceiveMaterial<R, RI>
        implements ObjectEditorProvider {

    private final int type;

    public SendItemsOut(Class<R> returnClass, Class<RI> returnItemClass) {
        this(returnClass, returnItemClass, SelectStore.get());
    }

    public SendItemsOut(Class<R> returnClass, Class<RI> returnItemClass, String from) {
        super(returnClass, returnItemClass, from, false);
        type = type(returnClass);
    }

    public SendItemsOut(Class<R> returnClass, Class<RI> returnItemClass, InventoryLocation from) {
        super(returnClass, returnItemClass, from, false);
        type = type(returnClass);
    }

    private static int type(Class<?> rClass) {
        int type = InventoryRO.class.isAssignableFrom(rClass) ? 3 : (InventoryLoanOut.class.isAssignableFrom(rClass) ? 8 : -1);
        if(type < 0) {
            throw new SORuntimeException("Not a return type: " + rClass.getName());
        }
        return type;
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Button goToGRNs = actionAllowed("GO-TO-GRN") ? new Button("GRNs", VaadinIcon.STOCK, e -> toGRNs()) : null,
                receiveItems = actionAllowed("GO-TO-RECEIVE-ITEMS") ?
                        new Button("Receive", VaadinIcon.STORAGE, e -> receiveItems()) : null,
                closeButton = canClose() ? new ConfirmButton("Close", e -> closeR()) : null;
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(closeButton, receiveItems, h, goToGRNs);
        setFixedFilter("Status<2");
    }

    @Override
    protected void selectLocation() {
        new SelectStore().execute();
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchStore().execute());
    }

    private void closeR() {
        R r = selected();
        if(r == null) {
            return;
        }
        try {
            clearAlerts();
            r.close(getTransactionManager(), true);
            refresh(r);
        } catch (Exception e) {
            warning(e);
        } catch (Throwable e) {
            error(e);
        }
    }

    protected String getGRNBrowserLabel() {
        return switch (type) {
            case 3 -> "ROs";
            case 5 -> "LOs";
            default -> "Rs";
        };
    }

    private void toGRNs() {
        GRN grnView = new GRN(type, ((InventoryStoreBin) getLocationFrom()).getStore());
        grnView.setSource(getGRNBrowserLabel(), getClass(), getObjectClass());
        close();
        grnView.execute();
    }

    public static ObjectBrowser<?> createNew(Class<? extends StoredObject> rClass, InventoryStore store, boolean allowSwitchStore) {
        if(rClass == InventoryRO.class) {
            return new SendItemsForRepair(store.getStoreBin());
        }
        if(rClass == InventoryLoanOut.class) {
            return new LoanOutItems(store.getStoreBin());
        }
        return null;
    }

    private void receiveItems() {
        R r = getSelected();
        close();
        ReceiveReturnedItems rri = new ReceiveReturnedItems(type, ((InventoryStoreBin) getLocationFrom()).getStore(),
                r == null ? null : r.getToLocation());
        final InventoryLocation from = getLocationFrom();
        rri.setCancelAction(() -> switchStore(from));
        rri.execute();
    }

    private void switchStore(InventoryLocation loc) {
        try {
            SendItemsOut<?, ?> sio = getClass().getConstructor(InventoryLocation.class).newInstance(loc);
            close();
            sio.execute();
        } catch (Throwable ignored) {
            warning("Unable to switch store to " + loc.toDisplay());
        }
    }


    private class SwitchStore extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Store", getLocationFrom());
        private final LocationField newLoc = LocationField.create("Change to", 0);

        public SwitchStore() {
            super("Change Store");
            addField(currentLoc, newLoc);
            setFieldReadOnly(currentLoc);
            setRequired(newLoc);
        }

        @Override
        protected boolean process() {
            InventoryLocation loc = newLoc.getValue();
            if(loc.getId().equals(currentLoc.getObjectId())) {
                message("Not changed!");
                return true;
            }
            message("Store changed to '" + loc.toDisplay() + "'");
            close();
            switchStore(loc);
            return true;
        }
    }
}
