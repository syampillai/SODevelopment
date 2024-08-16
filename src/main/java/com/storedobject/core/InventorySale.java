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
        var loc = getToLocation();
        if (loc == null || loc.getType() != 2) {
            throw new Invalid_Value("Customer");
        }
    }

    public Entity getCustomerEntity() {
        return get(Entity.class, getToLocation().getEntityId());
    }
}
