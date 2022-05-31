package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

public final class ReturnMaterial extends AbstractReturnMaterial {

    public ReturnMaterial() {
        this(SelectLocation.get(ALL_TYPES));
    }

    public ReturnMaterial(String from) {
        super(from);
    }

    public ReturnMaterial(InventoryLocation from) {
        this(from, null);
    }

    public ReturnMaterial(InventoryLocation from, InventoryLocation otherLocation) {
        super(from, otherLocation);
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchLocation().execute());
    }

    private class SwitchLocation extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Location", getLocationFrom());
        private final LocationField newLoc = LocationField.create("Change to", 0, 4, 5, 10, 11, 16);

        public SwitchLocation() {
            super("Change Location");
            addField(currentLoc, newLoc);
            setFieldReadOnly(currentLoc);
            setRequired(newLoc);
        }

        @Override
        protected boolean process() {
            close();
            InventoryLocation loc = newLoc.getValue();
            if(loc.getId().equals(currentLoc.getObjectId())) {
                message("Not changed!");
                return true;
            }
            message("Location changed to '" + loc.toDisplay() + "'");
            ReturnMaterial.this.close();
            new ReturnMaterial(loc).execute();
            return true;
        }
    }

    @Override
    protected void selectLocation() {
        new SelectLocation(0, 4, 5, 10, 11, 16).execute();
    }
}
