package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TimeDuration;
import com.storedobject.svg.chart.Bars;
import com.storedobject.svg.chart.Chart;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.Svg;

import java.util.concurrent.TimeUnit;

public class Test extends DataForm {

    private final ContactField cf = new ContactField("Contact");

    public Test() {
        super("Test");
        BooleanField raw = new BooleanField("Raw");
        addField(raw);
        addField(new TimeDurationField("Time Duration"));
        TimeDurationField x = new TimeDurationField("Another Time Duration");
        addField(x);
        x.setValue(new TimeDuration(100, TimeUnit.SECONDS));
        setFieldReadOnly(x);
        addField(new TemperatureField("Temperature"));
        addField(cf);
        add(new Svg(chart()));
        cf.setMaster(StoredObject.get(Person.class, "FirstName='Collinz'"));
    }

    @Override
    protected boolean process() {
        message(cf.getObject());
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
