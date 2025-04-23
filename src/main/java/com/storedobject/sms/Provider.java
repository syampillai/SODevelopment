package com.storedobject.sms;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

public class Provider extends StoredObject {

    private String name;
    private String senderID;
    private String userName;
    private String password;
    private String baseURL;
    private String technicalRemarks;
    private String generalRemarks;

    public Provider() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("SenderID", "text");
        columns.add("UserName", "text");
        columns.add("Password", "text");
        columns.add("BaseURL", "text");
        columns.add("TechnicalRemarks", "text");
        columns.add("GeneralRemarks", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    @Override
	public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static Provider get(String name) {
        return StoredObjectUtility.get(Provider.class, "Name", name, false);
    }

    public static ObjectIterator < Provider > list(String name) {
        return StoredObjectUtility.list(Provider.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(style = "(code)", order = 1)
    public String getName() {
        return name;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    @Column(required = false, caption = "Sender ID", order = 2)
    public String getSenderID() {
        return senderID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(required = false, order = 3)
    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(required = false, order = 4)
    public String getPassword() {
        return password;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    @Column(required = false, caption = "Base URL", order = 5)
    public String getBaseURL() {
        return baseURL;
    }

    public void setTechnicalRemarks(String technicalRemarks) {
        this.technicalRemarks = technicalRemarks;
    }

    @Column(style = "(large)", required = false, order = 6)
    public String getTechnicalRemarks() {
        return technicalRemarks;
    }

    public void setGeneralRemarks(String generalRemarks) {
        this.generalRemarks = generalRemarks;
    }

    @Column(style = "(large)", order = 7)
    public String getGeneralRemarks() {
        return generalRemarks;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        name = toCode(name).replace('-', '_');
        if(StringUtility.isWhite(generalRemarks)) {
            throw new Invalid_Value("General Remarks");
        }
        super.validateData(tm);
    }
    
    @Override
	public String toString() {
    	return name;
    }
}
