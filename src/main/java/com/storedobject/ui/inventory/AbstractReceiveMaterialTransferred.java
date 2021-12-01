package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialTransferred;
import com.storedobject.core.MaterialTransferredItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public abstract class AbstractReceiveMaterialTransferred extends AbstractSendAndReceiveMaterial<MaterialTransferred, MaterialTransferredItem> {

    public AbstractReceiveMaterialTransferred() {
        super(MaterialTransferred.class, MaterialTransferredItem.class, (String) null, true);
    }

    public AbstractReceiveMaterialTransferred(String to) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, to, true);
    }

    public AbstractReceiveMaterialTransferred(InventoryLocation to) {
        super(MaterialTransferred.class, MaterialTransferredItem.class, to, true);
    }

    @Override
    void created() {
        super.created();
        setFixedFilter("Status=1");
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status=1"));
        buttonPanel.add(h);
    }
}
