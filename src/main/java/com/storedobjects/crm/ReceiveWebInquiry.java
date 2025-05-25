package com.storedobjects.crm;

import com.storedobject.common.StringList;
import com.storedobject.core.ParameterService;
import com.storedobject.core.TransactionManager;
import com.storedobject.job.MessageGroup;

import java.util.Map;

public class ReceiveWebInquiry implements ParameterService {

    private TransactionManager tm;

    @Override
    public String getName() {
        return "Receive Web Inquiry";
    }

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
        this.tm = transactionManager;
    }

    @Override
    public StringList getMandatoryParameters() {
        return StringList.create("fullName", "email", "country", "contactNumber", "companyName", "message");
    }

    @Override
    public String serve(Map<String, String[]> parameters) {
        Inquiry inquiry = new Inquiry();
        inquiry.setFullName(parameters.get("fullName")[0]);
        inquiry.setEmail(parameters.get("email")[0]);
        inquiry.setCountry(parameters.get("country")[0]);
        inquiry.setPhone(parameters.get("contactNumber")[0]);
        inquiry.setCompanyName(parameters.get("companyName")[0]);
        inquiry.setMessage(parameters.get("message")[0]);
        try {
            tm.transact(inquiry::save);
        } catch (Exception ignored) {
        }
        MessageGroup.notify("SALES-INQUIRY", tm,"Inquiry received from "
                + inquiry.getFullName() + " (" + inquiry.getEmail() + ", " + inquiry.getCountry()
                + inquiry.getPhone() + "), Message: " + inquiry.getMessage());
        return "OK";
    }
}
