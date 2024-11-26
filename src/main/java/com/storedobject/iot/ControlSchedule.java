package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import com.storedobject.core.converter.MinutesValueConverter;
import java.math.BigDecimal;

public final class ControlSchedule extends Name {

    private static final String[] daysBitValues =
            new String[] {
                    "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
            };
    private int sendAt = 0;
    private int days = 0;
    private Id controlId;
    private double value;
    private boolean offOn;
    private boolean active;

    public ControlSchedule() {
    }

    public static void columns(Columns columns) {
        columns.add("SendAt", "int");
        columns.add("Days", "int");
        columns.add("Control", "id");
        columns.add("Value", "double precision");
        columns.add("OffOn", "boolean");
        columns.add("Active", "boolean");
    }

    public static String[] searchColumns() {
        return new String[] {
                "Name",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Name", "SendAt", "Days", "Control.Caption AS Control", "ControlValue"
        };
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        Controller.restart();
    }

    public static ControlSchedule get(String name) {
        return StoredObjectUtility.get(ControlSchedule.class, "Name", name, false);
    }

    public static ObjectIterator<ControlSchedule> list(String name) {
        return StoredObjectUtility.list(ControlSchedule.class, "Name", name, false);
    }

    public void setSendAt(int sendAt) {
        this.sendAt = sendAt % 1440;
    }

    @Column(order = 200, required = false)
    public int getSendAt() {
        return sendAt;
    }

    public static MinutesValueConverter getSendAtConverter() {
        return MinutesValueConverter.create("", false);
    }

    public static String getSendAtValue(int value) {
        return getSendAtConverter().format(value);
    }

    public String getSendAtValue() {
        return getSendAtConverter().format(sendAt);
    }

    public void setDays(int days) {
        this.days = days;
    }

    @Column(order = 300)
    public int getDays() {
        return days;
    }

    public static String[] getDaysBitValues() {
        return daysBitValues;
    }

    public static String getDaysValue(int value) {
        String[] s = getDaysBitValues();
        return StringUtility.bitsValue(value, s);
    }

    public String getDaysValue() {
        return getDaysValue(days);
    }

    public void setControl(Id controlId) {
        this.controlId = controlId;
    }

    public void setControl(BigDecimal idValue) {
        setControl(new Id(idValue));
    }

    public void setControl(ValueDefinition<?> control) {
        setControl(control == null ? null : control.getId());
    }

    @Column(style = "(any)", order = 400)
    public Id getControlId() {
        return controlId;
    }

    public ValueDefinition<?> getControl() {
        return getRelated(ValueDefinition.class, controlId, true);
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Column(required = false, order = 500)
    public double getValue() {
        return value;
    }

    public void setOffOn(boolean offOn) {
        this.offOn = offOn;
    }

    @Column(caption = "On", order = 600)
    public boolean getOffOn() {
        return offOn;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 100000)
    public boolean getActive() {
        return active;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        controlId = tm.checkTypeAny(this, controlId, com.storedobject.iot.ValueDefinition.class, false);
        super.validateData(tm);
    }

    public String getControlValue() {
        ValueDefinition<?> vd = getControl();
        if(vd instanceof ValueLimit vl) {
            return StringUtility.format(value, vl.getDecimals(), false) + vl.getUnitSuffix();
        }
        return offOn ? "On" : "Off";
    }

    String controlValue() {
        ValueDefinition<?> vd = getControl();
        if(vd instanceof ValueLimit vl) {
            return StringUtility.format(value, vl.getDecimals(), false);
        }
        return offOn ? "true" : "false";
    }
}
