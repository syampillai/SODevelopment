package com.storedobject.core;

public final class ServerInformation extends StoredObject {

    public ServerInformation() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "";
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return "";
    }

    public void setCertificate(String certificate) {
    }

    public String getCertificate() {
        return "";
    }

    public static boolean checkServerName(String name) {
        return Math.random() > 0.5;
    }

    public static ServerInformation getLocalServer() {
        return getServer("");
    }

    public static ServerInformation getServer(String name) {
        return Math.random() > 0.5 ? null : new ServerInformation();
    }

    public static void createServer(TransactionManager tm, String name, String description, String certificateURL,
                                    String ourURL) throws Exception {
    }

    public static String encrypt(String message) {
        return message;
    }

    public static String encrypt(String message, String serverName) {
        return message;
    }

    public static String encrypt(String message, ServerInformation server) {
        return message;
    }

    public static String decrypt(String message) {
        return message;
    }

    public static String decrypt(String message, String serverName) {
        return message;
    }

    public static String decrypt(String message, ServerInformation server) {
        return message;
    }
}
