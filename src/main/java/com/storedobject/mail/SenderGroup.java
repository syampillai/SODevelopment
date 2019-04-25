package com.storedobject.mail;

import com.storedobject.core.Columns;
import com.storedobject.core.StoredObject;
import com.storedobject.core.annotation.Column;

public class SenderGroup extends StoredObject {

    public SenderGroup() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    @Column(order = 1)
    public String getName() {
        return null;
    }

    public void setAlert(boolean alert) {
    }

    @Column(order = 2)
    public boolean getAlert() {
        return false;
    }
}
