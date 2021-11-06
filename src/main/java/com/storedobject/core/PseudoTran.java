package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public final class PseudoTran extends StoredObject {

    public PseudoTran() {
    }

    public static void columns(Columns columns) {
    }

    public void setLogicCode(Object value) {
    }

    public void setLogicCode(DecimalNumber logic) {
    }

    public DecimalNumber getLogicCode() {
        return new DecimalNumber();
    }

    public Logic getLogic() {
        return new Logic();
    }

    public void setNo(int no) {
    }

    public int getNo() {
        return 0;
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return DateUtility.today();
    }

    public void setApprovalCount(int approvalCount) {
    }

    public int getApprovalCount() {
        return 0;
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
        return new String[0];
    }

    public static String getStatusValue(int value) {
        return "";
    }

    public String getStatusValue() {
        return "";
    }

    public void authorize(TransactionManager tm) throws Exception {
    }

    public void delete(TransactionManager tm) throws Exception {
    }

    public List<PseudoTranDetail> buildView(TransactionManager tm) throws Exception {
        return new ArrayList<>();
    }
}