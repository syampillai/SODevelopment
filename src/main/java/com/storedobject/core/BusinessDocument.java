package com.storedobject.core;

public class BusinessDocument extends Document<Entity> {

    public BusinessDocument() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    protected Class<Entity> getOwnerClass() {
        return Entity.class;
    }
}
