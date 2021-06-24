package com.storedobject.core;

import java.math.BigDecimal;

import com.storedobject.core.StoredObjectUtility.Link;
import com.storedobject.core.annotation.Column;

public class AuditTrailLinkConfiguration extends StoredObject implements Detail {

    public AuditTrailLinkConfiguration() {
    }

    public static void columns(Columns columns) {
    }

    public void setAuditTrailConfiguration(Id auditTrailConfigurationId) {
    }

    public void setAuditTrailConfiguration(BigDecimal idValue) {
    }

    public void setAuditTrailConfiguration(AuditTrailConfiguration auditTrailConfiguration) {
    }

    @Column(order = 1)
    public Id getAuditTrailConfigurationId() {
        return null;
    }

    public AuditTrailConfiguration getAuditTrailConfiguration() {
        return null;
    }

    public void setAllowAny(boolean any) {
    }

    @Column(order = 2)
    public boolean getAllowAny() {
        return false;
    }

    public void setLinkType(int link) {
    }

    @Column(order = 3)
    public int getLinkType() {
        return 0;
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }

    public Link<?> createLink(AuditTrailConfiguration masterConfiguration) {
        return null;
    }

    public Link<?> createLink(Class <? extends StoredObject > masterClass) {
        return null;
    }
}