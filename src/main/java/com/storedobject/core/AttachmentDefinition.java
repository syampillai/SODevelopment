package com.storedobject.core;

public class AttachmentDefinition extends com.storedobject.core.StoredObject {

    public AttachmentDefinition() {
    }

    @com.storedobject.core.annotation.Column(order = 300, caption = "", required = true, style = "")
    public java.lang.String getName() {
        return null;
    }

    public void setName(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 400, caption = "", required = true, style = "")
    public int getType() {
        return 0;
    }

    @com.storedobject.core.annotation.Column(order = 100, caption = "", required = true, style = "")
    public java.lang.String getClassName() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public void validateData() throws java.lang.Exception {
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    public static int hints() {
        return 0;
    }

    public void setClassName(java.lang.String p1) {
    }

    public void setDisplayOrder(int p1) {
    }

    @com.storedobject.core.annotation.Column(order = 200, caption = "", required = true, style = "")
    public int getDisplayOrder() {
        return 0;
    }

    public void setType(int p1) {
    }

    public static java.lang.String[] getTypeValues() {
        return null;
    }

    public static java.lang.String getTypeValue(int p1) {
        return null;
    }

    public java.lang.String getTypeValue() {
        return null;
    }

    public void setMandatory(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 500, caption = "", required = true, style = "")
    public boolean getMandatory() {
        return false;
    }

    public void setCaption(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 500, caption = "", required = false, style = "")
    public java.lang.String getCaption() {
        return null;
    }

    public com.storedobject.core.UIFieldMetadata getMetadata() {
        return null;
    }
}
