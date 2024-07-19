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
                "Entity",
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
    public void validateData(TransactionManager tm) throws Exception {
        personId = tm.checkType(this, personId, Person.class);
        if(!exists(SystemUser.class, "Person=" + personId)) {
            throw new Invalid_State(getPerson() + " does not have access to the system");
        }
        if (!getName().toLowerCase().contains("cash")) {
            setName("Cash - " + getName());
        }
        super.validateData(tm);
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
