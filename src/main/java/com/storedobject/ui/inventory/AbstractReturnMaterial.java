package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public abstract class AbstractReturnMaterial extends
        AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public AbstractReturnMaterial(String fromLocation) {
        super(MaterialReturned.class, MaterialReturnedItem.class, fromLocation, false);
    }

    public AbstractReturnMaterial(InventoryLocation fromLocation) {
        this(fromLocation, null);
    }

    public AbstractReturnMaterial(InventoryLocation fromLocation, InventoryLocation otherLocation) {
        super(MaterialReturned.class, MaterialReturnedItem.class, fromLocation, false, otherLocation);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setFixedFilter("Status<2");
    }
}
