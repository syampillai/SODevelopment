package com.storedobject.accounts;

import com.storedobject.core.*;

import java.util.Currency;

public final class NoTax extends TaxMethod {

    public NoTax() {
        name = "No Tax";
    }

    public static void columns(Columns columns) {
    }

    @Override
    public Money getTax(InventoryItemType itemType, Quantity quantity,
                        Money unitCost, Percentage taxRate, Currency localCurrency) {
        return new Money(localCurrency);
    }
}
