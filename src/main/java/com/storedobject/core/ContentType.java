package com.storedobject.core;

public interface ContentType {

    String getContentType();

    default String getMimeType() {
        return getContentType();
    }

    default boolean isImage() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    default boolean isVideo() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("video/");
    }

    default boolean isAudio() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.startsWith("audio/");
    }

    default boolean isMedia() {
        return isImage() || isAudio() || isVideo();
    }

    default boolean isLink() {
        String contentType = getContentType();
        return contentType != null && contentType.startsWith("l:");
    }

    default boolean isPDF() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.equals("application/pdf");
    }

    default boolean isHTML() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.equals("text/html");
    }

    default boolean isText() {
        String mimeType = getMimeType();
        return mimeType != null && mimeType.equals("text/plain");
    }
    default String getLink() {
        return null;
    }
}
