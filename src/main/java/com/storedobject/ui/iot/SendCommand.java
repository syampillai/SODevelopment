package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class SendCommand extends DataForm {

    private final ObjectField<Unit> unitField = new ObjectField<>("Unit", Unit.class, true);
    private final ObjectComboField<ValueDefinition> commandField = new ObjectComboField<>("Command",
            ValueDefinition.class, "Command AND Active", true);
    private ValueDefinition valueDefinition;
    private Unit unit;
    private final Consumer<Command> sendAction;
    private final boolean direct;
    private Block block;

    public SendCommand() {
        this(null, null);
    }

    public SendCommand(GUI gui) {
        this(null, gui);
    }


    public SendCommand(Consumer<Command> sendAction) {
        this(sendAction, null);
    }

    private SendCommand(Consumer<Command> sendAction, GUI gui) {
        super("Select Unit & Command");
        direct = false;
        this.sendAction = sendAction == null ? defaultAction() : sendAction;
        if(gui != null) {
            Block block = gui.block();
            if(block != null) {
                this.block = block;
                unitField.setFilter("Block=" + block.getId(), false);
            }
        }
        unitField.setLoadFilter(SendCommand::checkUnit);
        addField(unitField, commandField);
        unitField.addValueChangeListener(l -> {
            List<ValueDefinition> commands = new ArrayList<>();
            Unit unit = unitField.getObject();
            if(unit != null) {
                StoredObject.list(UnitDefinition.class, "UnitType=" + UnitType.getFor(unit.getClass()).getId())
                        .forEach(ud -> ud.listLinks(ValueDefinition.class, "Command", true)
                                .collectAll(commands));
            }
            commands.sort(Comparator.comparing(ValueDefinition::getCaption));
            commandField.load(commands);
        });
        setRequired(unitField);
        setRequired(commandField);
        setFirstFocus(unitField);
    }

    public SendCommand(Consumer<Command> sendAction, Unit unit, ValueDefinition valueDefinition) {
        super("");
        direct = true;
        this.sendAction = sendAction == null ? defaultAction() : sendAction;
        this.unit = unit;
        this.valueDefinition = valueDefinition;
    }

    public void setBlock(Block block) {
        if(block == null) {
            if(this.block == null) {
                return;
            }
            unitField.setFilter((String) null, true);
            this.block = null;
            return;
        }
        if(this.block != null && this.block.getId().equals(block.getId())) {
            return;
        }
        unitField.setFilter("Block=" + block.getId(), true);
        this.block = block;
    }

    private Consumer<Command> defaultAction() {
        return c -> {
            try {
                MQTTDataCollector.publish(c);
                message("Command sent successfully");
            } catch (Exception e) {
                error(e);
            }
        };
    }

    private static boolean checkUnit(Unit unit) {
        if(unit == null || !unit.getActive()) {
            return false;
        }
        ObjectIterator<UnitDefinition> uds = StoredObject.list(UnitDefinition.class,
                "UnitType=" + UnitType.getFor(unit.getClass()).getId());
        for(UnitDefinition ud: uds) {
            if(ud.existsLinks(ValueDefinition.class, "Command", true)) {
                uds.close();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(direct && unit != null ) {
            new AcceptCommandValue().execute();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        unit = unitField.getObject();
        clearAlerts();
        if(!unit.getActive()) {
            message("Unit is not active");
            return false;
        }
        valueDefinition = commandField.getObject();
        close();
        new AcceptCommandValue().execute();
        return true;
    }

    private class AcceptCommandValue extends DataForm {

        private final HasValue<?, ?> field;

        public AcceptCommandValue() {
            super("Command Value");
            String label = "New Value for [" + valueDefinition.getCaption() + "]";
            if (valueDefinition instanceof AlarmSwitch) {
                field = new RadioField<>(label, StringList.create("Off", "On"));
            } else {
                BigDecimalField f;
                f = new BigDecimalField(label, 18, 6, false, true);
                field = f;
                ValueLimit vl = (ValueLimit) valueDefinition;
                final double min = vl.getMinimum(), max = vl.getMaximum();
                final String m;
                if (min == max) {
                    m = "Only " + min + " is allowed";
                } else {
                    m = "Value must be between " + min + " and " + max;
                }
                addValidator(f, v -> v.doubleValue() >= min && v.doubleValue() <= max, m);
                setFirstFocus((Focusable<?>) field);
            }
            addField(new ELabelField("Unit", unit.toDisplay()));
            addField(new ELabelField("Current Value for [" + valueDefinition.getCaption() + "]",
                    v2s(valueDefinition.getValue(unit.getId())) + " "
                            + (valueDefinition instanceof ValueLimit vl ? vl.getUnitOfMeasurement().getUnit() : "")));
            addField(field);
        }

        private String v2s(Object v) {
            if(v == null) {
                return "Unknown";
            }
            if(v instanceof Boolean b) {
                return b ? "On" : "Off";
            }
            if(v instanceof String s) {
                return s.equals("On") ? "true" : "false";
            }
            return v.toString();
        }

        @Override
        protected boolean process() {
            close();
            Command command = new Command();
            command.setUnit(unit);
            command.setCommand(valueDefinition);
            command.setCommandValue(v2s(field.getValue()));
            sendAction.accept(command);
            return true;
        }
    }
}
