package com.storedobjects.support;

import com.storedobject.core.*;

import java.util.HashMap;
import java.util.Map;

public final class ProductModule extends Name {

    private static final Map<Id, ProductModule> cache = new HashMap<>();

    public ProductModule() {
    }

    public static void columns(Columns columns) {}

    public static ProductModule get(String name) {
        return StoredObjectUtility.get(ProductModule.class, "Name", name, false);
    }

    public static ObjectIterator<ProductModule> list(String name) {
        return StoredObjectUtility.list(ProductModule.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        cache.remove(getId());
    }

    public static ProductModule get(Id id) {
        if(Id.isNull(id)) {
            return null;
        }
        ProductModule o = cache.get(id);
        if(o == null) {
            o = get(ProductModule.class, id);
            cache.put(id, o);
        }
        return o;
    }
}
