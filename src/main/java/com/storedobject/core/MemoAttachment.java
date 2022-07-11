package com.storedobject.core;

public final class MemoAttachment extends FileData {

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MemoComment.class;
    }

    public static void columns(Columns columns) {
    }
}
