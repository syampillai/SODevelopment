package com.storedobjects.support;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class ProductSkill extends StoredObject {

    private static final String[] skillLevelValues =
            new String[] {
                    "Level 1", "Level 2", "Level 3",
            };
    private Id productId;
    private int skillLevel = 0;

    public ProductSkill() {
    }

    public static void columns(Columns columns) {
        columns.add("Product", "id");
        columns.add("SkillLevel", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Product, SkillLevel", true);
    }

    public String getUniqueCondition() {
        return "Product=" + getProductId() + " AND " + "SkillLevel=" + getSkillLevel();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setProduct(Id productId) {
        if (!loading() && !Id.equals(this.getProductId(), productId)) {
            throw new Set_Not_Allowed("Product");
        }
        this.productId = productId;
    }

    public void setProduct(BigDecimal idValue) {
        setProduct(new Id(idValue));
    }

    public void setProduct(Product product) {
        setProduct(product == null ? null : product.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getProductId() {
        return productId;
    }

    public Product getProduct() {
        return getRelated(Product.class, productId);
    }

    public void setSkillLevel(int skillLevel) {
        if (!loading()) {
            throw new Set_Not_Allowed("Skill Level");
        }
        this.skillLevel = skillLevel;
    }

    @SetNotAllowed
    @Column(order = 200)
    public int getSkillLevel() {
        return skillLevel;
    }

    public static String[] getSkillLevelValues() {
        return skillLevelValues;
    }

    public static String getSkillLevelValue(int value) {
        String[] s = getSkillLevelValues();
        return s[value % s.length];
    }

    public String getSkillLevelValue() {
        return getSkillLevelValue(skillLevel);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        productId = tm.checkType(this, productId, Product.class, false);
        super.validateData(tm);
    }

    @Override
    public String toString() {
        return getProduct().toDisplay() + " (Level " + skillLevel + ")";
    }
}
