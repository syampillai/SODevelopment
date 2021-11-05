package com.storedobject.core;

public final class PseudoTranDetail extends StoredObject implements Detail {

    public PseudoTranDetail() {
    }

    public static void columns(Columns columns) {
    }

    public int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
        return new String[0];
    }

    public static String getStatusValue(int value) {
        return "";
    }

    public String getStatusValue() {
        return "";
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return masterClass == PseudoTran.class;
    }

    public StoredObject getObject() {
        return new Person();
    }

    public String getObjectLabel() {
        return "";
    }

    public String getChanges() {
        return "";
    }
}
