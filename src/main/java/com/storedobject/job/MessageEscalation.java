package com.storedobject.job;

import com.storedobject.core.*;
import java.math.BigDecimal;

public final class MessageEscalation extends StoredObject implements Detail {

    private Id escalateToId;
    private int days;

    public MessageEscalation() {
    }

    public static void columns(Columns columns) {
        columns.add("EscalateTo", "id");
        columns.add("Days", "int");
    }

    public void setEscalateTo(Id escalateToId) {
        this.escalateToId = escalateToId;
    }

    public void setEscalateTo(BigDecimal idValue) {
        setEscalateTo(new Id(idValue));
    }

    public void setEscalateTo(MessageEscalationGroup escalateTo) {
        setEscalateTo(escalateTo == null ? null : escalateTo.getId());
    }

    public Id getEscalateToId() {
        return escalateToId;
    }

    public MessageEscalationGroup getEscalateTo() {
        return get(MessageEscalationGroup.class, escalateToId);
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
    	if(days <= 0) {
    		throw new Invalid_Value("Days");
    	}
        escalateToId = tm.checkType(this, escalateToId, MessageEscalationGroup.class, false);
        super.validateData(tm);
    }

    @Override
    public Id getUniqueId() {
        return getId();
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return masterClass == MessageGroup.class;
    }
}