package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.Temperature;
import com.storedobject.core.TimeDuration;
import com.storedobject.office.Filler;
import com.storedobject.office.ODT;
import com.storedobject.office.ODTReport;
import com.storedobject.ui.report.SectionSelector;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

import java.util.concurrent.TimeUnit;

public class Test extends DataForm {

    private final BooleanField raw = new BooleanField("Raw");

    public Test() {
        super("Test");
        addField(raw);
        addField(new TimeDurationField("Time Duration"));
        TimeDurationField x = new TimeDurationField("Another Time Duration");
        addField(x);
        x.setValue(new TimeDuration(100, TimeUnit.SECONDS));
        setFieldReadOnly(x);
        addField(new TemperatureField("Temperature"));
    }

    @Override
    protected boolean process() {
        message("Temperature: " + new Temperature());
        close();
        ODTReport r = new ODTReport(getApplication(), new Id("3464"));
        SectionSelector ss = new SectionSelector(r);
        r.setFiller(new F());
        r.setRawOutput(raw.getValue());
        ss.execute();
        return true;
    }

    public static class F extends Filler<Object> {

        private F() {
        }

        @Override
        public void customizeTable(ODT.Table table) {
            System.err.println("Table: " + table.getName());
            super.customizeTable(table);
        }

        @Override
        public void customizeImage(ODT.Image image) {
            System.err.println("Image: " + image.getName());
        }

        @Override
        public Object evaluate(ODT.Element element, String variableName) {
            if(element instanceof ODT.Image && variableName.equals("Image1")) {
                return getTransactionManager().getUser();
            }
            return super.evaluate(element, variableName);
        }
    }
}
