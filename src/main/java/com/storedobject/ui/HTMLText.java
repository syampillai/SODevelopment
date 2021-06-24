package com.storedobject.ui;

public class HTMLText extends com.storedobject.common.HTMLText implements GridCellText {

    public HTMLText() {
    }

    public HTMLText(Object object, String... style) {
        super(object, style);
    }

    @Override
    public String getHTML() {
        return getText().toString();
    }
}
