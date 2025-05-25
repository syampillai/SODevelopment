package com.storedobject.ui.crm;

import com.storedobject.core.DateUtility;
import com.storedobject.core.EditorAction;
import com.storedobject.ui.ObjectBrowser;
import com.storedobjects.crm.Inquiry;

import java.sql.Timestamp;

public class InquiryBrowser extends ObjectBrowser<Inquiry> {

    public InquiryBrowser() {
        super(Inquiry.class, EditorAction.ALL & ~EditorAction.DELETE);
    }

    public InquiryBrowser(String className) {
        this();
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("ReceivedAt".equals(columnName)) {
            return "Received at (" + getTransactionManager().getEntity().getTimeZone() + ")";
        }
        return super.getColumnCaption(columnName);
    }

    public String getReceivedAt(Inquiry inquiry) {
        Timestamp receivedAt = inquiry.getReceivedAt();
        return DateUtility.formatWithTimeHHMM(getTransactionManager().date(receivedAt));
    }
}
