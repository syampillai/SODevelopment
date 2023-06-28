package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialRequest;
import com.storedobject.core.MaterialRequestItem;

public class ProcessMaterialRequest extends BaseProcessMaterialRequest<MaterialRequest, MaterialRequestItem> {

    public ProcessMaterialRequest() {
        super(MaterialRequest.class);
    }

    public ProcessMaterialRequest(String store) {
        super(MaterialRequest.class, store);
    }

    public ProcessMaterialRequest(InventoryLocation store) {
        super(MaterialRequest.class, store);
    }
}
