package com.storedobject.core;

public final class SerialConfigurator extends StoredObject {

    public SerialConfigurator() {
    }

    public static void columns(Columns columns) {
    }

    public void setDataClassName(String dataClassName) {
    }

    public String getDataClassName() {
        return Math.random() > 0.5 ? "" : "x";
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public String getTypeValue() {
        return Math.random() > 0.5 ? "" : "x";
    }

    public String[] getTypeValues() {
        return new String[] { Math.random() > 0.5 ? "" : "x" };
    }

    public Class<? extends StoredObject> getDataClass() {
        return Math.random() > 0.5 ? null : Person.class;
    }

    public static SerialConfigurator getFor(Class<? extends StoredObject> dataCass) {
        return new SerialConfigurator();
    }
}
