package com.storedobject.iot;

import com.storedobject.core.Columns;
import com.storedobject.core.Detail;
import com.storedobject.core.StoredObject;

public final class AlarmSwitch extends StoredObject implements Detail {

    public AlarmSwitch() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setCaption(String caption) {
    }

    public String getCaption() {
        return "";
    }

    public void setSignificance(int significance) {
    }

    public int getSignificance() {
        return 0;
    }

    public void setAlert(boolean alert) {
    }

    public boolean getAlert() {
        return true;
    }

    public void setAlarmWhen(int alarmWhen) {
    }

    public int getAlarmWhen() {
        return 0;
    }

    public static String[] getAlarmWhenValues() {
        return new String[] {};
    }

    public static String getAlarmWhenValue(int value) {
        return "";
    }

    public String getAlarmWhenValue() {
        return "";
    }

    public void setActive(boolean active) {
    }

    public boolean getActive() {
        return true;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == UnitDefinition.class;
    }
}
