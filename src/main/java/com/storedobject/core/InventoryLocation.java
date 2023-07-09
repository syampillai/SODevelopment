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
            "Thrashed", // 15 Thrash
            "Consumption", // 16 (Consume the item internally or for providing external services)
            "External Owner", // 17 External owner (Can receive stock from these locations but the ownership is still theirs)
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

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!(this instanceof InventoryBin ||
                this instanceof InventoryFitmentPosition ||
                this instanceof InventoryCustodyLocation ||
                this instanceof InventoryVirtualLocation)) {
            throw new Invalid_State("Not allowed");
        }
        if(deleted() && exists(InventoryItem.class, "Location=" + getId(), true)) {
            throw new Invalid_State("Can not delete, bin/location is in use.");
        }
        if(StringUtility.isWhite(name) || name.contains("|")) {
            throw new Invalid_Value("Name");
        }
        name = name.trim();
        setReturnPolicy(getReturnPolicyInt(getReturnPolicy()) % returnPolicyValues.length);
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        setReturnPolicy(getReturnPolicyInt(getReturnPolicy()) % returnPolicyValues.length);
    }

    private int getReturnPolicyInt(int defaultValue) {
        return switch(getType()) {
            // Normal
            // Scrap
            // Inventory loss/shortage
            // Initial inventory
            // Service/Subscription
            // Assembly
            // Maintenance unit
            // Repair unit
            // Service unit
            // Thrash
            // Consumed
            case 0, 6, 7, 12, 13, 14, 5, 11, 10, 15, 16, 19 -> 0;
            // Supplier
            // Consumer
            case 1, 2 -> 1; // Allowed
            // Repair org.
            // Rented out to
            // Rented from
            // External owner
            case 3, 8, 9, 17 -> 2;
            // Tracked
            default -> defaultValue;
        };
    }

    /**
     * Check whether a particular item can be stored at this location or not.
     *
     * @param item Item to check.
     * @return True or false.
     */
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
                case 3, 8, 17, 18 -> {
                    return false; // Third-party location or personal custody - only serialized are allowed
                }
            }
        }
        return checkStorage(partNumber) && !locationMismatch(partNumber) && partNumber.canBin(this) &&
                canStore(partNumber);
    }

    boolean checkStorage(InventoryItemType partNumber) {
        return true;
    }

    protected boolean canStore(InventoryItemType partNumber) {
        return true;
    }

    private boolean locationMismatch(InventoryItemType itemType) {
        if(itemType == null) {
            return true;
        }
        if(itemType instanceof ServiceItemType || itemType instanceof SubscriptionItemType) {
            return getType() != 13;
        }
        Id ci = itemType.getCategoryId();
        Id catId = getCategoryId();
        if(ci != catId && !(Id.isNull(ci) && Id.isNull(catId))) {
            return !((Id.isNull(ci) || !Id.isNull(catId)) && (!Id.isNull(ci) || Id.isNull(catId)) && ci.equals(catId));
        }
        return false;
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
     * <p>15: Thrash - used for keeping items that were entered with incorrect details</p>
     * <p>16: Internal consumption (Consume the item internally - typically consumables)</p>
     * <p>17: External owner (Can receive stock from these locations but the ownership is still theirs)</p>
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
        return getReturnPolicyInt(0);
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

    public final boolean isInspectionRequired() {
        return switch(getType()) {
            // 0 - Store
            // 3 - Repair/Maintenance Organization
            // 4 - Production Unit
            // 5 - Maintenance Unit (Within the organization)
            // 9 - Rented/Leased from
            // 10 - Service Unit
            // 11 - Repair Unit
            case 0, 3, 4, 5, 9, 10, 11 -> true;
            default -> false;
        };
    }

    public final boolean isScrapAllowed() {
        return switch(getType()) { // Normal
            // 0 - Maintenance unit
            // 5 - Maintenance unit
            // 10 - Service unit
            // 11 - Repair unit
            case 0, 5, 10, 11 -> true;
            default -> false;
        };
    }

    boolean infiniteSource() {
        return switch(getType()) {
            // 1 - Supplier
            // 9 - Rented from
            // 12 - Initial inventory
            case 1, 9, 12 -> true;
            default -> false;
        };
    }

    boolean infiniteSink() {
        return switch(getType()) {
            // 2 - Consumer
            // 6 - Scrap
            // 7 - Shortage
            // 15 - Thrash
            case 2, 6, 7, 15 -> true;
            default -> false;
        };
    }

    public String getReceiptText() {
        return switch(getType()) {
            case 1 -> // Supplier
                    "Purchased return -" + en();
            case 2 -> // Consumer
                    "Sold to" + en();
            case 6 -> // Scrap
                    "Scrapped -" + en();
            case 9 -> // Rented from
                    "Loaned from" + en();
            case 14 -> // Assembly
                    "To " + toDisplay();
            case 17 ->  // External location
                    "From" + en();
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
            case 16 -> // Consumption
                    "Consumed by" + en();
            case 17 -> // External owner
                    "Returned to" + en();
            default -> "Received from " + name;
        };
    }

    private String en() {
        return " " + get(Entity.class, getEntityId()).toDisplay();
    }

    public boolean canResurrect() {
        return switch(getType()) {
            // 1 - Supplier
            // 2 - Consumer
            // 7 - Inventory shortage
            // 9 - Rented/Leased from
            // 15 - Thrashed
            // 17 - External owner
            case 1, 2, 7, 9, 15, 17 -> true;
            default -> false;
        };
    }

    public boolean isActive() {
        return true;
    }
}