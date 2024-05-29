package com.storedobjects.support;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class SupportPerson extends StoredObject {

    private Id personId;

    public SupportPerson() {
    }

    public static void columns(Columns columns) {
        columns.add("Person", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Person", true);
    }

    public String getUniqueCondition() {
        return "Person=" + getPersonId();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Products|com.storedobjects.support.ProductSkill|||0",
                "Assigned to|com.storedobjects.support.Organization|||0",
        };
    }

    public void setPerson(Id personId) {
        if (!loading() && !Id.equals(this.getPersonId(), personId)) {
            throw new Set_Not_Allowed("Person");
        }
        this.personId = personId;
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(SystemUser person) {
        setPerson(person == null ? null : person.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getPersonId() {
        return personId;
    }

    public SystemUser getPerson() {
        return getRelated(SystemUser.class, personId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        personId = tm.checkType(this, personId, SystemUser.class, false);
        super.validateData(tm);
    }

    @Override
    public void saved() {
        Issue.approvers.clear();
    }
}
