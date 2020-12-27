package com.storedobject.core;

import java.math.BigDecimal;

public final class MediaFile extends Name {

    public MediaFile() {
    }

    public static void columns(Columns columns) {
    }

    public final void setFile(Id fileId) {
    }

    public void setFile(BigDecimal idValue) {
    }

    public void setFile(StreamData file) {
    }

    public Id getFileId() {
        return null;
    }

    public StreamData getFile() {
        return new StreamData();
    }

    public long getTimeKey() {
        return 0L;
    }

    public void setTimeKey(long timeKey) {
    }

    public String getContentType() {
        return null;
    }

    public String getMimeType() {
        return "";
    }

    public String getFileName() {
        return null;
    }

    public boolean isImage() {
        return false;
    }

    public boolean isAudio() {
        return false;
    }

    public boolean isVideo() {
        return false;
    }

    public static MediaFile get(String name) {
        return null;
    }

    public static ObjectIterator<MediaFile> list(String name) {
        return null;
    }
}