package com.storedobject.job;

import com.storedobject.core.*;
import java.math.BigDecimal;

public final class MessageEscalation extends StoredObject implements Detail {

    public MessageEscalation() {
    }

    public static void columns(Columns columns) {
    }

    public void setEscalateTo(Id escalateToId) {
    }

    public void setEscalateTo(BigDecimal idValue) {
    }

    public void setEscalateTo(MessageEscalationGroup escalateTo) {
    }

    public Id getEscalateToId() {
        return null;
    }

    public MessageEscalationGroup getEscalateTo() {
        return null;
    }

    public void setDays(int days) {
    }

    public int getDays() {
        return 0;
    }

    public Id getUniqueId() {
        return null;
    }

    public void copyValuesFrom(Detail detail) {
    }

    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
}