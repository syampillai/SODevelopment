package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialTransferred;
import com.storedobject.core.MaterialTransferredItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class TransferMaterial extends AbstractSendAndReceiveMaterial<MaterialTransferred, MaterialTransferredItem> {

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
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setExtraFilter("Status<2");
    }
}
