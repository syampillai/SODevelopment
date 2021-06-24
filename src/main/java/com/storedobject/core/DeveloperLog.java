package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DeveloperLog extends StoredObject {

    public DeveloperLog() {
    }

    public static void columns(Columns columns) {
    }

    public void setDeveloper(Id developerId) {
    }

    public void setDeveloper(BigDecimal idValue) {
    }

    public void setDeveloper(Person developer) {
    }

    public Id getDeveloperId() {
        return null;
    }

    public Person getDeveloper() {
        return null;
    }

    public void setSourceCode(Id sourceCodeId) {
    }

    public void setSourceCode(BigDecimal idValue) {
    }

    public void setSourceCode(JavaClass sourceCode) {
    }

    public Id getSourceCodeId() {
        return null;
    }

    public JavaClass getSourceCode() {
        return null;
    }

    public void setVersion(int version) {
    }

    public int getVersion() {
        return 0;
    }

    public void setAction(int action) {
    }

    public int getAction() {
        return 0;
    }

    public static String[] getActionValues() {
        return null;
    }

    public static String getActionValue(int value) {
        return null;
    }

    public String getActionValue() {
        return null;
    }

    public void setActionedAt(Timestamp actionedAt) {
    }

    public Timestamp getActionedAt() {
        return null;
    }
}
