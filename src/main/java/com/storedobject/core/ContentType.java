package com.storedobject.core;

public interface ContentType {

    String getContentType();

    default String getMimeType() {
        return getContentType();
    }

    default boolean isImage() {
        return getMimeType().startsWith("image/");
    }

    default boolean isVideo() {
        return getMimeType().startsWith("video/");
    }

    default boolean isAudio() {
        return getMimeType().startsWith("audio/");
    }

    default boolean isMedia() {
        return isImage() || isAudio() || isVideo();
    }

    default boolean isLink() {
        return getContentType().startsWith("l:");
    }

    default boolean isPDF() {
        return getMimeType().equals("application/pdf");
    }

    default boolean isHTML() {
        return getMimeType().equals("text/html");
    }

    default boolean isText() {
        return getMimeType().equals("text/plain");
    }

    default String getLink() {
        return null;
    }
}
