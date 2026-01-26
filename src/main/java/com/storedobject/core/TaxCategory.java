package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

public final class TaxCategory extends Name {

    private static TaxCategory DEFAULT;

    public TaxCategory() {}

    public static void columns(Columns columns) {}

    public static TaxCategory get(String name) {
        return StoredObjectUtility.get(TaxCategory.class, "Name", name, false);
    }

    public static ObjectIterator<TaxCategory> list(String name) {
        return StoredObjectUtility.list(TaxCategory.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static TaxCategory getDefault() {
        if(DEFAULT == null) {
            DEFAULT = list(TaxCategory.class, null, "Id").findFirst();
            if(DEFAULT == null) {
                throw new SORuntimeException("No default tax category found.");
            }
        }
        return DEFAULT;
    }
}
