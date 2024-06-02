package com.storedobject.mail;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

public class Error extends StoredObject implements Detail {

    private String error;

    public Error() {
    }

    Error(String error) {
        this.error = error;
    }

    Error(Sender sender, Mail mail, Throwable error) {
        this.error = mail.getSenderGroup().getName() + "/" + sender.getFromAddress() + "\n"
                + SORuntimeException.getTrace(error, true);
    }

    public static void columns(Columns columns) {
        columns.add("Error", "text");
    }

    public void setError(String error) {
        this.error = error;
    }

    @Column(style = "(large)")
    public String getError() {
        return error;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(error)) {
            throw new Invalid_Value("Error");
        }
        super.validateData(tm);
    }

    @Override
	public Id getUniqueId() {
        return getId();
    }

    @Override
	public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return masterClass == Sender.class || masterClass == Mail.class;
    }
}
