package com.storedobject.ui.iot;

import com.storedobject.common.MathUtility;
import com.storedobject.core.*;
import com.storedobject.iot.Block;
import com.storedobject.iot.Data;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ELabelField;
import com.storedobject.vaadin.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Consumer;

public class SmoothenInvalidData extends SelectData {

    public SmoothenInvalidData() {
        this(null);
    }

    public SmoothenInvalidData(Block block) {
        super("Smoothen Invalid Data", block, true, true);
    }

    @Override
    protected Consumer<Data4Unit> getProcessor() {
        return d4u -> new DataView<>(d4u, false) {
            @Override
            protected void addExtraButtons(ButtonLayout buttonLayout) {
                buttonLayout.add(new Button("Select Data Range", e -> new SmoothenData(d4u).execute()));
            }
        }.execute();
    }

    private class SmoothenData extends DataForm {

        private final Data4Unit data4Unit;
        private final BigDecimalField lowerValueField = new BigDecimalField(),
                upperValueField = new BigDecimalField();

        public SmoothenData(Data4Unit data4Unit) {
            super("Smoothen Data");
            this.data4Unit = data4Unit;
            ELabelField warn = new ELabelField("Warning:",
                    "Data outside the valid range will be smoothened.",
                    Application.COLOR_ERROR);
            addField(warn,
                    new CompoundField("Valid Data Range", lowerValueField, new ELabel("to"),
                            upperValueField));
        }

        @Override
        protected boolean process() {
            clearAlerts();
            BigDecimal lowerValue = lowerValueField.getValue(), upperValue = upperValueField.getValue();
            if(lowerValue.compareTo(upperValue) > 0) {
                warning("Invalid Range");
                return false;
            }
            close();
            new ActionForm(describe(data4Unit, lowerValue, upperValue) + "\nAre you sure?",
                    () -> smoothenData(data4Unit, lowerValue, upperValue)).execute();
            return true;
        }
    }

    private static String describe(Data4Unit data4Unit, BigDecimal lowerValue, BigDecimal upperValue) {
        return "For '" + data4Unit.unit().getName() + "', values of '"
                + StringUtility.makeLabel(data4Unit.attributes().stream().skip(1).findFirst().orElse(""))
                + "' falling outside the range (" + lowerValue + " to " + upperValue + ") will be smoothened.";
    }

    private void smoothenData(Data4Unit data4Unit, BigDecimal lowerValue, BigDecimal upperValue) {
        ClassAttribute<?> ca = ClassAttribute.get(data4Unit.dataClass());
        String tableName = ca.getModuleName() + "." + ca.getTableName();
        double currentValue;
        String attribute = data4Unit.attributes().stream().skip(1).findFirst().orElse("");
        attribute = attribute.substring(0, attribute.indexOf(" AS "));
        Method m = ca.getMethod(attribute);
        QueryBuilder<?> qb = QueryBuilder.from(data4Unit.dataClass())
                .where(data4Unit.condition() + " AND " + attribute + " NOT BETWEEN " + lowerValue + " AND "
                        + upperValue);
        Data data;
        int count = 0;
        try(ObjectIterator<?> list = qb.list()) {
            QueryBuilder<?> rv = QueryBuilder.from(data4Unit.dataClass()).columns(attribute).limit(1);
            Query rq;
            long timestamp;
            double lower = 0, upper, value;
            boolean notFound;
            String c = "Unit=" + data4Unit.unit().getId() + " AND " + attribute + " BETWEEN " + lowerValue + " AND "
                    + upperValue + " AND CollectedAt";
            for(StoredObject so: list) {
                ++count;
                data = (Data) so;
                try {
                    timestamp = data.getCollectedAt();
                    rq = rv.orderBy("CollectedAt DESC").where(c + "<" + timestamp).query();
                    notFound = rq.eoq();
                    if(!notFound) {
                        lower = rq.getResultSet().getDouble(1);
                    }
                    rq.close();
                    rq = rv.orderBy("CollectedAt").where(c + ">" + timestamp).query();
                    if(rq.eoq()) {
                        if(notFound) {
                            value = lowerValue.add(upperValue).divide(BigDecimal.valueOf(2), MathContext.UNLIMITED)
                                    .doubleValue();
                        } else {
                            value = lower;
                        }
                    } else {
                        upper = rq.getResultSet().getDouble(1);
                        if(notFound) {
                            value = upper;
                        } else {
                            value = (lower + upper) / 2;
                        }
                    }
                    rq.close();
                    currentValue = (double) m.invoke(data);
                    if(MathUtility.equals(currentValue, value)) {
                        continue;
                    }
                    System.err.println("-- UTC Time: " + DateUtility.format(data.getTimestamp()) + ", Current Value: " + currentValue);
                    System.err.println("UPDATE " + tableName + " SET " + attribute + " = " + value + " WHERE Id = " + data.getId() + ";");
                    data.setRawValue(attribute, value);
                } catch (Exception e) {
                    error(e);
                }
            }
        }
        warning("Smoothened " + count + " invalid data");
    }
}
