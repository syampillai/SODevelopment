package com.storedobject.core;

import com.storedobject.core.annotation.Column;

/**
 * <p>Represents a location where an item can be located or sourced. A location could be an {@link InventoryBin} (if
 * it is stocked in a store), a "supplier", a "repair organization" (if it is sent for repair there),
 * a {@link InventoryFitmentPosition} (if it is fitted on an assembly) etc.</p>
 * <p>Only regular inventory bin ({@link InventoryBin}) can be extended further. All other type of bins have fixed
 * platform-level implementation.</p>
 *
 * @author Syam
 */
public abstract class InventoryLocation extends StoredObject {

    private static final String[] typeValues = new String[] {
            "Store", // 0
            "Supplier", // 1
            "Consumer/Customer", // 2
            "Repair/Maintenance Organization", // 3 (Outside the organization, stock owner is still us)
            "Production Unit", // 4
            "Maintenance Unit", // 5 (Inside the organization)
            "Scrap", // 6
            "Inventory Shortage", // 7
            "Rented/Loaned out to", // 8 (Stock owner is the original owner)
            "Rented/Leased from", // 9 (Entity owns the stock)
            "Service Unit", // 10 (Providing services to customers)
            "Repair Unit", // 11 (Inside the organization)
            "Initial Inventory", // 12 (Used for data pick-up only)
            "Service/Subscription", // 13 (For services/subscriptions)
            "Assembly", // 14 (Assembly - {@link InventoryFitmentPosition})
            "To recycle", // 15 (Dumping location from where items can be resurrected)
            "Consumption", // 16 (Consume the item internally or for providing external services)
            "External Owner", // 17 External owner location
            "Custody", // 18 In the custody of someone (mostly tools)
            "Package", // 19 In a package
    };
    private static final String[] returnPolicyValues = new String[] {
            "Not allowed/applicable", "Allowed", "Tracked"
    };
    private String name;

    /**
     * Constructor.
     */
    public InventoryLocation() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)");
    }

    public static String[] displayColumns() {
        return new String[] { "Name" };
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100, caption = "Description")
    public String getName() {
        return name;
    }

    /**
     * Check whether a particular item can be stored at this location or not.
     *
     * @param item Item to check.
     * @return True or false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean canBin(InventoryItem item) {
        return item.canStore(this) && canBin(item.getPartNumber());
    }

    /**
     * Check whether a particular type of item (part number) can be stored at this location or not.
     *
     * @param partNumber Item type to check.
     * @return True or false.
     */
    public final boolean canBin(InventoryItemType partNumber) {
        if(!partNumber.isSerialized()) {
            switch(getType()) {
                case 3:
                case 8:
                case 9:
                case 17:
                case 18:
                    return false; // Third-party location or personal custody - only serialized are allowed
            }
        }
        return true;
    }

    protected boolean canStore(InventoryItemType partNumber) {
        return true;
    }

    public void setCategory(Id category) {
    }

    public Id getCategoryId() {
        return Id.ZERO;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract Id getEntityId();

    /**
     * Get the type of this location. Location type values are as follows:
     * <p>0: Inventory store bins {@link InventoryBin}</p>
     * <p>1: Supplier locations (purchase of items are coming from supplier locations)</p>
     * <p>2: Consumer/customer locations (items sold to a customer go here)</p>
     * <p>3: Repair/Maintenance organization (items sent for repair or maintenance work will be located here)</p>
     * <p>4: Production unit</p>
     * <p>5: Maintenance unit (within the organization)</p>
     * <p>6: Scrap</p>
     * <p>7: Inventory shortage is booked here</p>
     * <p>8: Rented/Loaned out to (an outside organization)</p>
     * <p>9: Rented/Leased from (an outside organization)</p>
     * <p>10: Service Unit or Work Centers (providing services to external customers)</p>
     * <p>11: Repair unit (within the organization)</p>
     * <p>12: Initial inventory (used as a source for data pick-up)</p>
     * <p>13: Service/Subscription</p>
     * <p>14: Assembly (fitment positions on assembled items) {@link InventoryFitmentPosition}</p>
     * <p>15: Thrash used for recycling items that were entered with incorrect details</p>
     * <p>16: Internal consumption (Consume the item internally - typically consumables)</p>
     * <p>17: External owner (An external entity owns the item)</p>
     * <p>18: Custody (In the custody of someone. Mostly used for tools)</p>
     * <p>19: Packaged (Packaged in a package for sending it out)</p>
     *
     * @return Location type.
     */
    public abstract int getType();

    public static String[] getTypeValues() {
        return typeValues;
    }

    public static String getTypeValue(int value) {
        String[] s = getTypeValues();
        if(value < 0 || value >= s.length) {
            return "Unknown type " + value;
        }
        return s[value];
    }

    public String getTypeValue() {
        return getTypeValue(getType());
    }

    public int getReturnPolicy() {
        return 0;
    }

    public void setReturnPolicy(int returnPolicy) {
    }

    public String getReturnPolicyValue() {
        return getReturnPolicyValue(getReturnPolicy());
    }

    public static String[] getReturnPolicyValues() {
        return returnPolicyValues;
    }

    public static String getReturnPolicyValue(int returnPolicy) {
        return returnPolicyValues[returnPolicy % returnPolicyValues.length];
    }

    public final boolean isScrapAllowed() {
        return switch(getType()) { // Normal
            // Maintenance unit
            // Service unit
            // Repair unit
            case 0, 5, 10, 11 -> true;
            default -> false;
        };
    }

    public String getReceiptText() {
        return switch(getType()) {
            case 1 -> // Supplier
                    "Purchased return -" + en();
            case 2 -> // Consumer
                    "Sold to" + en();
            case 9 -> // Rented from
                    "Loaned from" + en();
            case 14 -> // Assembly
                    "To " + toDisplay();
            case 17 -> "From" + en();
            default -> "Issued to " + name;
        };
    }

    public String getIssueText() {
        return switch(getType()) {
            case 1 -> // Supplier
                    "Purchased from" + en();
            case 2 -> // Consumer
                    "Sales return -" + en();
            case 8 -> // Rented out
                    "Loaned out to" + en();
            case 9 -> // Rented from
                    "Loaned from" + en();
            case 12 -> // Initial inventory
                    "Initial data";
            case 14 -> // Assembly
                    "From " + toDisplay();
            case 15 -> // Thrashed
                    "Thrashed";
            case 16 -> "Consumed by" + en();
            case 17 -> "From" + en();
            default -> "Received from " + name;
        };
    }

    private String en() {
        return " " + get(Entity.class, getEntityId()).toDisplay();
    }
}