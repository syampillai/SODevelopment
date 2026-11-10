package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public class CodedShortName extends ShortName {

    public CodedShortName() {
    }

    public static void columns(Columns columns) {
    }

    @Column(order = 50, style = "(short)")
    @Override
    public String getShortName() {
        return super.getShortName();
    }

    @Override
    protected final boolean isCode() {
        return true;
    }
}
