package com.storedobject.core;


import com.storedobject.common.StringList;

public class AdditionalDocumentValueDefinition extends StoredObject implements Detail {

    public AdditionalDocumentValueDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setValueType(int valueType) {
    }

    public int getValueType() {
        return 0;
    }

    public static String[] getValueTypeValues() {
        return null;
    }

    public static String getValueTypeValue(int value) {
        return null;
    }

    public String getValueTypeValue() {
        return null;
    }

    public void setParameter(String parameter) {
    }

    public String getParameter() {
        return null;
    }
    
    public void setEmptyAllowed(boolean emptyAllowed) {
    }

    public boolean getEmptyAllowed() {
        return false;
    }

    public void setCaption(String caption) {
    }

    public String getCaption() {
        return null;
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }

    public Id getUniqueId() {
        return null;
    }

    public void copyValuesFrom(Detail detail) {
    }

    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
    
    public StringList getChoiceValues() {
        return null;
    }
    
    public StringList getChoiceBitValues() {
        return null;
    }
}