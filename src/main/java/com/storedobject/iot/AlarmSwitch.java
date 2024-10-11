package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public final class AlarmSwitch extends ValueDefinition<Boolean> {

    private static final String[] alarmWhenValues =
            new String[] {
                    "Off", "On",
            };
    private int alarmWhen = 0;

    public AlarmSwitch() {
    }

    public static void columns(Columns columns) {
        columns.add("AlarmWhen", "int");
    }

    public void setAlarmWhen(int alarmWhen) {
        this.alarmWhen = alarmWhen;
    }

    @Column(order = 400)
    public int getAlarmWhen() {
        return alarmWhen;
    }

    public static String[] getAlarmWhenValues() {
        return alarmWhenValues;
    }

    public static String getAlarmWhenValue(int value) {
        String[] s = getAlarmWhenValues();
        return s[value % s.length];
    }

    public String getAlarmWhenValue() {
        return getAlarmWhenValue(alarmWhen);
    }

    @Override
    public String toString() {
        return getCaption() + " (On/Off)";
    }
}
