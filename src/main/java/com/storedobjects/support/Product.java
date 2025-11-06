package com.storedobjects.support;

import com.storedobject.core.*;

public class Product extends Name {

    public Product() {
    }

    public static void columns(Columns columns) {}

    public static String[] links() {
        return new String[] {
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
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
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
    }
}
