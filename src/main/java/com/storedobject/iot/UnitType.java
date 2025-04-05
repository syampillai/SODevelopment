package com.storedobject.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

public final class UnitType extends StoredObject {

    private String unitClassName;
    private Class<? extends Unit> unitClass;
    private String statistics = "";

    public UnitType() {
    }

    public static void columns(Columns columns) {
        columns.add("UnitClassName", "text");
        columns.add("Statistics", "text");
    }

    public static void indices(Indices indices) {
        indices.add("UnitClassName", true);
    }

    @Override
    public String getUniqueCondition() {
        return "UnitClassName='" + getUnitClassName() + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        DataSet.refresh();
    }

    public void setUnitClassName(String unitClassName) {
        if(!loading()) {
            throw new Set_Not_Allowed("Unit Class Name");
        }
        this.unitClassName = unitClassName;
    }

    @SetNotAllowed
    @Column(order = 100)
    public String getUnitClassName() {
        return unitClassName;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    @Column(order = 200, required = false, caption = "Statistics Variables (Comma seperated)")
    public String getStatistics() {
        return statistics;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            super.validateData(tm);
            return;
        }
        unitClassName = StringUtility.pack(unitClassName);
        Class<? extends Unit> unitClass = getUnitClass();
        if(unitClass == null) {
            throw new Invalid_Value("Unit Class Name = " + unitClassName);
        }
        checkForDuplicate("UnitClassName");
        StringList list = StringList.create(statistics);
        if(!list.isEmpty()) {
            for(String s : list) {
                if(!StringUtility.isLetterOrDigit(s) || Character.isDigit(s.charAt(0))
                        || !Character.isUpperCase(s.charAt(0))) {
                    throw new Invalid_Value("Statistics Variable '" + s + "'");
                }
                if(list.indexOf(s) != list.lastIndexOf(s)) {
                    throw new Invalid_Value("Duplicate Statistics Variable '" + s + "'");
                }
            }
        }
        super.validateData(tm);
    }

    public Class<? extends Unit> getUnitClass() {
        if(unitClass != null && unitClass.getName().equals(unitClassName)) {
            return unitClass;
        }
        //noinspection unchecked
        unitClass = (Class<? extends Unit>) soClass(unitClassName);
        if(unitClass == Unit.class) {
            unitClass = null;
        }
        return unitClass;
    }

    static Class<? extends StoredObject> soClass(String name) {
        try {
            //noinspection unchecked
            return (Class<? extends StoredObject>) JavaClassLoader.getLogic(name);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static UnitType getFor(Class<? extends Unit> uClass) {
        return getFor(uClass.getName());
    }

    public static UnitType getFor(String uClassName) {
        return get(UnitType.class, "UnitClassName='" + uClassName + "'");
    }

    public static UnitType create(TransactionManager tm, Class<? extends Unit> uClass) throws Exception {
        UnitType ut = getFor(uClass.getName());
        if(ut == null) {
            ut = new UnitType();
            ut.setUnitClassName(uClass.getName());
            if(tm == null) {
                ut.makeVirtual();
            } else {
                tm.transact(ut::save);
            }
        }
        return ut;
    }

    @Override
    public String toString() {
        return StringUtility.makeLabel(unitClassName.substring(unitClassName.lastIndexOf('.')));
    }
}
