package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

public class CustomerInvoice extends Invoice implements HasReference {

    private static final ReferencePattern<CustomerInvoice> ref = new ReferencePattern<>();
    private int no;
    private String reference;

    public CustomerInvoice() {
    }

    public static void columns(Columns columns) {
        columns.add("No", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Party,Date,No");
        indices.add("Date,No", true);
    }

    public static String actionPrefixForUI() {
        return "CINV";
    }

    public void setNo(int no) {
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    @SetNotAllowed
    @Column(style = "(serial)", order = 300)
    public int getNo() {
        if(no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    @Override
    public final String getInvoiceNo() {
        return getReference();
    }

    @Override
    public final String getReference() {
        if (reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    @Override
    public String getTagPrefix() {
        return "INV-";
    }

    @Override
    protected void postTax(JournalVoucher journalVoucher) throws Exception {
        //throw new SOException("Posting Tax - Not yet implemented");
    }

    public static String[] serialTypes() {
        return new String[]{
                "Organization",
                "Invoice Type",
                "Organization + Invoice Type",
        };
    }

    @Override
    public String serialTag(int index) {
        return switch(index) {
            case 2 -> getSystemEntityId() + "-" + getType();
            case 1 -> String.valueOf(getType());
            default -> String.valueOf(getSystemEntityId());
        };
    }
}
