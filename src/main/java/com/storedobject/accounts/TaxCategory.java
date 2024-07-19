package com.storedobject.accounts;

import com.storedobject.core.*;

import java.util.HashMap;
import java.util.Map;

public final class TaxCategory extends Name {

    private static final Map<Id, TaxCategory> categories = new HashMap<>();

    public TaxCategory() {
    }

    public static void columns(Columns columns) {}

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static TaxCategory get(String name) {
        return StoredObjectUtility.get(TaxCategory.class, "Name", name, false);
    }

    public static ObjectIterator<TaxCategory> list(String name) {
        return StoredObjectUtility.list(TaxCategory.class, "Name", name, false);
    }

    @Override
    public void saved() throws Exception {
        categories.remove(getId());
    }

    public static TaxCategory getFor(Id id) {
        TaxCategory tc = categories.get(id);
        if(tc == null) {
            tc = get(TaxCategory.class, id);
            if(tc != null) {
                categories.put(id, tc);
            }
        }
        return tc;
    }
}
