package com.storedobject.core;

import java.util.Objects;
import java.util.function.Function;

public final class OffsetAccount extends Account {

    private static Function<SystemEntity, String> numberGenerator;

    public OffsetAccount() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity", true);
    }

    public static String[] protectedColumns() {
        return new String[] { "Name" };
    }

    public String getTitle() {
        return getSystemEntity().getName();
    }

    public static OffsetAccount get(SystemEntity systemEntity, String name) {
        return OffsetAccount.getByNameOrNumber(systemEntity, OffsetAccount.class, name, true);
    }

    public static ObjectIterator<? extends OffsetAccount> list(SystemEntity systemEntity, String name) {
        return OffsetAccount.listByNameOrNumber(systemEntity, OffsetAccount.class, name, true);
    }

    public static OffsetAccount get(TransactionManager tm) {
        return get(Objects.requireNonNull(tm.getEntity()));
    }

    public static OffsetAccount get(SystemEntity forEntity) {
        return get(OffsetAccount.class, "SystemEntity=" + forEntity.getId());
    }

    public static OffsetAccount create(TransactionManager tm) throws Exception {
        return create(tm, tm.getEntity());
    }

    public static OffsetAccount create(TransactionManager tm, SystemEntity forEntity) throws Exception {
        OffsetAccount oa = get(forEntity);
        if(oa != null) {
            return oa;
        }
        oa = new OffsetAccount();
        oa.setSystemEntity(forEntity);
        oa.setNumber(numberGenerator.apply(tm.getEntity()));
        return oa;
    }

    public static void setNumberGenerator(Function<SystemEntity, String> numberGenerator) {
        OffsetAccount.numberGenerator = numberGenerator;
    }
}
