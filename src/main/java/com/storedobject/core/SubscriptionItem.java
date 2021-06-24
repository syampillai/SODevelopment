package com.storedobject.core;

import java.sql.Date;

public class SubscriptionItem extends InventoryItem {

    public SubscriptionItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setDateFrom(Date dateFrom) {
    }

    public Date getDateFrom() {
        return new Date(0);
    }

    public void setDateTo(Date dateTo) {
    }

    public Date getDateTo() {
        return new Date(0);
    }

    public void setSubscriptionNumber(String subscriptionNumber) {
    }

    public String getSubscriptionNumber() {
        return "";
    }

    @Override
    public SubscriptionItemType getPartNumber() {
        return(SubscriptionItemType) super.getPartNumber();
    }

    public static SubscriptionItem get(String serial, SubscriptionItemType itemType) {
        return InventoryItem.get(SubscriptionItem.class, serial, itemType);
    }

    public static ObjectIterator <? extends SubscriptionItem> list(String serial, SubscriptionItemType itemType) {
        return InventoryItem.list(SubscriptionItem.class, serial, itemType);
    }
}