
package com.storedobject.core;

public class MaterialReturned extends InventoryTransfer {

    public MaterialReturned() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] links() {
        return new String[] {
                "Items|com.storedobject.core.MaterialReturnedItem|||0",
        };
    }

    public static String actionPrefixForUI() {
        return "MR";
    }
}
