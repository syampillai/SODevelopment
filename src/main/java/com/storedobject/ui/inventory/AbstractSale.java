package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;

public abstract class AbstractSale<S extends InventorySale, SI extends InventorySaleItem> extends AbstractSendAndReceiveMaterial<S, SI> {

    public AbstractSale(Class<S> saleClass, Class<SI> saleItemClass, String caption) {
        this(saleClass, saleItemClass, SelectLocation.get(ALL_TYPES), caption);
    }

    public AbstractSale(Class<S> saleClass, Class<SI> saleItemClass, String from, String caption) {
        super(saleClass, saleItemClass, from, false, caption);
    }

    public AbstractSale(Class<S> saleClass, Class<SI> saleItemClass, InventoryLocation from, String caption) {
        super(saleClass, saleItemClass, from, false, caption);
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
            AbstractSale.this.close();
            createInstance(loc).execute();
            return true;
        }
    }

    @Override
    protected void selectLocation() {
        new SelectLocation(ALL_TYPES).execute();
    }

    protected abstract AbstractSale<S, SI> createInstance(InventoryLocation location);
}
