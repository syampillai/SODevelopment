package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryRO;
import com.storedobject.core.InventoryROItem;
import com.storedobject.ui.PrintButton;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class SendItemsForRepair extends AbstractSendAndReceiveMaterial<InventoryRO, InventoryROItem> {

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
}
