package com.storedobject.core;

public final class TransactionType extends ShortName {

    public TransactionType() {
    }

    public static void columns(Columns columns) {}

    public static TransactionType get(String name) {
        return StoredObjectUtility.get(TransactionType.class, "Name", name, false);
    }

    public static ObjectIterator<TransactionType> list(String name) {
        return StoredObjectUtility.list(TransactionType.class, "Name", name, false);
    }
}
