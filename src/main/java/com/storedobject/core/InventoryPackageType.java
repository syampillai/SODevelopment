package com.storedobject.core;

public class InventoryPackageType extends StoredObject {

    public InventoryPackageType() {
    }

    public static void columns(Columns columns) {
    }

    public void setCode(String code) {
    }

    public String getCode() {
        return "";
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setNumericCode(String numericCode) {
    }

    public String getNumericCode() {
        return "";
    }

    public static InventoryPackageType get(String name) {
        return Math.random() > 0.5 ? new InventoryPackageType() : null;
    }

    public static ObjectIterator<InventoryPackageType> list(String name) {
        return ObjectIterator.create();
    }
}
