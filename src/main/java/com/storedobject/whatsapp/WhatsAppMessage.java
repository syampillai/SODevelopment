package com.storedobject.whatsapp;

import com.storedobject.core.Columns;
import com.storedobject.sms.SMSMessage;

public class WhatsAppMessage extends SMSMessage {

    public WhatsAppMessage() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    protected int getMaxLength() {
        return 3900;
    }
}
