package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.sql.Timestamp;
import java.math.BigDecimal;

public final class Command extends StoredObject {

    private final Timestamp sentAt = DateUtility.now();
    private Id unitId;
    private Id commandId;
    private String commandValue;
    private boolean success;

    public Command() {
    }

    public static void columns(Columns columns) {
        columns.add("SentAt", "timestamp");
        columns.add("Unit", "id");
        columns.add("Command", "id");
        columns.add("CommandValue", "text");
        columns.add("Success", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("SentAt, Unit");
        indices.add("Unit, SentAt");
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt.setTime(sentAt.getTime());
        this.sentAt.setNanos(sentAt.getNanos());
    }

    @Column(order = 100)
    public Timestamp getSentAt() {
        return new Timestamp(sentAt.getTime());
    }

    public void setUnit(Id unitId) {
        this.unitId = unitId;
    }

    public void setUnit(BigDecimal idValue) {
        setUnit(new Id(idValue));
    }

    public void setUnit(Unit unit) {
        setUnit(unit == null ? null : unit.getId());
    }

    @Column(style = "(any)", order = 200)
    public Id getUnitId() {
        return unitId;
    }

    public Unit getUnit() {
        return getRelated(Unit.class, unitId, true);
    }

    public void setCommand(Id commandId) {
        this.commandId = commandId;
    }

    public void setCommand(BigDecimal idValue) {
        setCommand(new Id(idValue));
    }

    public void setCommand(ValueDefinition<?> command) {
        setCommand(command == null ? null : command.getId());
    }

    @Column(order = 300, style = "(any)")
    public Id getCommandId() {
        return commandId;
    }

    public ValueDefinition<?> getCommand() {
        return getRelated(ValueDefinition.class, commandId, true);
    }

    public void setCommandValue(String commandValue) {
        this.commandValue = commandValue;
    }

    @Column(order = 400, style = "(large)")
    public String getCommandValue() {
        return commandValue;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Column(order = 500)
    public boolean getSuccess() {
        return success;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (Utility.isEmpty(sentAt)) {
            throw new Invalid_Value("Sent at");
        }
        unitId = tm.checkTypeAny(this, unitId, Unit.class, false);
        commandId = tm.checkTypeAny(this, commandId, ValueDefinition.class, false);
        if (StringUtility.isWhite(commandValue)) {
            throw new Invalid_Value("Command Value");
        }
        super.validateData(tm);
    }

    @Override
    public void validateInsert() throws Exception {
        sentAt.setTime(DateUtility.now().getTime());
        super.validateInsert();
    }

    @Override
    public void validateUpdate() throws Exception {
        throw new Invalid_State("Update not allowed");
    }
}
