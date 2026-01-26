package com.storedobject.core;

import com.storedobject.accounts.FixedTax;
import com.storedobject.accounts.NoTax;
import com.storedobject.core.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a taxation method for financial calculations.
 * Provides functionality to calculate taxes based on specific
 * logic, manage active state, and implement data validation.
 * Caching is implemented to optimize retrieval by maintaining a static
 * map of cached {@code TaxMethod} objects.
 *
 * @author Syam
 */
public class TaxMethod extends Name {

    private static final BigDecimal BD_100 = BigDecimal.valueOf(100);
    private static final Map<Id, TaxMethod> cache = new HashMap<>();
    private boolean active = true;

    /**
     * Constructs a TaxMethod object with a default tax calculation method name.
     * The default name is set to "Percentage".
     */
    public TaxMethod() {
        name = "Percentage";
    }

    /**
     * Configures columns for a data model by adding a column named "Active" with a type of "boolean".
     *
     * @param columns The columns object to which the "Active" column is added. Represents a set of definable columns
     *                in a database or data structure.
     */
    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    /**
     * Modifies the given indices object by adding a specific index with a specified condition.
     *
     * @param indices The object representing the collection of indices to be modified.
     */
    public static void indices(Indices indices) {
        indices.add("T_Family", true);
    }

    /**
     * Provides hints for this object, indicating specific characteristics or optimizations
     * associated with the object or its usage.
     *
     * @return A bitwise combination of constants from the {@code ObjectHint} class,
     *         such as {@code ObjectHint.SMALL} and {@code ObjectHint.SMALL_LIST},
     *         representing the applicable hints.
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the active status of the object.
     *
     * @param active a boolean value indicating whether the object should be active (true) or inactive (false)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves the active status of the tax method.
     *
     * @return true if the tax method is active, false otherwise.
     */
    @Column(order = 300)
    public boolean getActive() {
        return active;
    }

    /**
     * Removes the cached entry associated with this instance's Id after the object is saved.
     * This method is invoked when the object state is persisted, ensuring the cache remains consistent
     * with the underlying state.
     *
     * @throws Exception if an error occurs during the cache removal process.
     */
    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    /**
     * Validates the data associated with a TaxMethod instance.
     * This method ensures that the correct TaxMethod types exist and checks for possible duplicate entries,
     * throwing exceptions in case of invalid or inconsistent states.
     *
     * @param tm The transaction manager used to handle database transactions during validation.
     * @throws Exception Thrown if validation encounters an invalid state, such as duplicate entries or
     *                   missing required TaxMethod types.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        Class<? extends TaxMethod> c = getClass();
        if(c == TaxMethod.class) {
            name = "Percentage";
        }
        if(c != TaxMethod.class && c != NoTax.class && c != FixedTax.class) {
            if(!exists(TaxMethod.class, "T_Family=" + ClassAttribute.get(TaxMethod.class).getFamily())) {
                tm.transact(new TaxMethod()::save);
            }
        }
        if(inserted()) {
            TaxMethod m = get(TaxMethod.class, "T_Family=" + ClassAttribute.get(c).getFamily());
            if(m != null) {
                throw new Invalid_State("Duplicate entry");
            }
        }
        if(c == TaxMethod.class) {
            if (!exists(NoTax.class, "T_Family=" + ClassAttribute.get(NoTax.class).getFamily())) {
                tm.transact(new NoTax()::save);
            }
            if (!exists(FixedTax.class, "T_Family=" + ClassAttribute.get(FixedTax.class).getFamily())) {
                tm.transact(new FixedTax()::save);
            }
        }
    }

    /**
     * Retrieves a TaxMethod instance for the provided Id.
     * If the instance is not found in the cache, it fetches the instance, caches it, and then returns it.
     *
     * @param id the unique identifier of the TaxMethod to retrieve
     * @return the TaxMethod instance associated with the provided Id, or null if not found
     */
    public static TaxMethod getFor(Id id) {
        TaxMethod tm = cache.get(id);
        if(tm == null) {
            tm = get(TaxMethod.class, id, true);
            if(tm != null) {
                cache.put(id, tm);
            }
        }
        return tm;
    }

    /**
     * Calculates the tax amount for a given inventory item based on its type, quantity, unit cost, tax rate, and local currency.
     *
     * @param itemType      the type of the inventory item for which the tax is being calculated
     * @param quantity      the quantity of the inventory item
     * @param unitCost      the cost per unit of the inventory item
     * @param taxRate       the percentage tax rate to be applied
     * @param localCurrency the currency in which the tax amount should be calculated
     * @return the calculated tax amount in the specified local currency
     */
    public Money getTax(InventoryItemType itemType, Quantity quantity,
                        Money unitCost, Percentage taxRate, Currency localCurrency) {
        return round(unitCost.multiply(quantity).multiply(taxRate).divide(BD_100).convert(localCurrency));
    }

    /**
     * Rounds the given monetary amount to the nearest valid value
     * based on the specific rounding rules defined.
     *
     * @param taxAmount the monetary amount to be rounded
     * @return the rounded monetary amount
     */
    public Money round(Money taxAmount) {
        return taxAmount;
    }
}
