package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.office.Filler;
import com.storedobject.office.ODT;
import com.storedobject.office.ODTReport;
import com.storedobject.vaadin.*;

public class Test extends DataForm {

    private final BooleanField raw = new BooleanField("Raw");

    public Test() {
        super("Test");
        addField(raw);
    }

    @Override
    protected boolean process() {
        close();
        ODTReport r = new ODTReport(getApplication(), new Id("3464"));
        r.setFiller(new F());
        r.setRawOutput(raw.getValue());
        r.execute();
        return true;
    }

    public static class F extends Filler {

        @Override
        public void customizeTable(ODT.Table table) {
            System.err.println("Table: " + table.getName());
        }

        @Override
        public void customizeSection(ODT.Section section) {
            System.err.println("Section: " + section.getName());
        }
    }
}
