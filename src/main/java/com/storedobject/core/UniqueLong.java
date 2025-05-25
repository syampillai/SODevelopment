package com.storedobject.core;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueLong {

    private final AtomicLong unique = new AtomicLong(0);

    private UniqueLong() {
    }

    public static long get() {
        return new UniqueLong().unique.incrementAndGet();
    }
}
