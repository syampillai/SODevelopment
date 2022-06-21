package com.storedobject.core;

public class ServerLink extends StoredObject implements Detail {

    public ServerLink() {
    }

    public static void columns(Columns columns) {
    }

    public void setFromLink(String fromLink) {
    }

    public String getFromLink() {
        return "";
    }

    public void setToLink(String toLink) {
    }

    public String getToLink() {
        return "";
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == ServerInformation.class;
    }

    public static String trim(String link) {
        return link;
    }
}
