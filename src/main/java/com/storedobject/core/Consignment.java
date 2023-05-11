package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.math.BigDecimal;
import java.sql.Date;

public final class Consignment extends StoredObject {

    private final static String[] typeValues = new String[] {
            "Purchase Return", "Repair Order"
    };
    private final Date date = DateUtility.today();
    private int no = 0, type = 0;
    private String portOfLoading = "", portOfDischarge = "", remark = "", airwayBillNumber = "";
    private Id buyerId = Id.ZERO;

    public Consignment() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] getTypeValues() {
        return typeValues;
    }

    public void setType(int type) {
        if(!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        if(type < 0 || type >= typeValues.length) {
            throw new SORuntimeException();
        }
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getTypeValue() {
        return typeValues[type % typeValues.length];
    }

    public void setNo(int no) {
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setBuyer(Id buyerId) {
        this.buyerId = buyerId;
    }

    public void setBuyer(BigDecimal idValue) {
        setBuyer(new Id(idValue));
    }

    public void setBuyer(Entity buyer) {
        setBuyer(buyer == null ? Id.ZERO : buyer.getId());
    }

    public Id getBuyerId() {
        return buyerId;
    }

    public Entity getBuyer() {
        return get(Entity.class, buyerId);
    }

    public void setAirwayBillNumber(String airwayBillNumber) {
        this.airwayBillNumber = airwayBillNumber;
    }

    public String getAirwayBillNumber() {
        return airwayBillNumber;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public String getReference() {
        return String.valueOf(getNo());
    }
}
