package com.storedobject.telegram;

import com.storedobject.core.*;

public final class Telegram extends Message {

    public Telegram() {
    }

    public Telegram(long telegramNumber, String message) {
        super(message);
    }

    public static void columns(Columns columns) {
    }

    public void setTelegramNumber(long telegramNumber) {
    }

    public long getTelegramNumber() {
        return 0L;
    }

    public void setErrorValue(String errorValue) {
    }

    public static String[] getErrorValues() {
        return new String[] {};
    }

    public static String getErrorValue(int value) {
        String[] s = getErrorValues();
        return s[value % s.length];
    }

    public String getErrorValue() {
        return getErrorValue(0);
    }
}
