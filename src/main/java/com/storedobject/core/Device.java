package com.storedobject.core;

import java.sql.Date;
import java.util.Locale;
import java.util.function.Consumer;

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
        view(null, fileData);
    }

    default void view(String caption, FileData fileData) {
        if(fileData == null) {
            return;
        }
        if(caption == null || caption.isBlank()) {
            caption = fileData.getName();
            if(caption.contains("/")) {
                caption = caption.substring(caption.lastIndexOf('/') + 1);
            }
        }
        view(caption, fileData.getFile());
    }

    default void view(String caption, ContentProducer producer) {
        view(caption, producer, null);
    }

    default void view(ContentProducer producer) {
        view(null, producer);
    }

    default void view(ContentProducer producer, Consumer<Long> informMe) {
        view(null, producer, informMe);
    }

    void view(String caption, ContentProducer producer, Consumer<Long> informMe);

    default void download(StreamData streamData) {
        download(null, streamData);
    }

    default void download(String fileName, StreamData streamData) {
        download(new StreamDataContent(streamData, fileName));
    }

    default void download(ContentProducer producer) {
        download(producer, null);
    }

    default void download(ContentProducer producer, Consumer<Long> informMe) {
        view(producer, informMe);
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
    MessageViewer getMessageViewer();

    default void setDate(Date date) {
    }

    default void parse(Logic logic) throws SOException {
        throw new SOException("Not supported");
    }
}
