package com.storedobject.job;

import com.storedobject.tools.SystemDevice;

public final class Scheduler extends SystemDevice {

    private Scheduler(String link) {
        super(link);
    }

    public static String getStatus() {
        return "";
    }

    public static String getStatusValue() {
        return "";
    }

    public static int getStatusCode() {
        return 0;
    }

    public static void restart() {
    }

    public static void kill() {
    }

    @Override
    public String getDeviceType() {
        return "scheduler";
    }

    @Override
    public void close() {
    }

    @Override
    protected String getIdentifierTag() {
        return "SO Scheduler";
    }

    @Override
    protected String getPackageName() {
        return "com.storedobject.job";
    }
}
