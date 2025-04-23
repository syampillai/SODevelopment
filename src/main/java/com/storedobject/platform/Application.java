package com.storedobject.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Application {

    public static List<String> listHostNames() {
        return new ArrayList<>();
    }

    public static List<Application> list(String value) {
        return new ArrayList<>();
    }

    public static Application get(String name, String hostName) {
        return null;
    }

    public void setOwner(Function<String, String> owner) {
    }

    public boolean isRunning() {
        return false;
    }

    public String getName() {
        return null;
    }

    public void start() throws Exception {
    }

    public String  reload(String by) throws Exception {
        return null;
    }

    public String stop(String  by) {
        return null;
    }
}
