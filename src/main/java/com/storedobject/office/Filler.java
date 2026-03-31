package com.storedobject.office;

public class Filler {

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
