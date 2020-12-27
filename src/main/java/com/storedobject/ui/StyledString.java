package com.storedobject.ui;

public class StyledString extends com.storedobject.common.StyledString implements GridCellText {

    @Override
    public String getHTML() {
        return toString();
    }
}