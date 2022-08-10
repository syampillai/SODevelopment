package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStoreBin;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.icon.VaadinIcon;

public final class ReceiveMaterialReturned extends AbstractReceiveMaterialReturned {

    private final Button goToROs = new Button("ROs", VaadinIcon.FILE_TABLE, e -> gotToROs());

    public ReceiveMaterialReturned() {
        this(SelectStore.get());
    }

    public ReceiveMaterialReturned(String to) {
        super(to);
    }

    public ReceiveMaterialReturned(InventoryLocation to) {
        super(to);
    }

    public ReceiveMaterialReturned(InventoryLocation to, InventoryLocation otherLocation) {
        super(to, otherLocation);
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchStore().execute());
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        InventoryLocation storeLoc = getLocationTo();
        if(storeLoc instanceof InventoryStoreBin && getLocationFrom().getType() == 3) {
            buttonPanel.add(goToROs);
        }
    }

    private void gotToROs() {
        close();
        new SendItemsForRepair(getLocationTo()).execute();
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
            ReceiveMaterialReturned.this.close();
            new ReceiveMaterialReturned(loc).execute();
            return true;
        }
    }
}
