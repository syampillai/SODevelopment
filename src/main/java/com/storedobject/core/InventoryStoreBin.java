package com.storedobject.core;

public final class InventoryStoreBin extends InventoryBin {

    public InventoryStoreBin() {
    }

    public static void columns(Columns columns) {
    }

    public static InventoryStoreBin getForStore(Id storeId) {
        return Id.isNull(storeId) ? null : get(InventoryStoreBin.class, "Store=" + storeId);
    }

    public static InventoryStoreBin getForStore(InventoryStore store) {
        return store == null ? null : getForStore(store.getId());
    }
}
