package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class Test extends PDFReport {

    public Test() {
        super(Application.get());
    }

    @Override
    public PDFTable getTitleTable() {
        return createTitleTable("Stock Report", "Store: Main Store", "As of today");
    }

    @Override
    public void generateContent() throws Exception {
        ObjectTable<Person> p = new ObjectTable<>(Person.class);
        p.configureAttribute("LastName", o -> "Who cares?");
        StoredObject.list(Person.class).forEach(p::addObject);
        add(p);
    }
}