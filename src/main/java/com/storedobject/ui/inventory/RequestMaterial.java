package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialRequest;
import com.storedobject.core.MaterialRequestItem;

public class RequestMaterial extends BaseRequestMaterial<MaterialRequest, MaterialRequestItem> {

    public RequestMaterial() {
        super(MaterialRequest.class);
    }

    public RequestMaterial(String from) {
        super(MaterialRequest.class, from);
    }

    public RequestMaterial(InventoryLocation from) {
        super(MaterialRequest.class, from);
    }

    public RequestMaterial(InventoryLocation from, InventoryLocation to) {
        super(MaterialRequest.class, from, to);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from) {
        super(MaterialRequest.class, itemTypeClass, from);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from,
                           InventoryLocation to) {
        super(MaterialRequest.class, itemTypeClass, from, to);
    }
}
