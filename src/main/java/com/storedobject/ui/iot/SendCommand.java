package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.vaadin.BigDecimalField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioField;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;

import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class SendCommand extends CommandSelector {

    private final Consumer<Command> sendAction;
    private Application application;

    public SendCommand() {
        this(null, null);
    }

    public SendCommand(Block block) {
        this(null, block);
    }

    public SendCommand(Consumer<Command> sendAction) {
        this(sendAction, null);
    }

    private SendCommand(Consumer<Command> sendAction, Block block) {
        super(block);
        this.sendAction = sendAction == null ? defaultAction() : sendAction;
    }

    public SendCommand(Consumer<Command> sendAction, Unit unit, ValueDefinition valueDefinition) {
        super(unit, valueDefinition);
        this.sendAction = sendAction == null ? defaultAction() : sendAction;
    }

    private Consumer<Command> defaultAction() {
        if(application == null) {
            application = Application.get();
        }
        return c -> {
            application.startPolling(SendCommand.this);
            try {
                long t = System.currentTimeMillis();
                MQTTDataCollector.publish(c);
                t = System.currentTimeMillis() - t;
                String m = "Command sent successfully (Took " + t + " ms)";
                application.access(() -> warning(m));
            } catch (Exception e) {
                application.access(() -> error(e));
            }
            application.stopPolling(SendCommand.this);
        };
    }

    @Override
    protected void processCommandFor(Unit unit, ValueDefinition<?> valueDefinition) {
        new AcceptCommandValue(unit, valueDefinition, sendAction).execute();
    }

    private static class AcceptCommandValue extends DataForm {

        private final HasValue<?, ?> field;
        private final Unit unit;
        private final ValueDefinition<?> valueDefinition;
        private final Consumer<Command> sendAction;

        public AcceptCommandValue(Unit unit, ValueDefinition<?> valueDefinition, Consumer<Command> sendAction) {
            super("Command Value");
            this.unit = unit;
            this.valueDefinition = valueDefinition;
            this.sendAction = sendAction;
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
            return switch (v) {
                case null -> "Unknown";
                case Boolean b -> b ? "On" : "Off";
                case String s -> s.equals("On") ? "true" : "false";
                default -> v.toString();
            };
        }

        @Override
        protected boolean process() {
            close();
            Command command = new Command();
            command.setUnit(unit);
            command.setCommand(valueDefinition);
            command.setCommandValue(v2s(field.getValue()));
            message("Command processing initiated");
            Thread.startVirtualThread(() -> sendAction.accept(command));
            return true;
        }
    }
}
