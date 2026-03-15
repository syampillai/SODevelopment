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

    private static class R extends ODTReport {

        public R(Device device) {
            super(device);
            setTemplate(new Id("3405"));
        }

        @Override
        protected boolean includeSection(String sectionName) {
            return !"Section2".equals(sectionName);
        }
    }
}
