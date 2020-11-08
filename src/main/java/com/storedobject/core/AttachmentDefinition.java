package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public class AttachmentDefinition extends StoredObject {

    public AttachmentDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public void setClassName(String className) {
    }

    public String getClassName() {
        return "";
    }

    public void setDisplayOrder(int displayOrder) {
    }

    @Column(order = 200, required = false)
    public int getDisplayOrder() {
        return 0;
    }

    public void setName(String name) {
    }

    @Column(order = 300)
    public String getName() {
        return "";
    }

    public void setType(int type) {
    }

    @Column(order = 400)
    public int getType() {
        return 0;
    }

    public static String[] getTypeBitValues() {
        return new String[0];
    }

    public static String getTypeValue(int value) {
        return "";
    }

    public String getTypeValue() {
        return null;
    }

    public void setFileClassName(String fileClassName) {
    }

    @Column(order = 500, required = false)
    public String getFileClassName() {
        return "";
    }

    public void setMandatory(boolean mandatory) {
    }

    @Column(order = 600)
    public boolean getMandatory() {
        return false;
    }

    public void setCaption(String caption) {
    }

    @Column(order = 700, required = false)
    public String getCaption() {
        return "";
    }

    public UIFieldMetadata getMetadata() {
        return new UIFieldMetadata();
    }

    public Class<? extends FileData> getFileClass() {
        return FileData.class;
    }
}