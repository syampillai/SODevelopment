package com.storedobjects.support;

import com.storedobject.core.*;

import java.util.HashMap;
import java.util.Map;

public final class Product extends Name {

    private static final Map<Id, Product> cache = new HashMap<>();

    public Product() {
    }

    public static void columns(Columns columns) {}

    public static String[] links() {
        return new String[] {
                "Modules|com.storedobjects.support.ProductModule|||0",
                "Product Types|com.storedobject.core.InventoryItemType/Any|||0",
        };
    }

    public static Product get(String name) {
        return StoredObjectUtility.get(Product.class, "Name", name, false);
    }

    public static ObjectIterator<Product> list(String name) {
        return StoredObjectUtility.list(Product.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        ProductSkill ps;
        Transaction t = getTransaction();
        for(int level = 0; level < ProductSkill.getSkillLevelValues().length; level++) {
            ps = get(ProductSkill.class, "Product=" + getId() + " AND SkillLevel=" + level);
            if(ps == null) {
                ps = new ProductSkill();
                ps.setProduct(getId());
                ps.setSkillLevel(level);
                ps.save(t);
            }
        }
        Issue.approvers.clear();
        cache.remove(getId());
    }

    public static Product get(Id id) {
        if(Id.isNull(id)) {
            return null;
        }
        Product o = cache.get(id);
        if(o == null) {
            o = get(Product.class, id);
            cache.put(id, o);
        }
        return o;
    }
}
