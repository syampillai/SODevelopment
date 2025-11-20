package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryStore;

public class Rebin extends SelectStore {

    public Rebin() {
        super("Re-bin", Rebin::rebin);
    }

    private static void rebin(InventoryStore store) {
        LocateItem locateItem = new LocateItem(true);
        locateItem.setStore(store);
        locateItem.execute();
    }
}
