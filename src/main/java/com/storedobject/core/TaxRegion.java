package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.sql.Date;
import java.util.*;

/**
 * Represents a tax region, which defines a set of tax rules and configurations for a specific region.
 *
 * @author Syam
 */
public final class TaxRegion extends Name {

    private static final Map<Id, TaxRegion> cache = new HashMap<>();
    private static TaxRegion DEFAULT;
    private List<TaxType> taxTypes;
    private boolean active = true;

    /**
     * Default constructor for the TaxRegion class.
     * Initializes a new instance of the TaxRegion object.
     */
    public TaxRegion() {}

    /**
     * Adds a column with the specified name and data type to the given Columns instance.
     *
     * @param columns the Columns instance to which the column will be added
     */
    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    /**
     * Retrieves a TaxRegion instance based on the provided name.
     *
     * @param name the name of the TaxRegion to retrieve
     * @return the TaxRegion instance matching the specified name, or null if no match is found
     */
    public static TaxRegion get(String name) {
        return StoredObjectUtility.get(TaxRegion.class, "Name", name, false);
    }

    /**
     * Retrieves a list of TaxRegion objects filtered by the specified name.
     *
     * @param name the name used to filter the TaxRegion objects
     * @return an ObjectIterator containing the filtered TaxRegion objects
     */
    public static ObjectIterator<TaxRegion> list(String name) {
        return StoredObjectUtility.list(TaxRegion.class, "Name", name, false);
    }

    /**
     * Provides hint values for object instances in the tax region system. The hints are a bitwise combination
     * of predefined constants such as {@code ObjectHint.SMALL} and {@code ObjectHint.SMALL_LIST}.
     *
     * @return A bitwise combination of integer constants indicating object size or list size hints.
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * This method is called when an object is saved.
     * It performs necessary post-save operations, including removing
     * the object from the cache to ensure consistency with the storage.
     *
     * @throws Exception if an error occurs during the cache removal process
     */
    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    /**
     * Sets the active status of the TaxRegion.
     *
     * @param active a boolean value indicating whether the TaxRegion is active (true) or inactive (false)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves the active status of the TaxRegion.
     *
     * @return true if the TaxRegion is active, otherwise false.
     */
    @Column(order = 200)
    public boolean getActive() {
        return active;
    }

    /**
     * Retrieves a TaxRegion object for the given identifier. If the object is not found in the cache,
     * it is retrieved from persistent storage, cached, and then returned.
     *
     * @param id the unique identifier of the TaxRegion to retrieve
     * @return the TaxRegion object corresponding to the given ID, or null if it does not exist
     */
    public static TaxRegion getFor(Id id) {
        TaxRegion tc = cache.get(id);
        if (tc == null) {
            tc = get(TaxRegion.class, id);
            if (tc != null) {
                cache.put(id, tc);
            }
        }
        return tc;
    }

    /**
     * Retrieves the default TaxRegion instance. If the default instance has not been initialized,
     * it fetches the first active TaxRegion ordered by ID and initializes it.
     *
     * @return the default TaxRegion instance
     */
    public static TaxRegion getDefault() {
        if(DEFAULT == null) {
            DEFAULT = list(TaxRegion.class, "Active", "Id").findFirst();
        }
        return DEFAULT;
    }

    /**
     * Retrieves the list of tax types associated with the current region.
     * If the list is not initialized, it will fetch and populate the tax types
     * based on the region's identifier and filter by active status, ordering them
     * by display order.
     *
     * @return a list of TaxType objects representing the tax types for the region
     */
    public List<TaxType> getTaxTypes() {
        if(taxTypes == null) {
            taxTypes = list(TaxType.class, "Region=" + getId() + " AND Active", "DisplayOrder").toList();
        }
        return taxTypes;
    }

    /**
     * Computes and save the applicable taxes for a given inventory item based on the provided parameters.
     *
     * @param parent         The parent object representing the context for the tax computation.
     * @param itemType       The type of inventory item for which taxes are to be computed.
     * @param quantity       The quantity of the inventory item being taxed.
     * @param unitCost       The cost per unit of the inventory item.
     * @param transaction Transaction
     * @return A list of taxes applicable to the specified inventory item and context.
     */
    public List<Tax> computeTax(StoredObject parent, InventoryItemType itemType, Quantity quantity,
                                Money unitCost, Transaction transaction) throws Exception {
        Date date = transaction.getManager().getWorkingDate();
        return computeTax(date, parent, itemType, quantity, unitCost, transaction);
    }

    /**
     * Computes and save the applicable taxes for a given inventory item based on the provided parameters.
     *
     * @param date           The date for which the tax computation is being performed.
     * @param parent         The parent object representing the context for the tax computation.
     * @param itemType       The type of inventory item for which taxes are to be computed.
     * @param quantity       The quantity of the inventory item being taxed.
     * @param unitCost       The cost per unit of the inventory item.
     * @param transaction Transaction
     * @return A list of taxes applicable to the specified inventory item and context.
     */
    public List<Tax> computeTax(Date date, StoredObject parent, InventoryItemType itemType, Quantity quantity,
                                Money unitCost, Transaction transaction) throws Exception {
        SystemEntity organization = transaction.getManager().getEntity();
        Currency localCurrency = Currency.getInstance(organization.getCurrency());
        Percentage p;
        List<TaxType> taxTypes = TaxDefinition.listTypes(itemType, organization, this, date).toList();
        List<Tax> taxes = parent.listLinks(Tax.class).toList();
        Set<Id> toDelete = new HashSet<>();
        for(Tax tax : taxes) {
            if(!tax.getRegionId().equals(getId()) // Region changed
                || taxTypes.stream().noneMatch(t -> tax.getTypeId().equals(t.getId())) // No more applicable
            ) { // To be deleted
                tax.delete(transaction);
                toDelete.add(tax.getId());
            } else { // Modify
                TaxType tt = tax.getType();
                p = TaxRate.getRate(date, tt);
                Money t = tt.getTaxMethod().getTax(itemType, quantity, unitCost, p, localCurrency);
                if(!p.equals(tax.getRate()) || !t.equals(tax.getTax())) { // Save only if changed
                    tax.setTax(t);
                    tax.setRate(p);
                    tax.save(transaction);
                }
            }
        }
        taxes.removeIf(t -> toDelete.contains(t.getId()));
        Tax tax;
        for(TaxType taxType : taxTypes) {
            tax = taxes.stream().filter(t -> t.getTypeId().equals(taxType.getId())).findFirst().orElse(null);
            if(tax != null) {
                continue;
            }
            // New tax type
            tax = new Tax();
            tax.setType(taxType);
            p = TaxRate.getRate(date, taxType);
            tax.setRate(p);
            tax.setTax(taxType.getTaxMethod().getTax(itemType, quantity, unitCost, p, localCurrency));
            tax.save(transaction);
            parent.addLink(transaction, tax);
            taxes.add(tax);
        }
        return taxes;
    }
}