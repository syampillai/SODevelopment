package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public final class ReturnMaterial extends AbstractReturnMaterial {

    public ReturnMaterial(String from) {
        super(from);
    }

    public ReturnMaterial(InventoryLocation from) {
        super(from);
    }
}
