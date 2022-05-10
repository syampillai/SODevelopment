package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.sql.Date;

public final class Consignment extends StoredObject {

    private final static String[] typeValues = new String[] {
            "Purchase Return", "Repair Order"
    };
    private final Date date = DateUtility.today();
    private int no = 0, type = 0;

    public Consignment() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "int");
        columns.add("No", "int");
        columns.add("Date", "date");
    }

    public static String[] links() {
        return new String[]{
                "Packets|com.storedobject.core.ConsignmentPacket|Number||0",
                "Items|com.storedobject.core.ConsignmentItem|||0",
        };
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

    @SetNotAllowed
    @Column(order = 600)
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

    @SetNotAllowed
    @Column(style = "(serial)", order = 10)
    public int getNo() {
        if (no == 0) {
            no = SerialGenerator.generate(getTransaction(), "CONSIGNMENT-" + type).intValue();
        }
        return no;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Column(order = 20)
    public Date getDate() {
        return new Date(date.getTime());
    }
}
