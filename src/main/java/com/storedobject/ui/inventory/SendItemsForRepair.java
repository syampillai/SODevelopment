package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditorProvider;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

public final class SendItemsForRepair extends AbstractSendAndReceiveMaterial<InventoryRO, InventoryROItem>
        implements ObjectEditorProvider, ProducesGRN {

    private final Button goToGRNs = new Button("GRNs", VaadinIcon.STOCK, e -> toGRNs());
    private final Button receiveItems = new Button("Receive", VaadinIcon.STORAGE, e -> receiveItems());
    private boolean forGRN = false;

    public SendItemsForRepair() {
        this(SelectStore.get());
    }

    public SendItemsForRepair(String from) {
        super(InventoryRO.class, InventoryROItem.class, from, false);
    }

    public SendItemsForRepair(InventoryLocation from) {
        super(InventoryRO.class, InventoryROItem.class, from, false);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(receiveItems, h, goToGRNs);
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

    public void setForGRNs() {
        this.forGRN = true;
        if(add != null) {
            add.setVisible(false);
            add = null;
        }
        if(edit != null) {
            edit.setVisible(false);
            edit = null;
        }
    }

    private void toGRNs() {
        GRN grnView = new GRN(3, ((InventoryStoreBin) getLocationFrom()).getStore());
        grnView.setPOClass(getClass());
        if(!forGRN) {
            grnView.setFromROs();
            grnView.setAllowSwitchStore(true);
        }
        grnView.setGRNProducer(this);
        grnView.setEditorProvider(this);
        close();
        grnView.execute();
    }

    private void receiveItems() {
        close();
        new ReceiveReturnedItems(3, ((InventoryStoreBin) getLocationFrom()).getStore()).execute();
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
            SendItemsForRepair.this.close();
            new SendItemsForRepair(loc).execute();
            return true;
        }
    }
}
