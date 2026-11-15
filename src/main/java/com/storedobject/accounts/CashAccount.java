package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;

public final class CashAccount extends InstantaneousAccount {

    private Id personId;

    public CashAccount() {
        setName("Cash Account");
    }

    public static void columns(Columns columns) {
        columns.add("Person", "id");
    }

    public static String[] browseColumns() {
        return new String[] { "AccountNumber", "Name", "Person AS Custodian" };
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Entity", "Person"
        };
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }
    public void setPerson(Id personId) {
        this.personId = personId;
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(Person person) {
        setPerson(person == null ? null : person.getId());
    }

    @Column(caption = "Custodian")
    public Id getPersonId() {
        return personId;
    }

    public Person getPerson() {
        return getRelated(Person.class, personId);
    }

    @Override
    protected void checkPosting(DBTransaction tran, Money amount, Money localCurrencyAmount) throws Exception {
        Id pid = tran.getManager().getUser().getPersonId();
        if(amount.isZero() || pid.equals(personId)) {
            return;
        }
        if(amount.isCredit() && tran.containsTran(CashAccount.class, amount.negate(), a -> a.personId.equals(pid))) {
            return;
        }
        throw new Invalid_State("Changes to cash can only be made by the custodian - " + getPerson());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        personId = tm.getUser().getPersonId();
        if (!getName().toLowerCase().contains("cash")) {
            setName("Cash - " + getName());
        }
        super.validateData(tm);
        AccountChart ac = getChart();
        if(ac.getCategory() != 0 || ac.getBalanceType() != 0 || !ac.getStrictBalanceControl()) {
            throw new Invalid_State("Invalid chart - " + ac.toDisplay() + ", Category - " + ac.getCategoryValue());
        }
    }

    @Override
    protected void validateAccountStatus() throws Exception {
        super.validateAccountStatus();
        if(Financial.hasStrictBalanceControl(this)) {
            return;
        }
        throw new Invalid_State("Invalid balance control");
    }
}
