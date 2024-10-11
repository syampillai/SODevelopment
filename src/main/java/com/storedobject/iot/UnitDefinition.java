package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class UnitDefinition extends StoredObject implements HasChildren {

    private Id unitTypeId;
    private UnitType unitType;
    private String dataClassName = "";
    private int significance = 0;
    private String caption;
    private Class<? extends Data> dataClass;
    private int updateFrequency = 900;

    public UnitDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("UnitType", "id");
        columns.add("Significance", "int");
        columns.add("DataClassName", "text");
        columns.add("UpdateFrequency", "int");
        columns.add("Caption", "text");
    }

    public static void indices(Indices indices) {
        indices.add("UnitType,DataClassName", true);
    }

    @Override
    public String getUniqueCondition() {
        return "UnitType=" + unitTypeId + " AND DataClassName='" + getDataClassName() + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Limits|com.storedobject.iot.ValueLimit",
                "Alarms|com.storedobject.iot.AlarmSwitch",
        };
    }

    public void setUnitType(Id unitTypeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Unit Type");
        }
        this.unitTypeId = unitTypeId;
    }

    public void setUnitType(BigDecimal idValue) {
        setUnitType(new Id(idValue));
    }

    public void setUnitType(UnitType unitType) {
        setUnitType(unitType == null ? null : unitType.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getUnitTypeId() {
        return unitTypeId;
    }

    public UnitType getUnitType() {
        if(unitType == null) {
            unitType = getRelated(UnitType.class, unitTypeId);
        }
        return unitType;
    }

    public String getUnitClassName() {
        return getUnitType().getUnitClassName();
    }

    public void setSignificance(int significance) {
        this.significance = significance;
    }

    @Column(order = 200, required = false)
    public int getSignificance() {
        return significance;
    }

    public void setDataClassName(String dataClassName) {
        this.dataClassName = dataClassName;
    }

    @Column(order = 500)
    public String getDataClassName() {
        return dataClassName;
    }

    public void setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    @Column(order = 600, required = false, caption = "Update Frequency (In Seconds)")
    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Column(order = 700)
    public String getCaption() {
        return caption;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(caption)) {
            throw new Invalid_Value("Caption");
        }
        Class<? extends StoredObject> dataClass = getDataClass();
        if(dataClass == null) {
            throw new Invalid_Value("Data Class Name = " + dataClassName);
        }
        checkForDuplicate("UnitType", "DataClassName");
        unitTypeId = tm.checkType(this, unitTypeId, UnitType.class, false);
        super.validateData(tm);
    }

    public Class<? extends Unit> getUnitClass() {
        return getUnitType().getUnitClass();
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        unitType = null;
        DataSet.scheduleRefresh();
    }

    public Class<? extends Data> getDataClass() {
        if(getUnitClass() == null) {
            return null;
        }
        if(dataClass != null && dataClass.getName().equals(dataClassName)) {
            return dataClass;
        }
        Class<? extends StoredObject> soClass = UnitType.soClass(dataClassName);
        if(soClass != null && Data.class.isAssignableFrom(soClass)) {
            //noinspection unchecked
            dataClass = (Class<? extends Data>) soClass;
            if(dataClass == Data.class) {
                dataClass = null;
            } else {
                int p = dataClassName.indexOf("Data");
                if(p < 0 || !dataClassName.substring(0, p).equals(getUnitClassName())) {
                    dataClass = null;
                }
            }
        }
        return dataClass;
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        if(child instanceof ValueLimit v) {
            check(v);
        } else if(child instanceof AlarmSwitch a) {
            check(a);
        }
    }

    private void check(ValueLimit valueLimit) throws Invalid_State {
        Method m = m(valueLimit.getName());
        Class<?> type = m == null ? null : m.getReturnType();
        if(m != null && type == double.class || type == int.class || type == long.class) {
            return;
        }
        throw new Invalid_State("Invalid Limit Field - " + valueLimit.getName());
    }

    private void check(AlarmSwitch alarmSwitch) throws Invalid_State {
        Method m;
        if((m = m(alarmSwitch.getName())) == null || m.getReturnType() != boolean.class) {
            throw new Invalid_State("Invalid Alarm Switch - " + alarmSwitch.getName());
        }
    }

    private Method m(String field) {
        return m(field, getDataClass());
    }

    private static Method m(String field, Class<?> ofClass) {
        if(ofClass == null) {
            return null;
        }
        try {
            return ofClass.getMethod("get" + field);
        } catch(NoSuchMethodException e) {
            return null;
        }
    }

    public static UnitDefinition getFor(Class<? extends Unit> uClass, Class<? extends Data> dClass) {
        if(uClass == null || dClass == null) {
            return null;
        }
        return getFor(uClass.getName(), dClass.getName());
    }

    public static UnitDefinition getFor(String uClassName, String dClassName) {
        return get(UnitDefinition.class, "UnitType=" + UnitType.getFor(uClassName).getId()
                + " AND DataClassName='" + dClassName + "'");
    }

    public static void generateLimitsAndAlarms(TransactionManager tm) throws Exception {
        List<? extends Class<Data>> iotClasses = ClassAttribute.get(Data.class).listChildClasses(true);
        Class<? extends Unit> uClass;
        int methodAttr;
        UnitDefinition unitDefinition;
        String field;
        boolean added;
        ValueLimit valueLimit;
        AlarmSwitch alarmSwitch;
        List<ValueLimit> limitList;
        List<AlarmSwitch> alarmList;
        List<ValueDefinition<?>> toAdd;
        Transaction t = null;
        try {
            t = tm.createTransaction();
            for(Class<Data> iotClass : iotClasses) {
                if(Modifier.isAbstract(iotClass.getModifiers())) {
                    continue;
                }
                uClass = Data.getUnitClass(iotClass);
                if(uClass == null) {
                    continue;
                }
                UnitType.create(tm, uClass); // Make sure that the Unit Type is created
                unitDefinition = getFor(uClass, iotClass);
                if(unitDefinition == null) {
                    limitList = new ArrayList<>();
                    alarmList = new ArrayList<>();
                    added = true;
                    unitDefinition = new UnitDefinition();
                    unitDefinition.setUnitType(UnitType.create(tm, uClass));
                } else {
                    limitList = unitDefinition.listLinks(ValueLimit.class).toList();
                    alarmList = unitDefinition.listLinks(AlarmSwitch.class).toList();
                    added = false;
                }
                if(!unitDefinition.getDataClassName().equals(iotClass.getName())) {
                    unitDefinition.setCaption(StringUtility.makeLabel(uClass));
                }
                unitDefinition.setDataClassName(iotClass.getName());
                toAdd = new ArrayList<>();
                for(Method m: iotClass.getMethods()) {
                    field = m.getName();
                    if(field.length() <= 3 || !field.startsWith("get") || m.getParameterCount() > 0) {
                        continue;
                    }
                    methodAttr = m.getModifiers();
                    if(!Modifier.isPublic(methodAttr) || Modifier.isStatic(methodAttr)) {
                        continue;
                    }
                    field = field.substring(3);
                    switch (field) {
                        case "Unit", "CollectedAt" -> {
                            continue;
                        }
                    }
                    if(m.getReturnType() == boolean.class) {
                        alarmSwitch = null;
                        if(!added) {
                            String finalField = field;
                            alarmSwitch = alarmList.stream().filter(v -> v.getName().equals(finalField))
                                    .findAny().orElse(null);
                        }
                        if(alarmSwitch == null) {
                            alarmSwitch = new AlarmSwitch();
                            alarmSwitch.setName(field);
                            alarmSwitch.makeVirtual();
                            toAdd.add(alarmSwitch);
                        } else {
                            alarmList.remove(alarmSwitch);
                        }
                    } else {
                        Class<?> type = m.getReturnType();
                        if(type == double.class || type == int.class || type == long.class) {
                            valueLimit = null;
                            if(!added) {
                                String finalField = field;
                                valueLimit = limitList.stream().filter(v -> v.getName().equals(finalField))
                                        .findAny().orElse(null);
                            }
                            if(valueLimit == null) {
                                valueLimit = new ValueLimit();
                                valueLimit.setName(field);
                                toAdd.add(valueLimit);
                            } else {
                                limitList.remove(valueLimit);
                            }
                        }
                    }
                }
                for(AlarmSwitch v: alarmList) {
                    v.delete(t);
                }
                for(ValueLimit v: limitList) {
                    v.delete(t);
                }
                unitDefinition.save(t);
                for(ValueDefinition<?> vd: toAdd) {
                    if(vd.isVirtual()) {
                        vd.makeNew();
                    }
                    vd.save(t);
                    unitDefinition.addLink(vd);
                }
            }
            t.commit();
        } finally {
            if(t != null) {
                t.rollback();
            }
        }
    }
}
