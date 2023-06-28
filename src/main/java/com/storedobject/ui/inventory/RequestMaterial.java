package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialRequest;
import com.storedobject.core.MaterialRequestItem;

public class RequestMaterial extends BaseRequestMaterial<MaterialRequest, MaterialRequestItem> {

    public RequestMaterial() {
        this(SelectLocation.get(0, 4, 5, 10, 11, 16));
    }

    public RequestMaterial(String from) {
        this(ParameterParser.itemTypeClass(from), ParameterParser.location(from, 0, 4, 5, 10, 11, 16));
    }

    public RequestMaterial(InventoryLocation from) {
        this(null, from, null);
    }

    public RequestMaterial(InventoryLocation from, InventoryLocation to) {
        this(null, from, to);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from) {
        this(itemTypeClass, from, null);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from,
                           InventoryLocation to) {
        super(MaterialRequest.class, itemTypeClass, from, to);
    }
}
