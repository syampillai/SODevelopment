package com.storedobject.ui;

import com.storedobject.core.Device;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.office.CSVReport;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Test");
    }

    @Override
    protected boolean process() {
        //noinspection resource
        new Download(getApplication()).execute();
        return false;
    }

    private static class Download extends CSVReport {

        public Download(Device device) {
            super(device, 4);
        }

        @Override
        public void generateContent() throws Exception {
            for(Person p: StoredObject.list(Person.class)) {
                setValues(p.getName(), p.getAge(), p.getDateOfBirth(), p.getGenderValue());
                writeRow();
            }
        }

        @Override
        public String getFileName() {
            return "test-test-test";
        }
    }
}