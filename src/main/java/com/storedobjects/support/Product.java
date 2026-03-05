package com.storedobjects.support;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Product extends Name {

    private static final Map<Id, Product> cache = new HashMap<>();
    private boolean internal;

    public Product() {
    }

    public static void columns(Columns columns) {
        columns.add("Internal", "boolean");
    }

    public static String[] links() {
        return new String[] {
                "Modules/Categories|com.storedobjects.support.ProductModule|||0",
                "Product/Service Types|com.storedobject.core.InventoryItemType/Any|||0",
        };
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    @Column(order = 2000, caption = "Internal Task")
    public boolean getInternal() {
        return internal;
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
    public void validateDelete() throws Exception {
        super.validateDelete();
        Transaction t = getTransaction();
        List<ProductSkill> pss = list(ProductSkill.class, "Product=" + getId()).toList();
        for(ProductSkill ps : pss) {
            ps.delete(t);
        }
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        if(!deleted()) createSkills();
        Issue.approvers.clear();
        cache.remove(getId());
    }

    private void createSkills() throws Exception {
        ProductSkill ps;
        Transaction t = getTransaction();
        for (int level = 0; level < ProductSkill.getSkillLevelValues().length; level++) {
            ps = get(ProductSkill.class, "Product=" + getId() + " AND SkillLevel=" + level);
            if (ps == null) {
                ps = new ProductSkill();
                ps.setProduct(getId());
                ps.setSkillLevel(level);
                ps.save(t);
            }
        }
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
