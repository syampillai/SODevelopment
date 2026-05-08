package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.office.Filler;
import com.storedobject.office.ODT;
import com.storedobject.office.ODTReport;
import com.storedobject.pdf.PDFReport;
import com.storedobject.report.ObjectList;
import com.storedobject.svg.chart.Chart;
import com.storedobject.ui.report.SectionSelector;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.svg.chart.Bars;

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
        ODTReport r = new ODTReport(getApplication(), new Id("4415"));
        SectionSelector ss = new SectionSelector(r);
        r.setFiller(new F());
        r.setRawOutput(raw.getValue());
        ss.execute();
        new R().execute();
        return true;
    }

    private static Chart chart() {
        Bars c = new Bars();
        c.setUnit("%");
        c.setLabelName("Categories");
        c.setValueName("Category");
        c.addValue("Category A", 10);
        c.addValue("Category B", 90);
        c.addValue("Category C", 45);
        c.addValue("Category D", 20);
        c.addValue("Category E", 30);
        c.addValue("Category F", 50);
        c.addValue("Category G", 15);
        c.addValue("Category H", 100);
        return c;
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
            if(element instanceof ODT.Image) {
                return chart();
            }
            return super.evaluate(element, variableName);
        }
    }

    private static class R extends PDFReport {

        public R() {
            super(Application.get());
        }

        @Override
        public void generateContent() throws Exception {
            add(chart());
        }
    }
}
