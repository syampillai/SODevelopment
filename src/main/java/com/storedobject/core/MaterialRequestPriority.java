package com.storedobject.core;

import java.util.Random;

public final class MaterialRequestPriority extends Name {

    public MaterialRequestPriority() {
    }

    public static void columns(Columns columns) {
        columns.add("Priority", "int");
    }

    public static MaterialRequestPriority get(String name) {
        return new MaterialRequestPriority();
    }

    public static ObjectIterator<MaterialRequestPriority> list(String name) {
        return ObjectIterator.create();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setPriority(int priority) {
    }

    public int getPriority() {
        return new Random().nextInt();
    }
}
