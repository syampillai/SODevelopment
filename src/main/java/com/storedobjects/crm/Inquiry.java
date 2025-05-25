package com.storedobjects.crm;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class Inquiry extends StoredObject {

    private static final String[] sourceValues =
            new String[] {
                    "Web site",
                    "Phone",
                    "Email",
                    "Sales call",
                    "Linkedin",
                    "Facebook",
                    "Instagram",
                    "Reference",
            };
    private static final String[] statusValues =
            new String[] {
                    "Received", "Assigned to", "Responded", "Following up", "Pending", "Converted", "Lost",
            };
    private final Timestamp receivedAt = DateUtility.now();
    private int source = 0;
    private String fullName;
    private String email;
    private String country;
    private String phone;
    private String companyName;
    private String message;
    private int status = 0;
    private Id assignedToId = Id.ZERO;
    private String remark;

    public Inquiry() {}

    public static void columns(Columns columns) {
        columns.add("ReceivedAt", "timestamp");
        columns.add("Source", "int");
        columns.add("FullName", "text");
        columns.add("Email", "text");
        columns.add("Country", "text");
        columns.add("Phone", "text");
        columns.add("CompanyName", "text");
        columns.add("Message", "text");
        columns.add("Status", "int");
        columns.add("AssignedTo", "id");
        columns.add("Remark", "text");
    }

    public static String[] searchColumns() {
        return new String[] {
                "FullName",
                "email",
                "Country",
                "CompanyName",
                "Status",
                "AssignedTo",
        };
    }

    public void setReceivedAt(Timestamp receivedAt) {
        if (!loading()) {
            throw new Set_Not_Allowed("Received at");
        }
        this.receivedAt.setTime(receivedAt.getTime());
        this.receivedAt.setNanos(receivedAt.getNanos());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Timestamp getReceivedAt() {
        return new Timestamp(receivedAt.getTime());
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Column(order = 200)
    public int getSource() {
        return source;
    }

    public static String[] getSourceValues() {
        return sourceValues;
    }

    public static String getSourceValue(int value) {
        String[] s = getSourceValues();
        return s[value % s.length];
    }

    public String getSourceValue() {
        return getSourceValue(source);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(required = false, order = 300)
    public String getFullName() {
        return fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(required = false, order = 400)
    public String getEmail() {
        return email;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(required = false, order = 500)
    public String getCountry() {
        return country;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(required = false, order = 700)
    public String getPhone() {
        return phone;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(required = false, order = 800)
    public String getCompanyName() {
        return companyName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(style = "(large)", required = false, order = 900)
    public String getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(order = 1000)
    public int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return getStatusValue(status);
    }

    public void setAssignedTo(Id assignedToId) {
        this.assignedToId = assignedToId;
    }

    public void setAssignedTo(BigDecimal idValue) {
        setAssignedTo(new Id(idValue));
    }

    public void setAssignedTo(SystemUser assignedTo) {
        setAssignedTo(assignedTo == null ? null : assignedTo.getId());
    }

    @Column(required = false, order = 1100)
    public Id getAssignedToId() {
        return assignedToId;
    }

    public SystemUser getAssignedTo() {
        return getRelated(SystemUser.class, assignedToId);
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(style = "(large)", required = false, order = 1200)
    public String getRemark() {
        return remark;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        assignedToId = tm.checkType(this, assignedToId, SystemUser.class, true);
        super.validateData(tm);
    }
}
