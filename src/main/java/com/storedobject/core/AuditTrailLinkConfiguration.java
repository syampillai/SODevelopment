package com.storedobject.core;

public class AuditTrailLinkConfiguration extends com.storedobject.core.StoredObject implements com.storedobject.core.Detail {

    public AuditTrailLinkConfiguration() {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static java.lang.String[] browseColumns() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }

    public boolean isDetailOf(java.lang.Class <? extends com.storedobject.core.StoredObject > p1) {
        return false;
    }

    public void copyValuesFrom(com.storedobject.core.Detail p1) {
    }

    public com.storedobject.core.Id getUniqueId() {
        return null;
    }

    public void setAuditTrailConfiguration(com.storedobject.core.Id p1) {
    }

    public void setAuditTrailConfiguration(java.math.BigDecimal p1) {
    }

    public void setAuditTrailConfiguration(com.storedobject.core.AuditTrailConfiguration p1) {
    }

    @com.storedobject.core.annotation.Column(order = 1, caption = "", required = true, style = "")
    public com.storedobject.core.Id getAuditTrailConfigurationId() {
        return null;
    }

    public com.storedobject.core.AuditTrailConfiguration getAuditTrailConfiguration() {
        return null;
    }

    public void setAllowAny(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 2, caption = "", required = true, style = "")
    public boolean getAllowAny() {
        return false;
    }

    public void setLinkType(int p1) {
    }

    @com.storedobject.core.annotation.Column(order = 3, caption = "", required = true, style = "")
    public int getLinkType() {
        return 0;
    }

    public com.storedobject.core.StoredObjectUtility.Link <?> createLink() {
        return null;
    }
}
