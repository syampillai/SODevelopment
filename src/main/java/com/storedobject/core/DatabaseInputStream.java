package com.storedobject.core;

public class DatabaseInputStream extends java.io.ByteArrayInputStream {

    public static int BUFFER_SIZE;

    public DatabaseInputStream(Id id) {
        this(id, null, false);
    }

    DatabaseInputStream(StreamData sd) {
        this(sd.getId(), sd.old() ? sd.getTransactionId() : null, false);
    }

    DatabaseInputStream(Id id, Id tranId, boolean master) {
        this();
    }

    private DatabaseInputStream() {
        super((byte[]) null);
    }

    public void close() throws java.io.IOException {
    }

    public int read() {
        return 0;
    }

    public int read(byte[] p1, int p2, int p3) {
        return 0;
    }

    public int available() {
        return 0;
    }

    public long skip(long p1) {
        return 0;
    }

    public boolean markSupported() {
        return false;
    }
}
