package com.storedobjects.support;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class SupportUser extends StoredObject {

    private Id supportUserId;
    private Id organizationId;

    public SupportUser() {
    }

    public static void columns(Columns columns) {
        columns.add("SupportUser", "id");
        columns.add("Organization", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SupportUser", true);
    }

    public String getUniqueCondition() {
        return "SupportUser=" + getSupportUserId();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setSupportUser(Id supportUserId) {
        if (!loading() && !Id.equals(this.getSupportUserId(), supportUserId)) {
            throw new Set_Not_Allowed("Support User");
        }
        this.supportUserId = supportUserId;
    }

    public void setSupportUser(BigDecimal idValue) {
        setSupportUser(new Id(idValue));
    }

    public void setSupportUser(SystemUser supportUser) {
        setSupportUser(supportUser == null ? null : supportUser.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getSupportUserId() {
        return supportUserId;
    }

    public SystemUser getSupportUser() {
        return getRelated(SystemUser.class, supportUserId);
    }

    public void setOrganization(Id organizationId) {
        this.organizationId = organizationId;
    }

    public void setOrganization(BigDecimal idValue) {
        setOrganization(new Id(idValue));
    }

    public void setOrganization(Organization organization) {
        setOrganization(organization == null ? null : organization.getId());
    }

    @Column(order = 200)
    public Id getOrganizationId() {
        return organizationId;
    }

    public Organization getOrganization() {
        return getRelated(Organization.class, organizationId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        supportUserId = tm.checkType(this, supportUserId, SystemUser.class, false);
        organizationId = tm.checkType(this, organizationId, Organization.class, false);
        checkForDuplicate("SupportUser");
        super.validateData(tm);
    }
}
