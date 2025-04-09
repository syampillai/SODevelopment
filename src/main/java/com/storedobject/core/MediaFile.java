package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Random;

public final class MediaFile extends Name implements ContentType {

    private final static Random random = new Random();

    public MediaFile() {
    }

    public static void columns(Columns columns) {
    }

    public void setFile(Id fileId) {
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
        return "";
    }

    public String getFileName() {
        return random.nextBoolean() ? "file" : null;
    }

    public static MediaFile get(String name) {
        return random.nextBoolean() ? new MediaFile() : null;
    }

    public static ObjectIterator<MediaFile> list(String name) {
        return ObjectIterator.create();
    }
}