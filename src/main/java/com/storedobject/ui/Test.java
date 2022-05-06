package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.inventory.RequestMaterial;

public class Test implements Executable {
    @Override
    public void execute() {
        new RequestMaterial((InventoryLocation) null, null).execute();
    }
}
