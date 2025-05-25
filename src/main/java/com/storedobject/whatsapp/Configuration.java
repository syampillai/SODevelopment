package com.storedobject.whatsapp;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public class Configuration extends StoredObject {

    private String uRL;
    private String token;

    public Configuration() {}

    public static void columns(Columns columns) {
        columns.add("URL", "text");
        columns.add("Token", "text");
    }

    public static void indices(Indices indices) {
        indices.add("T_Family", true);
    }

    public String[] browseColumns() {
        return new String[] { "URL" };
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    @Column(caption = "Connection URL", order = 100)
    public String getURL() {
        return uRL;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(style = "(hidden)", order = 200)
    public String getToken() {
        return token;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(uRL)) {
            throw new Invalid_Value("Connection URL");
        }
        if (StringUtility.isWhite(token)) {
            throw new Invalid_Value("Token");
        }
        super.validateData(tm);
    }
}
