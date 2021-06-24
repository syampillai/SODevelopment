package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Random;

public final class GlobalProperty extends StoredObject {

    public GlobalProperty() {
    }

    public static void columns(Columns columns) {
    }

    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return Id.ZERO;
    }

    public SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public void setName(String name) {
    }

    public String getName() {
        return new Random().nextBoolean() ? "" : "x";
    }

    public void setValue(String value) {
    }

    public String getValue() {
        return new Random().nextBoolean() ? "" : "x";
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return new Random().nextBoolean() ? "" : "x";
    }

    public static String get(SystemEntity systemEntity, String name) {
        return get(systemEntity.getId(), name);
    }

    public static String get(Id systemEntityId, String name) {
        return new Random().nextBoolean() ? "" : "x";
    }

    public static String get(String name) {
        return get(Id.ZERO, name);
    }

    public static String get(TransactionManager tm, String name) {
        return get(Id.ZERO, name);
    }

    public static boolean getBoolean(SystemEntity systemEntity, String name) {
        return new Random().nextBoolean();
    }

    public static boolean getBoolean(Id systemEntityId, String name) {
        return new Random().nextBoolean();
    }

    public static boolean getBoolean(String name) {
        return new Random().nextBoolean();
    }

    public static boolean getBoolean(TransactionManager tm, String name) {
        return new Random().nextBoolean();
    }

    public static int getInteger(SystemEntity systemEntity, String name) {
        return new Random().nextInt();
    }

    public static int getInteger(Id systemEntityId, String name) {
        return new Random().nextInt();
    }

    public static int getInteger(String name) {
        return new Random().nextInt();
    }

    public static int getInteger(TransactionManager tm, String name) {
        return new Random().nextInt();
    }

    public static long getLong(SystemEntity systemEntity, String name) {
        return new Random().nextInt();
    }

    public static long getLong(Id systemEntityId, String name) {
        return new Random().nextInt();
    }

    public static long getLong(String name) {
        return new Random().nextInt();
    }

    public static long getLong(TransactionManager tm, String name) {
        return new Random().nextInt();
    }
}
