package com.storedobject.platform;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static List<String> listHostNames() {
        return new ArrayList<>();
    }

    public static List<Application> list(String value) {
        return new ArrayList<>();
    }

    public boolean isRunning() {
        return false;
    }

    public String getName() {
        return null;
    }

    public void start() throws Exception {
    }

    public void reload() throws Exception {
    }

    public void stop() {
    }
}
