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
    	return null;
    }

    public void setDateTo(Date dateTo) {
    }

    public Date getDateTo() {
    	return null;
    }

    public void setSubscriptionNumber(String subscriptionNumber) {
    }

    public String getSubscriptionNumber() {
    	return null;
    }
    
    @Override
    public SubscriptionItemType getPartNumber() {
    	return null;
    }

    public static Class <SubscriptionItemType> getPartNumberType() {
    	return null;
    }

    public static SubscriptionItem get(String serial, SubscriptionItemType itemType) {
    	return null;
    }

    public static ObjectIterator <SubscriptionItem> list(String serial, SubscriptionItemType itemType) {
    	return null;
    }
}