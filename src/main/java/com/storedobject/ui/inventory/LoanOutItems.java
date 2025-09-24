package com.storedobject.ui.inventory;

import com.storedobject.core.*;

public final class LoanOutItems extends SendItemsOut<InventoryLoanOut, InventoryLoanOutItem> {

    public LoanOutItems() {
        this(SelectStore.get());
    }

    public LoanOutItems(String from) {
        super(InventoryLoanOut.class, InventoryLoanOutItem.class, from);
    }

    public LoanOutItems(InventoryLocation from) {
        super(InventoryLoanOut.class, InventoryLoanOutItem.class, from);
    }
}
