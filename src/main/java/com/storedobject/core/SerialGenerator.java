package com.storedobject.core;

import java.math.BigInteger;

public final class SerialGenerator extends StoredObject {

    public SerialGenerator() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setNumericTag(int numericTag) {
    }

    public int getNumericTag() {
        return 0;
    }

    public void setStartingValue(long startingValue) {
    }

    public long getStartingValue() {
        return 0;
    }

    public static BigInteger generate(Transaction transaction, Object tag) {
        return BigInteger.ZERO;
    }

    public static long next(Object tag) {
        return 0;
    }
}
