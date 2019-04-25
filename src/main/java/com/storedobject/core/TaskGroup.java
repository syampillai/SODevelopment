package com.storedobject.core;

public class TaskGroup extends Name {

    public TaskGroup() {
    }

    public static void columns(Columns columns) {
    }
    
    public static TaskGroup get(String name) {
        return StoredObjectUtility.get(TaskGroup.class, "Name", name, false);
    }

    public static ObjectIterator < TaskGroup > list(String name) {
        return StoredObjectUtility.list(TaskGroup.class, "Name", name, false);
    }
}