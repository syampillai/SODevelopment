package com.storedobject.core;

/**
 * Strictly for internal use only.
 *
 * @author Syam
 */
public final class ObjectId extends Id {

    ObjectId(PseudoTransaction transaction, StoredObject object) throws Exception {
        this(transaction, object, (byte)0);
    }

    ObjectId(PseudoTransaction transaction, StoredObject object, byte flag) throws Exception {
        super();
    }
}