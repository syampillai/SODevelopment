package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialTransferred;
import com.storedobject.core.MaterialTransferredItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class ReceiveMaterialTransferred extends AbstractSendAndReceiveMaterial<MaterialTransferred, MaterialTransferredItem> {

    public ReceiveMaterialTransferred() {
        super(MaterialTransferred.class, MaterialTransferredItem.class, (String) null, true);
    }

    public ReceiveMaterialTransferred(String to) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, to, true);
    }

    public ReceiveMaterialTransferred(InventoryLocation to) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, to, true);
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
}
