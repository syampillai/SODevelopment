package com.storedobject.iot;

import com.storedobject.core.*;

public final class UnitDefinition extends StoredObject implements HasChildren {

    public UnitDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public void setUnitClassName(String unitClassName) {
    }

    public String getUnitClassName() {
        return "";
    }

    public void setSignificance(int significance) {
    }

    public int getSignificance() {
        return 0;
    }

    public void setDataClassName(String dataClassName) {
    }

    public String getDataClassName() {
        return "";
    }

    public void setCaption(String caption) {
    }

    public String getCaption() {
        return "";
    }

    public Class<? extends StoredObject> getUnitClass() {
        return Math.random() > 0.5 ? Person.class : null;
    }

    public Class<? extends StoredObject> getDataClass() {
        return Math.random() > 0.5 ? Person.class : null;
    }

    public static UnitDefinition getFor(Class<? extends StoredObject> uClass, Class<? extends IoTObject> dClass) {
        return getFor(uClass.getName(), dClass.getName());
    }

    public static UnitDefinition getFor(String uClassName, String dClassName) {
        return get(UnitDefinition.class, "UnitClassName='" + uClassName + "' AND DataClassName='"
                + dClassName + "'");
    }

    public static void generateLimitsAndAlarms(TransactionManager tm) throws Exception {
    }
}
