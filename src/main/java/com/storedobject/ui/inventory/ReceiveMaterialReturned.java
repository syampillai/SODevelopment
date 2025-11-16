package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

public final class ReceiveMaterialReturned extends AbstractReceiveMaterialReturned {

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

    private class SwitchStore extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Store", getLocationTo());
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
            var action = ReceiveMaterialReturned.this.getExitAction();
            ReceiveMaterialReturned.this.setExitAction(null);
            ReceiveMaterialReturned.this.close();
            ReceiveMaterialReturned r = new ReceiveMaterialReturned(loc);
            r.setExitAction(action);
            r.execute();
            return true;
        }
    }
}
