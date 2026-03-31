package com.storedobject.office;

import com.storedobject.core.TransactionManager;

public class Filler {

    public final TransactionManager getTransactionManager() {
        return Math.random() > 0.5 ? null : new TransactionManager(null, null);
    }

    public Object evaluate(ODT.Element element, String variableName) {
        if(element instanceof ODT.Image) return null;
        return variableName + " = ?";
    }

    public void customizeSection(ODT.Section section) {
    }

    public void customizeTable(ODT.Table table) {
    }

    public void customizeImage(ODT.Image image) {
    }
}
