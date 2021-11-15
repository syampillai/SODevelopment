package com.storedobject.ui;

import com.storedobject.common.Barcode;
import com.storedobject.core.*;
import com.storedobject.pdf.*;
import com.storedobject.vaadin.DataForm;

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