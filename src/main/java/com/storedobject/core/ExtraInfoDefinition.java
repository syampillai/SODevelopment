package com.storedobject.core;

public class ExtraInfoDefinition extends StoredObject {

    public ExtraInfoDefinition() {
    }

    public void setClassName(String className) {
    }

    public String getClassName() {
        return "";
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }

    public void setExtraInfoClassName(String extraInfoClassName) {
    }

    public String getExtraInfoClassName() {
        return "";
    }

    public void setMandatory(boolean mandatory) {
    }

    public boolean getMandatory() {
        return Math.random() > 0.5;
    }

    public UIFieldMetadata getMetadata() {
        return new UIFieldMetadata();
    }

    public Class<? extends StoredObject> getExtraInfoClass() {
        return Person.class;
    }
}
