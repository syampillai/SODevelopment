package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialRequest;
import com.storedobject.core.MaterialRequestItem;

public class ReceiveMaterialRequested extends BaseReceiveMaterialRequested<MaterialRequest, MaterialRequestItem> {

    public ReceiveMaterialRequested(String from) {
        super(MaterialRequest.class, from);
    }

    public ReceiveMaterialRequested(InventoryLocation from) {
        super(MaterialRequest.class, from);
    }
}
