package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.RateField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;

import java.util.Currency;

public abstract class SupplierInvoiceDetail extends DataForm {

    final TextField refField = new TextField("Invoice Number");
    DateField dateField = new DateField("Invoice Date");
    RateField rateField = new RateField("Exchange Rate");

    public SupplierInvoiceDetail(TransactionManager tm, Currency currency) {
        super("Supplier Invoice Details");
        refField.uppercase();
        refField.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                refField.setValue(StoredObject.toCode(refField.getValue()));
            }
        });
        addField(refField, dateField, rateField);
        refField.setHelperText("Leave it blank if not available");
        set(tm, currency);
    }

    public void set(TransactionManager tm, Currency currency) {
        Currency localCurrency = tm.getCurrency();
        if(localCurrency == currency) {
            setFieldHidden(rateField);
        } else {
            setFieldVisible(rateField);
            rateField.setHelperText(currency.getCurrencyCode() + " to " + localCurrency.getCurrencyCode());
            rateField.setValue(Money.getBuyingRate(DateUtility.today(), currency, tm.getEntity()));
        }
    }
}
