package com.storedobject.core;

import com.storedobject.core.annotation.*;

public final class DocumentType extends Name {

    private static final String[] typeBitValues =
            new String[] {
                    "Personal", "Business", "Account Related", "Others",
            };
    private static final String[] contentTypeBitValues =
            new String[] {
                    "Image", "PDF", "Audio", "Video", "Other",
            };
    private boolean expiryApplicable;
    private boolean issued;
    private int type = 0;
    private int contentType = 0;

    public DocumentType() {
    }

    public static void columns(Columns columns) {
        columns.add("ExpiryApplicable", "boolean");
        columns.add("Issued", "boolean");
    }

    public static DocumentType get(String name) {
        return StoredObjectUtility.get(DocumentType.class, "Name", name, false);
    }

    public static ObjectIterator<DocumentType> list(String name) {
        return StoredObjectUtility.list(DocumentType.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setExpiryApplicable(boolean expiryApplicable) {
        if (!loading()) {
            throw new Set_Not_Allowed("Expiry Applicable");
        }
        this.expiryApplicable = expiryApplicable;
    }

    @SetNotAllowed
    @Column(order = 200)
    public boolean getExpiryApplicable() {
        return expiryApplicable;
    }

    public void setIssued(boolean issued) {
        if (!loading()) {
            throw new Set_Not_Allowed("Issued");
        }
        this.issued = issued;
    }

    @SetNotAllowed
    @Column(caption = "Issued by an Authority", order = 300)
    public boolean getIssued() {
        return issued;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(order = 400)
    public int getType() {
        return type;
    }

    public static String[] getTypeBitValues() {
        return typeBitValues;
    }

    public static String getTypeValue(int value) {
        String[] s = getTypeBitValues();
        return StringUtility.bitsValue(value, s);
    }

    public String getTypeValue() {
        return getTypeValue(type);
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    @Column(order = 500)
    public int getContentType() {
        return contentType;
    }

    public static String[] getContentTypeBitValues() {
        return contentTypeBitValues;
    }

    public static String getContentTypeValue(int value) {
        String[] s = getContentTypeBitValues();
        return StringUtility.bitsValue(value, s);
    }

    public String getContentTypeValue() {
        return getContentTypeValue(contentType);
    }
}
