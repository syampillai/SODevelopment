package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class ReturnMaterial extends AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public ReturnMaterial() {
        super(MaterialReturned.class, MaterialReturnedItem.class, (String) null, false);
    }

    public ReturnMaterial(String from) {
        super(MaterialReturned.class, MaterialReturnedItem.class, from, false);
    }

    public ReturnMaterial(InventoryLocation from) {
        super(MaterialReturned.class, MaterialReturnedItem.class, from, false);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setExtraFilter("Status<2");
    }
}
