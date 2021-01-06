package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class ReceiveMaterialReturned extends AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public ReceiveMaterialReturned() {
        super(MaterialReturned.class, MaterialReturnedItem.class, (String) null, true);
    }

    public ReceiveMaterialReturned(String to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }

    public ReceiveMaterialReturned(InventoryLocation to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }

    public ReceiveMaterialReturned(InventoryLocation to, InventoryLocation otherLocation) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true, otherLocation);
    }

    @Override
    public void constructed() {
        super.constructed();
        setExtraFilter("Status=1");
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status=1"));
        buttonPanel.add(h);
    }

    @Override
    public void receive(MaterialReturned entry) {
        super.receive(entry);
    }
}
