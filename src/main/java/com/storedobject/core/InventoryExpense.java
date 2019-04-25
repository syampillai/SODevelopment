package com.storedobject.core;

import java.sql.Date;
import java.math.BigDecimal;

public abstract class InventoryExpense extends StoredObject {

    public InventoryExpense() {
    }

    public static void columns(Columns columns) {
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return null;
    }

    public void setType(Id typeId) {
    }

    public void setType(BigDecimal idValue) {
    }

    public void setType(InventoryDocumentConfigurationDetail type) {
    }

    public Id getTypeId() {
        return null;
    }

    public InventoryDocumentConfigurationDetail getType() {
        return null;
    }

    public void setAmount(Money amount) {
    }

    public void setAmount(Object moneyValue) {
    }

    public Money getAmount() {
        return null;
    }
}