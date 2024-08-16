package com.storedobject.core;

import java.util.HashMap;
import java.util.Map;

public final class PurchaseType extends AbstractTradeType {

    private static final Map<Integer, PurchaseType> cache = new HashMap<>();

    public PurchaseType() {}

    public static void columns(Columns columns) {}

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static PurchaseType get(String name) {
        return StoredObjectUtility.get(PurchaseType.class, "Name", name, false);
    }

    public static ObjectIterator<PurchaseType> list(String name) {
        return StoredObjectUtility.list(PurchaseType.class, "Name", name, false);
    }

    @Override
    public void saved() throws Exception {
        cache.remove(getType());
    }

    public static PurchaseType get(int type) {
        PurchaseType pt = cache.get(type);
        if(pt == null) {
            pt = get(PurchaseType.class, "Type-" + type);
            if(pt != null) {
                cache.put(type, pt);
            }
        }
        return pt;
    }
}
