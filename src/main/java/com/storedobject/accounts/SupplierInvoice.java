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

    public static String[] searchColumns() {
        return new String[] {
                "Date", "InvoiceNo",  "Party", "Total", "PaymentStatus", "FromInventory",
        };
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
        if(getPurchaseType() == null) {
            throw new Invalid_Value("Purchase type = " + getType() + " not identified");
        }
        super.validateData(tm);
    }

    public final PurchaseType getPurchaseType() {
        return PurchaseType.get(getType());
    }

    public static <SI extends SupplierInvoice> void checkCreateFrom(Class<SI> invoiceClass, InventoryGRN grn)
            throws Exception {
        checkCreateFrom(invoiceClass.getDeclaredConstructor().newInstance(), grn);
    }

    private static <SI extends SupplierInvoice> void checkCreateFrom(SI invoice, InventoryGRN grn)
            throws Exception {
        if(!grn.isProcessed()) {
            throw new Invalid_State("GRN is not processed yet");
        }
        if(switch (grn.getType()) {
            case 0, 3 -> false; // PO or RO
            default -> true;
        }) {
            throw new Invalid_State("Not a PO/RO");
        }
        if(grn.getMaster(SupplierInvoice.class, true) != null) {
            throw new Invalid_State("GRN already linked to a Supplier Invoice");
        }
        int type = -1;
        switch (grn.getType()) {
            case 0 -> {
                InventoryPO po = grn.listMasters(InventoryPO.class, true).findFirst();
                if (po == null) {
                    throw new Invalid_State("No PO found for this GRN");
                }
                type = po.getType();
            }
            case 3 -> type = 1001;
        }
        if(type != invoice.getType()) {
            throw new Invalid_State("Type mismatch");
        }
    }

    public static <SI extends SupplierInvoice> SI createFrom(Class<SI> invoiceClass, InventoryGRN grn,
                                                             EntityAccount partyAccount)
            throws Exception {
        Money total = grn.getTotal();
        if(partyAccount.getCurrency() != total.getCurrency()) {
            throw new Invalid_State("Currency mismatch");
        }
        SI si = invoiceClass.getDeclaredConstructor().newInstance();
        checkCreateFrom(si, grn);
        si.setFromInventory(true);
        si.setSystemEntity(grn.getSystemEntityId());
        si.setParty(partyAccount);
        si.setDate(grn.getInvoiceDate());
        si.setInvoiceNo(grn.getInvoiceNumber());
        si.setAmount(grn.getAmount());
        si.setTotal(grn.getTotal());
        if(partyAccount.isForeignCurrency()) {
            si.setExchangeRate(si.getTotal().getExchangeRate(partyAccount.getLocalCurrency(),
                    partyAccount.getSystemEntity()).reverse());
        } else {
            si.setExchangeRate(Rate.ONE);
        }
        return si;
    }
}
