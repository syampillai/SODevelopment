package com.storedobject.ui;

import com.storedobject.core.Signature;
import com.storedobject.pdf.PDFReport;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements Transactional {

    public Test() {
        super("Signature");
        Image image = new Image(Signature.get(getTransactionManager().getUser().getPersonId()));
        add(image);
    }

    @Override
    protected boolean process() {
        close();
        PDFReport r = new PDFReport(getApplication()) {
            @Override
            public void generateContent() {
                add(createImage(Signature.get(getTransactionManager().getUser().getPersonId())));
            }
        };
        r.execute();
        return true;
    }
}
