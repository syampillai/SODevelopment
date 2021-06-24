package com.storedobject.core;

import java.sql.Date;
import java.util.Locale;

public interface Device {

    void setServer(ApplicationServer server);
    ApplicationServer getServer();
    String getDeviceType();
    int getDeviceHeight();
    int getDeviceWidth();
    String getIPAddress();
    String getIdentifier();
    String getDriverIdentifier();
    int getMajorVersion();
    int getMinorVersion();
    void close();
    DeviceLayout getDeviceLayout();
    void setDeviceLayout(DeviceLayout layout);
    void setLocale(Locale locale);
    boolean loggedin(Login login);

    default void view(String caption, StreamData streamData) {
        view(caption, new StreamDataContent(streamData, caption));
    }

    default void view(StreamData streamData) {
        view(null, streamData);
    }

    default void view(FileData fileData) {
        view(fileData.getName(), fileData.getFile());
    }

    void view(String caption, ContentProducer producer);

    default void view(ContentProducer producer) {
        view(null, producer);
    }

    default void download(StreamData streamData) {
        download(null, streamData);
    }

    default void download(String fileName, StreamData streamData) {
        download(new StreamDataContent(streamData, fileName));
    }

    default void download(ContentProducer producer) {
        view(producer);
    }

    default void alert(LoginMessage message) {
        alert(message.getMessage());
    }

    default void alert(String alert) {
        alert(null, alert);
    }

    default void alert(String caption, String alert) {
        showNotification(caption, alert);
    }

    default void log(Object anything) {
        ApplicationServer.log(this, anything);
    }

    default void log(Object anything, Throwable error) {
        ApplicationServer.log(this, anything, error);
    }

    void showNotification(String text);
    void showNotification(String caption, String text);
    void showNotification(Throwable error);
    void showNotification(String caption, Throwable error);
    String getDevicePackageTag();
    MessageViewer getMessageViewer();

    default void setDate(Date date) {
    }
}
