package com.storedobject.core;

import java.math.BigDecimal;

public class AdditionalDocumentValue extends StoredObject implements Detail {

    public AdditionalDocumentValue() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(Id nameId) {
    }

    public void setName(BigDecimal idValue) {
    }

    public void setName(AdditionalDocumentValueDefinition name) {
    }

    public Id getNameId() {
        return null;
    }

    public AdditionalDocumentValueDefinition getName() {
        return null;
    }

    public void setValue(String value) {
    }

    public String getValue() {
        return null;
    }

    public Id getUniqueId() {
        return null;
    }

    public void copyValuesFrom(Detail detail) {
    }

    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }

    public Object getObject() {
    	return null;
    }
    
    public Object getObjectValue() {
    	return null;
    }
}