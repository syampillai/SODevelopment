package com.storedobject.accounts;

import com.storedobject.core.*;

import java.util.Currency;

public final class FixedTax extends TaxMethod {

    public FixedTax() {
        name = "Fixed Tax";
    }

    public static void columns(Columns columns) {
    }

    @Override
    public Money getTax(InventoryItemType itemType, Quantity quantity,
                        Money unitCost, Percentage taxRate, Currency localCurrency) {
        return new Money(taxRate.getValue(), localCurrency);
    }
}
