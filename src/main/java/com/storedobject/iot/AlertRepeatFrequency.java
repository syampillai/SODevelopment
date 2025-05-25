package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import com.storedobject.core.converter.*;

public class AlertRepeatFrequency extends StoredObject {

    private Id systemEntityId;
    private int significance;
    private int frequency = 120;

    public AlertRepeatFrequency() {}

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Significance", "int");
        columns.add("Frequency", "int");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity, Significance", true);
    }

    @Override
    public String getUniqueCondition() {
        return "SystemEntity=" + getSystemEntityId() + " AND " + "Significance=" + getSignificance();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setSystemEntity(Id systemEntityId) {
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @Column(caption = "Entity", order = 100)
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return getRelated(SystemEntity.class, systemEntityId);
    }

    public void setSignificance(int significance) {
        this.significance = significance;
    }

    @Column(caption = "Parameter Significance", order = 200, required = false)
    public int getSignificance() {
        return significance;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Column(caption = "Repeat Frequency", order = 300)
    public int getFrequency() {
        return frequency;
    }

    public static MinutesValueConverter getFrequencyConverter() {
        return MinutesValueConverter.create("", false);
    }

    public static String getFrequencyValue(int value) {
        return getFrequencyConverter().format(value);
    }

    public String getFrequencyValue() {
        return getFrequencyConverter().format(frequency);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class, false);
        checkForDuplicate("SystemEntity", "Significance");
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        AlertGenerator.frequency.remove(systemEntityId + "/" + significance);
    }

    public static AlertRepeatFrequency get(int significance, TransactionManager tm) {
        SystemEntity systemEntity = tm.getEntity();
        AlertRepeatFrequency arf = get(AlertRepeatFrequency.class, "SystemEntity=" + systemEntity.getId()
                + " AND Significance=" + significance);
        if(arf == null) {
            arf = new AlertRepeatFrequency();
            arf.setSystemEntity(systemEntity);
            arf.setSignificance(significance);
            try {
                tm.transact(arf::save);
            } catch (Exception ignored) {
            }
        }
        return arf;
    }
}
