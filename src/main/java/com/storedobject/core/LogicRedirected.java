package com.storedobject.core;

import com.storedobject.common.Executable;

public final class LogicRedirected extends RuntimeException {

    public LogicRedirected(Executable executable) {
    }

    public Executable getExecutable() {
        return () -> { };
    }
}
