package com.storedobject.sms;

import com.storedobject.core.*;
import java.math.BigDecimal;

public class CreditMonitor extends StoredObject {

    public CreditMonitor() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
    }

    public String getUniqueCondition() {
        return null;
    }

    public void setProviderName(String providerName) {
    }

    public String getProviderName() {
        return null;
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
    }

    public void setPerson(com.storedobject.core.Person person) {
    }

    public Id getPersonId() {
        return null;
    }

    public com.storedobject.core.Person getPerson() {
        return null;
    }

    public void setContactType(Id contactTypeId) {
    }

    public void setContactType(BigDecimal idValue) {
    }

    public void setContactType(com.storedobject.core.ContactType contactType) {
    }

    public Id getContactTypeId() {
        return null;
    }

    public com.storedobject.core.ContactType getContactType() {
        return null;
    }

    public void setThreshold(int threshold) {
    }

    public int getThreshold() {
        return 0;
    }

    public void setMessageTemplate(Id messageTemplateId) {
    }

    public void setMessageTemplate(BigDecimal idValue) {
    }

    public void setMessageTemplate(com.storedobject.core.MessageTemplate messageTemplate) {
    }

    public Id getMessageTemplateId() {
        return null;
    }

    public com.storedobject.core.MessageTemplate getMessageTemplate() {
        return null;
    }
}
