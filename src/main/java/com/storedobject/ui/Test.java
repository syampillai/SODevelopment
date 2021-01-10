package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFRectangle;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class Test extends PDFReport {

    public Test() {
        super(Application.get());
    }

    @Override
    public void generateContent() throws Exception {
        setDefaultCellCustomizer(c -> {
            c.setBorderWidth(1);
            c.setBorder(PDFRectangle.TOP | PDFRectangle.BOTTOM);
        });
        PDFTable table = createTable(10, 10, 5, 10);
        for(Person p: StoredObject.list(Person.class)) {
            table.addCell(createCell(p.getFirstName()));
            table.addCell(createCell(p.getLastName()));
            table.addCell(createCell(p.getAge()));
            table.addCell(createCell(p.getDateOfBirth()));
        }
        addTable(table);
    }

    @Override
    public int getPageOrientation() {
        return ORIENTATION_LANDSCAPE;
    }
}
