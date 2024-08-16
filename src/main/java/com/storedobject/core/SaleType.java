package com.storedobject.core;

import java.util.HashMap;
import java.util.Map;

public final class SaleType extends AbstractTradeType {

    private static final Map<Integer, SaleType> cache = new HashMap<>();

    public SaleType() {}

    public static void columns(Columns columns) {}

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static SaleType get(String name) {
        return StoredObjectUtility.get(SaleType.class, "Name", name, false);
    }

    public static ObjectIterator<SaleType> list(String name) {
        return StoredObjectUtility.list(SaleType.class, "Name", name, false);
    }

    @Override
    public void saved() throws Exception {
        cache.remove(getType());
    }

    public static SaleType get(int type) {
        SaleType st = cache.get(type);
        if(st == null) {
            st = get(SaleType.class, "Type=" + type);
            if(st != null) {
                cache.put(type, st);
            }
        }
        return st;
    }
}
