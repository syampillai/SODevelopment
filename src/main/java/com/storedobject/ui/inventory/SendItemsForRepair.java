package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryRO;
import com.storedobject.core.InventoryROItem;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class SendItemsForRepair extends AbstractSendAndReceiveMaterial<InventoryRO, InventoryROItem> {

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
        buttonPanel.add(h);
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
