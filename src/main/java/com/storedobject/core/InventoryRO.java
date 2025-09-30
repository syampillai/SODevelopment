package com.storedobject.core;

public final class InventoryRO extends InventoryReturn {

    public InventoryRO() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] links() {
        return new String[]{
                "Items|com.storedobject.core.InventoryROItem|||0",
        };
    }

    public Entity getRepairEntity() {
        return getEntityTo();
    }

    @Override
    public String getActionDescription(ActionType actionType) {
        return actionType == ActionType.NOUN ? "Send for repair" : super.getActionDescription(actionType);
    }

    @Override
    public int getToLocationType() {
        return 3;
    }

    @Override
    public String getToLocationName() {
        return "Repair/Maintenance Organization";
    }

    public static String actionPrefixForUI() {
        return "RO";
    }
}