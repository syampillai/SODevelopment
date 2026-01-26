package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public class TaxDefinition extends StoredObject {

    private Id categoryId;
    private Id customerTaxRegionId = Id.ZERO;

    public TaxDefinition() {}

    public static void columns(Columns columns) {
        columns.add("Category", "id");
        columns.add("CustomerTaxRegion", "id");
    }

    public static void indices(Indices indices) {
        indices.add("CustomerTaxRegion,Category", true);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Applicable Tax Types|com.storedobject.core.TaxType|Region,DisplayOrder||0",
        };
    }

    public void setCategory(Id categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(BigDecimal idValue) {
        setCategory(new Id(idValue));
    }

    public void setCategory(TaxCategory category) {
        setCategory(category == null ? null : category.getId());
    }

    @Column(order = 100)
    public Id getCategoryId() {
        return categoryId;
    }

    public TaxCategory getCategory() {
        return getRelated(TaxCategory.class, categoryId);
    }

    public void setCustomerTaxRegion(Id customerTaxRegionId) {
        this.customerTaxRegionId = customerTaxRegionId;
    }

    public void setCustomerTaxRegion(BigDecimal idValue) {
        setCustomerTaxRegion(new Id(idValue));
    }

    public void setCustomerTaxRegion(TaxRegion customerTaxRegion) {
        setCustomerTaxRegion(customerTaxRegion == null ? null : customerTaxRegion.getId());
    }

    @Column(required = false, order = 200)
    public Id getCustomerTaxRegionId() {
        return customerTaxRegionId;
    }

    public TaxRegion getCustomerTaxRegion() {
        return getRelated(TaxRegion.class, customerTaxRegionId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        categoryId = tm.checkType(this, categoryId, TaxCategory.class, false);
        customerTaxRegionId = tm.checkType(this, customerTaxRegionId, TaxRegion.class, true);
        super.validateData(tm);
    }

    /**
     * Retrieves an iterator of tax types applicable to a specific inventory item, tax region of the customer, and date.
     *
     * @param item   the inventory item for which the tax types are to be determined
     * @param organization the organization for which the tax types are being retrieved
     * @param customerTaxRegion the tax customerTaxRegion that specifies the geographical context
     * @param date   the date at which the tax types are evaluated
     * @return an iterator over the tax types applicable to the given parameters
     */
    public static ObjectIterator<TaxType> listTypes(InventoryItem item, SystemEntity organization, TaxRegion customerTaxRegion, Date date) {
        return listTypes(item.getPartNumber(), organization, customerTaxRegion, date);
    }

    /**
     * Retrieves an iterator of tax types applicable to a given inventory item type and tax region of the customer at a specific date.
     *
     * @param itemType the inventory item type for which the tax types need to be listed
     * @param organization the organization for which the tax types are being retrieved
     * @param customerTaxRegion the tax customerTaxRegion for which the tax types are being retrieved
     * @param date the specific date to determine applicable tax types
     * @return an iterator of applicable tax types for the provided inventory item type, customerTaxRegion, and date
     */
    public static ObjectIterator<TaxType> listTypes(InventoryItemType itemType, SystemEntity organization, TaxRegion customerTaxRegion, Date date) {
        TaxCategory category = itemType.getSaleTaxCategory(customerTaxRegion, organization, date);
        if(category == null) category = TaxCategory.getDefault();
        TaxDefinition td = get(TaxDefinition.class, "Category=" + category.getId() + " AND CustomerTaxRegion="
                + customerTaxRegion.getId());
        if(td == null) td = get(TaxDefinition.class, "Category=" + category.getId() + " AND CustomerTaxRegion=0");
        if(td == null) throw new SORuntimeException("No tax definition found for category '" + category.getName()
                + "' and customer tax region '" + customerTaxRegion.getName() + "'");
        return td.listLinks(TaxType.class, "Region=" + TaxCode.getFor(organization).getTaxRegionId() + " AND '"
                        + Database.format(date) + "' BETWEEN Type.ApplicableFrom AND Type.ApplicableTo",
                "DisplayOrder");
    }
}
