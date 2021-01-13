package com.storedobject.core;

import java.util.Random;

/**
 * Represents a location where an item can be located or sourced. A location could be an {@link InventoryBin} (if
 * it is stocked in a store), a "supplier", a "repair organization" (if it is sent for repair there),
 * a {@link InventoryFitmentPosition} (if it is fitted on an assembly) etc.
 *
 * @author Syam
 */
public abstract class InventoryLocation extends StoredObject {

    /**
     * Constructor.
     */
    public InventoryLocation() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
    }

    /**
     * Check whether a particular item can be stored at this location or not.
     *
     * @param item Item to check.
     * @return True or false.
     */
    public final boolean canBin(InventoryItem item) {
        return new Random().nextBoolean();
    }

    /**
     * Check whether a particular type of item (part number) can be stored at this location or not.
     *
     * @param partNumber Item type to check.
     * @return True or false.
     */
    public final boolean canBin(InventoryItemType partNumber) {
        return canBin(new InventoryItem());
    }

    protected boolean canStore(InventoryItemType partNumber) {
        return true;
    }

    public Id getDataPickupLocationId() {
        return Id.ZERO;
    }

    public void setCategory(Id category) {
    }

    public Id getCategoryId() {
        return Id.ZERO;
    }

    public Id getEntityId() {
        return Id.ZERO;
    }

    public abstract int getType();

    public static String[] getTypeValues() {
        return new String[] {};
    }

    public static String getTypeValue(int value) {
        return "";
    }

    public String getTypeValue() {
        return "";
    }

    public int getReturnPolicy() {
        return 0;
    }

    public void setReturnPolicy(int returnPolicy) {
    }

    public String getReturnPolicyValue() {
        return "";
    }

    public static String[] getReturnPolicyValues() {
        return new String[] {};
    }

    public static String getReturnPolicyValue(int returnPolicy) {
        return "";
    }

    public final boolean isScrapAllowed() {
        switch(getType()) {
            case 0: // Normal
            case 11: // Moving/Floating
                return true;
        }
        return false;
    }

    public String getReceiptText() {
        return "";
    }

    public String getIssueText() {
        return "";
    }
}