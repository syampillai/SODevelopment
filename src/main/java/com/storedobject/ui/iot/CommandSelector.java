package com.storedobject.ui.iot;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class CommandSelector extends DataForm {

    private final ObjectField<Unit> unitField;
    private final ObjectComboField<ValueDefinition> commandField = new ObjectComboField<>("Command",
            ValueDefinition.class, "Command AND Active", true);
    private ValueDefinition valueDefinition;
    private Unit unit;
    private final boolean direct;

    public CommandSelector() {
        this(null);
    }

    public CommandSelector(Block block) {
        super("Select Unit & Command");
        direct = false;
        unitField = createUnitField(block);
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

    public CommandSelector(Unit unit, ValueDefinition valueDefinition) {
        super("");
        unitField = null;
        direct = true;
        this.unit = unit;
        this.valueDefinition = valueDefinition;
    }

    private static ObjectField<Unit> createUnitField(Block block) {
        ObjectField<Unit> uf;
        if(block == null) {
            uf = new ObjectField<>("Unit", Unit.class, true);
        } else {
            uf = new ObjectField<>("Unit", Unit.class, true, ObjectField.Type.CHOICE);
            uf.setFilter("Block=" + block.getId(), false);
        }
        uf.setLoadFilter(CommandSelector::checkUnit);
        return uf;
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

    public void setBlock(Block block) {
        if(block == null || unitField == null) {
            return;
        }
        unitField.setFilter("Block=" + block.getId(), true);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(direct && unit != null && valueDefinition != null ) {
            processCommandFor(unit, valueDefinition);
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected final boolean process() {
        unit = unitField.getObject();
        clearAlerts();
        if(!unit.getActive()) {
            message("Unit is not active");
            return false;
        }
        valueDefinition = commandField.getObject();
        close();
        processCommandFor(unit, valueDefinition);
        return true;
    }

    protected abstract void processCommandFor(Unit unit, ValueDefinition<?> valueDefinition);
}
