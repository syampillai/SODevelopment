package com.storedobject.core;

public class InventorySale extends InventoryTransfer implements TradeType {

    public InventorySale() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] links() {
        return new String[] {
                "Items|com.storedobject.core.InventorySaleItem|||0",
        };
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        if(getType() >= 1000) {
            throw new Invalid_State("Invalid type");
        }
    }

    @Override
    public String getActionDescription(ActionType actionType) {
        return switch (actionType) {
            case NOUN, VERB_PRESENT -> "Sell";
            case VERB_PAST, VERB_PAST_PARTICIPLE -> "Sold";
        };
    }

    @Override
    public final int getToLocationType() {
        return 2;
    }

    @Override
    public String getToLocationName() {
        return "Customer";
    }

    public Entity getCustomerEntity() {
        return getEntityTo();
    }
}
