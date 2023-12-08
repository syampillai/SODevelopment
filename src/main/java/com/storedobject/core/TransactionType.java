package com.storedobject.core;

import java.util.HashMap;
import java.util.Map;

public final class TransactionType extends ShortName {

    private static final Map<String, TransactionType> cache = new HashMap<>();

    public TransactionType() {
    }

    public static void columns(Columns columns) {}

    public static TransactionType get(String name) {
        return StoredObjectUtility.get(TransactionType.class, "Name", name, false);
    }

    public static ObjectIterator<TransactionType> list(String name) {
        return StoredObjectUtility.list(TransactionType.class, "Name", name, false);
    }

    @Override
    protected boolean isCode() {
        return true;
    }

    public static TransactionType getFor(String shortName) {
        shortName = toCode(shortName);
        TransactionType tt = cache.get(shortName);
        if(tt != null) {
            return tt;
        }
        tt = get(TransactionType.class, "lower(ShortName)='" + shortName.toLowerCase() + "'");
        if(tt != null) {
            cache.put(shortName, tt);
        }
        return tt;
    }

    public static TransactionType create(TransactionManager transactionManager, String shortName) throws Exception {
        TransactionType tt = getFor(shortName);
        if(tt != null || transactionManager == null) {
            return tt;
        }
        tt = new TransactionType();
        tt.setShortName(shortName);
        tt.setName(shortName);
        //noinspection ResultOfMethodCallIgnored
        transactionManager.transact(tt::save);
        cache.put(tt.getShortName(), tt);
        return tt;
    }
}
