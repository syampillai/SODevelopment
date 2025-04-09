package com.storedobject.ui.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TimestampPeriod;
import com.storedobject.iot.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.MultiSelectGrid;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

public abstract class SelectData extends BlockSelector {

    private final TimestampPeriodField periodField = new TimestampPeriodField("Period");
    private final MinutesField timeStepField = new MinutesField("Time Slice");

    public SelectData(String caption, Block block) {
        super(caption, block);
        addField(periodField, timeStepField);
        siteChanged(block.getSite());
    }

    @Override
    protected void siteChanged(Site newSite) {
        if(newSite != null && periodField != null) {
            int diff = newSite.getTimeDifference();
            TimestampPeriod period = periodField.getValue();
            Timestamp from = DateUtility.createTimestamp(period.getFrom().getTime()),
                    to = DateUtility.createTimestamp(period.getTo().getTime() + diff);
            periodField.setValue(new TimestampPeriod(from, to));
        }
    }

    @Override
    protected boolean accept(Block block) throws Exception {
        clearAlerts();
        if(!block.getActive()) {
            message("Not active - " + block.getName());
            return false;
        }
        List<Unit> units = StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active", true)
                .toList();
        if(units.isEmpty()) {
            message("No active units found for '" + block.getName() + "'");
            return false;
        }
        close();
        if(units.size() == 1) {
            accept(units.getFirst());
            return true;
        }
        new SelectUnit(units, block.getName()).execute();
        return true;
    }

    private void accept(Unit unit) {
        @SuppressWarnings("rawtypes") List<ValueDefinition> valueDefinitions = new ArrayList<>();
        buildTree(valueDefinitions, unit);
        clearAlerts();
        if(valueDefinitions.isEmpty()) {
            warning("No 'Value Definitions' found for '" + unit.getName() + "'");
        }
        if(valueDefinitions.size() == 1) {
            process(unit, valueDefinitions);
        } else {
            new SelectValues<>(ValueDefinition.class, valueDefinitions, unit).execute();
        }
    }

    private static void buildTree(@SuppressWarnings("rawtypes") List<ValueDefinition> values, Unit unit) {
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(unit.getSiteId()))
                .forEach(s -> buildBranches(values, s, unit));
    }

    private static void buildBranches(@SuppressWarnings("rawtypes") List<ValueDefinition> values,
                                      DataSet.AbstractData parent, Unit unit) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(ud.getUnit().getId().equals(unit.getId())) {
                    ud.getDataStatus().forEach(ds -> values.add(ds.getValueDefinition()));
                }
            }
            buildBranches(values, row, unit);
        });
    }

    protected void process(Unit unit, @SuppressWarnings("rawtypes") Collection<ValueDefinition> valueDefinitions) {
        Data4Unit.process(unit, valueDefinitions, periodField.getValue(), timeStepField.getValue() * 60000, getProcessor());
    }

    protected abstract Consumer<Data4Unit> getProcessor();

    private class SelectUnit extends DataForm {

        private final ObjectComboField<Unit> unitField;

        private SelectUnit(List<Unit> units, String blockName) {
            super("Select Unit");
            unitField = new ObjectComboField<>("Unit", Unit.class, units);
            addField(new ELabelField("Block", blockName), unitField);
            setRequired(unitField);
        }

        @Override
        protected boolean process() {
            close();
            accept(unitField.getValue());
            return true;
        }
    }

    private class SelectValues<T extends ValueDefinition<?>> extends MultiSelectGrid<T> {

        private final Unit unit;

        public SelectValues(Class<T> vdClass, List<T> items, Unit unit) {
            super(vdClass, items, StringList.create("Values"));
            this.unit = unit;
            setCaption("Choose Values");
            addConstructedListener(e -> buttonLayout.add(new ELabel("Unit: " + unit.getName())));
        }

        @SuppressWarnings("unused")
        public String getValues(T valueDefinition) {
            return valueDefinition.getCaption();
        }

        @Override
        protected void process(Set<T> selected) {
            if(selected.isEmpty()) {
                message("Nothing selected");
                return;
            }
            @SuppressWarnings("rawtypes") Set<ValueDefinition> set = new HashSet<>(selected);
            //noinspection
            SelectData.this.process(unit, set);
        }
    }
}
