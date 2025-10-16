package com.storedobject.core;

import java.util.function.Function;

public enum ActionType {

    NOUN, VERB_PRESENT, VERB_PAST, VERB_PAST_PARTICIPLE;

    private static<T extends InventoryTransfer>  String get(Class<T> transferClass, Function<T, String> function) {
        T transfer = null;
        try {
            transfer = transferClass.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {
        }
        return transfer == null ? "Unknown" : function.apply(transfer);
    }

    public static String getDescription(ActionType actionType, Class<? extends InventoryTransfer> transferClass) {
        return get(transferClass, transfer -> transfer.getActionDescription(actionType));
    }

    public static String getFromLocationName(Class<? extends InventoryTransfer> transferClass) {
        return get(transferClass, InventoryTransfer::getFromLocationName);
    }

    public static String getToLocationName(Class<? extends InventoryTransfer> transferClass) {
        return get(transferClass, InventoryTransfer::getToLocationName);
    }
}
