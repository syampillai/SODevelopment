package com.storedobject.core;

public class InventoryLoanOut extends InventoryReturn {

    public InventoryLoanOut() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] links() {
        return new String[]{
                "Items|com.storedobject.core.InventoryLoanOutItem|||0",
        };
    }

    @Override
    public String getActionDescription(ActionType actionType) {
        return actionType == ActionType.NOUN ? "Loan out" : super.getActionDescription(actionType);
    }

    @Override
    public int getToLocationType() {
        return 8;
    }

    @Override
    public String getToLocationName() {
        return "Loanee";
    }

    public static String actionPrefixForUI() {
        return "LO";
    }
}