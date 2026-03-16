package com.storedobject.ui;

import com.storedobject.core.*;
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
        R r = new R(getApplication());
        r.setRawOutput(raw.getValue());
        r.execute();
        return true;
    }

    public static class R extends ODTReport {

        public R(Device device) {
            super(device);
            setTemplate(new Id("3408"));
        }

        public Object fill(String name) {
            return name;
        }

        public Object fill(String name, int i) {
            return name + "[" + i + "]";
        }

        public int rowCount(String table) {
            return "Cargolist".equals(table) ? 2 : 0;
        }

        public int rowStart(String table) {
            return "Estimatelist".equals(table) ? 2 : -1;
        }

        @Override
        protected boolean isCustomTable(String tableName) {
            return "Estimatelist".equals(tableName);
        }

        @Override
        protected void customize(Table table) throws Exception {
            table.copy(0);
            TableRow row;
            row = table.getRow(1);
            table.add(row, 0);
            row = table.getRow(2);
            table.add(row, 10);
            row = table.getRow(2);
            table.add(row, 11);
            table.copy(3, 2);
        }
    }
}
