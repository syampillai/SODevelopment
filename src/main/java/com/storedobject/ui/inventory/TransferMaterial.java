package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialTransferred;
import com.storedobject.core.MaterialTransferredItem;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class TransferMaterial extends AbstractSendAndReceiveMaterial<MaterialTransferred, MaterialTransferredItem> {

    public TransferMaterial() {
        this(SelectLocation.get(ALL_TYPES));
    }

    public TransferMaterial(String from) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, from, false);
    }

    public TransferMaterial(InventoryLocation from) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, from, false);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setFixedFilter("Status<2");
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchLocation().execute());
    }

    private class SwitchLocation extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Location", getLocationFrom());
        private final LocationField newLoc = LocationField.create("Change to", ALL_TYPES);

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
            TransferMaterial.this.close();
            new TransferMaterial(loc).execute();
            return true;
        }
    }

    @Override
    protected void selectLocation() {
        new SelectLocation(ALL_TYPES).execute();
    }
}
