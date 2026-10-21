package com.storedobject.ui;

import com.storedobject.svg.chart.Bars;
import com.storedobject.svg.chart.Chart;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private final RichTextEditor r = new RichTextEditor("Rich Text");

    public Test() {
        super("Test");
        BooleanField raw = new BooleanField("Raw");
        addField(raw);
        addField(new TemperatureField("Temperature"));
        r.setValue("<h1>Hello World!</h1>");
        addField(r);
        //add(new Svg(chart()));
    }

    @Override
    protected boolean process() {
        message(r.getValue());
        if(!r.isReadOnly()) {
            r.setReadOnly(true);
            message("RO: " + r.isReadOnly());
            return false;
        }
        close();
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
}
