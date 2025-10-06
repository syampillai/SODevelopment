package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectInput;
import com.storedobject.ui.ObjectProvider;

/**
 * Fields that can input a given type of {@link InventoryItem} value.
 *
 * @param <T> Type of objects that can be inputted.
 * @author Syam
 */
public interface ItemInput<T extends InventoryItem> extends ObjectInput<T> {

    /**
     * Set the store. If set, only items from the given store will be acceptable.
     *
     * @param storeField Any provider that can supply an {@link InventoryStore} instance.
     */
    void setStore(ObjectProvider<? extends InventoryStore> storeField);

    /**
     * Set the store. If set, only items from the given store will be acceptable.
     *
     * @param store An instance of {@link InventoryStore}.
     */
    void setStore(InventoryStore store);

    /**
     * Set the location (it could be bin location or any other location).
     * If set, only items from the given location will be acceptable.
     *
     * @param locationField Any provider that can supply an {@link InventoryLocation} instance.
     */
    void setLocation(ObjectProvider<? extends InventoryLocation> locationField);

    /**
     * Set the location (it could be bin location or any other location).
     * If set, only items from the given location will be acceptable.
     *
     * @param location An instance of an {@link InventoryLocation}.
     */
    void setLocation(InventoryLocation location);

    /**
     * Fix the part number (an instance of {@link InventoryItemType}). If a non-null value is set,
     *  the "part number" portion will be locked with that value. A <code>null</code> value may be set later
     * to unlock it.
     *
     * @param partNumber Part number to set.
     */
    void fixPartNumber(InventoryItemType partNumber);

    /**
     * Set extra filters that need to be added to other filters when item's accessibility is checked.
     *
     * @param extraFilterProvider Extra filter to be set.
     */
    void setExtraFilterProvider(FilterProvider extraFilterProvider);

    /**
     * Get the P/N provider of this.
     * @return P/N provider.
     */
    ObjectProvider<? extends InventoryItemType> getPartNumberProvider();

    /**
     * A helper method to create an instance of the {@link ItemInput} field based on the properties of the
     * item's class.
     *
     * @param objectClass Class of the item.
     * @param <I> Type of the item class.
     * @return Instance of an {@link ItemInput}.
     */
    static <I extends InventoryItem> ItemInput<I> create(Class<I> objectClass) {
        return create(null, objectClass, false);
    }

    /**
     * A helper method to create an instance of the {@link ItemInput} field based on the properties of the
     * item's class.
     *
     * @param label Label for the field.
     * @param objectClass Class of the item.
     * @param <I> Type of the item class.
     * @return Instance of an {@link ItemInput}.
     */
    static <I extends InventoryItem> ItemInput<I> create(String label, Class<I> objectClass) {
        return create(label, objectClass, false);
    }

    /**
     * A helper method to create an instance of the {@link ItemInput} field based on the properties of the
     * item's class.
     *
     * @param objectClass Class of the item.
     * @param allowAny Whether to allow subclasses or not.
     * @param <I> Type of the item class.
     * @return Instance of an {@link ItemInput}.
     */
    static <I extends InventoryItem> ItemInput<I> create(Class<I> objectClass, boolean allowAny) {
        return create(null, objectClass, allowAny);
    }

    /**
     * A helper method to create an instance of the {@link ItemInput} field based on the properties of the
     * item's class.
     *
     * @param label Label for the field.
     * @param objectClass Class of the item.
     * @param allowAny Whether to allow subclasses or not.
     * @param <I> Type of the item class.
     * @return Instance of an {@link ItemInput}.
     */
    static <I extends InventoryItem> ItemInput<I> create(String label, Class<I> objectClass, boolean allowAny) {
        if(ObjectComboField.lessRows(objectClass, allowAny)) {
            return new ItemComboField<>(label, objectClass, allowAny);
        }
        if(ObjectGetField.canCreate(objectClass)) {
            return new ItemGetField<>(label, objectClass, allowAny);
        }
        return new ItemField<>(label, objectClass, allowAny);
    }
}
