package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public abstract class AbstractReceiveMaterialReturned extends AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public AbstractReceiveMaterialReturned(String to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }

    public AbstractReceiveMaterialReturned(InventoryLocation to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }

    public AbstractReceiveMaterialReturned(InventoryLocation to, InventoryLocation otherLocation) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true, otherLocation);
    }

    @Override
    void created() {
        super.created();
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
