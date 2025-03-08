package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

public class LimitMessage extends StoredObject {

    private Id valueNameId;
    private int condition;
    private String message;

    private static final String[] conditionValues = new String[] {
            "Lowest", "Lower", "Low", "High", "Higher", "Highest",
    };

    public LimitMessage() {}

    public static void columns(Columns columns) {
        columns.add("ValueName", "id");
        columns.add("Condition", "int");
        columns.add("Message", "text");
    }

    public static void indices(Indices indices) {
        indices.add("ValueName,Condition", true);
    }

    public void setValueName(Id valueNameId) {
        if (!loading() && !Id.equals(this.getValueNameId(), valueNameId)) {
            throw new Set_Not_Allowed("Value Name");
        }
        this.valueNameId = valueNameId;
    }

    public void setValueName(BigDecimal idValue) {
        setValueName(new Id(idValue));
    }

    public void setValueName(ValueLimit valueName) {
        setValueName(valueName == null ? null : valueName.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getValueNameId() {
        return valueNameId;
    }

    public ValueLimit getValueName() {
        return getRelated(ValueLimit.class, valueNameId, true);
    }

    public static String[] getConditionValues() {
        return conditionValues;
    }
    public void setCondition(int condition) {
        this.condition = condition;
    }

    @Column(required = false, order = 200)
    public int getCondition() {
        return condition;
    }

    public static String getConditionValue(int value) {
        String[] s = getConditionValues();
        return s[value % s.length];
    }

    public String getConditionValue() {
        return getConditionValue(condition);
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Column(order = 300)
    public String getMessage() {
        return message;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(message)) {
            throw new Invalid_Value("Message");
        }
        valueNameId = tm.checkType(this, valueNameId, ValueLimit.class, false);
        checkForDuplicate("ValueName", "Condition");
        super.validateData(tm);
    }
}
