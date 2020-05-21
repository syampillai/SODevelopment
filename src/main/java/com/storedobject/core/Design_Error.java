package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

public class Design_Error extends SORuntimeException {

    public Design_Error(Transaction transaction) {
    }

    public Design_Error(Transaction transaction, String message) {
    }

    public Design_Error(Transaction transaction, StoredObject so) {
    }

    public Design_Error(Transaction transaction, Class<?> soClass) {
    }

    public Design_Error(Transaction transaction, StoredObject so, String message) {
    }

    public Design_Error(Transaction transaction, Class<?> soClass, String message) {
    }
}
