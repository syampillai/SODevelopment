package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

public final class ReturnMaterial<M extends MaterialReturned, L extends MaterialReturnedItem> extends AbstractReturnMaterial<M, L> {

    public ReturnMaterial(Class<M> mrClass, Class<L> mriClass) {
        this(mrClass, mriClass, SelectLocation.get(ALL_TYPES));
    }

    public ReturnMaterial(Class<M> mrClass, Class<L> mriClass, String from) {
        super(mrClass, mriClass, from);
    }

    public ReturnMaterial(Class<M> mrClass, Class<L> mriClass, InventoryLocation from) {
        this(mrClass, mriClass, from, null);
    }

    public ReturnMaterial(Class<M> mrClass, Class<L> mriClass, InventoryLocation from, InventoryLocation otherLocation) {
        super(mrClass, mriClass, from, otherLocation);
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
            new ReturnMaterial<>(getObjectClass(), getItemClass(), loc, getLocationTo()).execute();
            return true;
        }
    }

    @Override
    protected void selectLocation() {
        new SelectLocation(0, 4, 5, 10, 11, 16).execute();
    }
}
