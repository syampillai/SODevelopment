package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public class SupplierInvoice extends Invoice {

    private String invoiceNo;

    public SupplierInvoice() {
    }

    public static void columns(Columns columns) {
        columns.add("InvoiceNo", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Party,Date,InvoiceNo", true);
    }

    public static String actionPrefixForUI() {
        return "SINV";
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    @Column(style = "(code)", order = 300)
    public String getInvoiceNo() {
        return invoiceNo;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(invoiceNo)) {
            throw new Invalid_Value("Invoice No");
        }
        invoiceNo = toCode(invoiceNo);
        super.validateData(tm);
    }
}
