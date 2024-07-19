package com.storedobject.accounts;

import com.storedobject.core.*;

public abstract class InstantaneousAccount extends EntityAccount {

    public InstantaneousAccount() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Entity",
        };
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(created()) {
            BusinessEntity be = get(BusinessEntity.class, "Entity=" + tm.getEntity().getEntityId());
            if (be == null) {
                be = new BusinessEntity();
                be.setEntity(tm.getEntity().getEntityId());
                tm.transact(be::save);
            }
            setEntity(be.getId());
        }
        super.validateData(tm);
    }

    @Override
    protected void validateAccountStatus() throws Exception {
        super.validateAccountStatus();
        if(Financial.getBalanceType(this) == 0) {
            return;
        }
        throw new Invalid_State("Invalid balance control");
    }
}
