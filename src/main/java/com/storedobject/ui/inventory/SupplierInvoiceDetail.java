package com.storedobject.ui.inventory;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;

public abstract class SupplierInvoiceDetail extends DataForm {

    final TextField refField = new TextField("Invoice Number");
    DateField dateField = new DateField("Invoice Date");

    public SupplierInvoiceDetail() {
        super("Supplier Invoice Details");
        refField.uppercase();
        refField.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                refField.setValue(StoredObject.toCode(refField.getValue()));
            }
        });
        addField(refField, dateField);
        refField.setHelperText("Leave it blank if not available");
    }
}
