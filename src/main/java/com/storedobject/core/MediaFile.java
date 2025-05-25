package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public final class MediaFile extends Name implements ContentType, HasStreamData {

    private static MediaFile noImage;
    private Id fileId = Id.ZERO;
    private StreamData file;
    private long timeKey;

    public MediaFile() {
    }

    public static void columns(Columns columns) {
        columns.add("File", "id");
        columns.add("TimeKey", "long");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    @Override
    public String getUniqueCondition() {
        return "lower(Name)='" + name.toLowerCase() + "'";
    }

    public static String[] displayColumns() {
        return new String[] { "Name" };
    }

    public static String[] browseColumns() {
        return new String[] { "Name", "File" };
    }

    public static String[] protectedColumns() {
        return new String[] { "TimeKey" };
    }

    public void setFile(Id fileId) {
        this.fileId = fileId;
    }

    public void setFile(BigDecimal idValue) {
        setFile(new Id(idValue));
    }

    public void setFile(StreamData file) {
        setFile(file == null ? null : file.getId());
    }

    @Column(order = 200)
    public Id getFileId() {
        return fileId;
    }

    public StreamData getFile() {
        if(file == null) {
            file = get(StreamData.class, fileId);
        }
        return file;
    }

    @Override
    public StreamData getStreamData() {
        return getFile();
    }

    @SetNotAllowed
    public long getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(long timeKey) {
        if(!loading()) {
            throw new Set_Not_Allowed("Time Key");
        }
        this.timeKey = timeKey;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        file = null;
        if(nameInvalid()) {
            throw new Invalid_Value("Name");
        }
        if(name.equals("noimage")) {
            throw new Invalid_Value("Name - 'noimage' is reserved");
        }
        timeKey = System.currentTimeMillis();
        fileId = tm.checkType(this, fileId, StreamData.class,false);
        checkForDuplicate("Name");
        super.validateData(tm);
    }

    private boolean nameInvalid() {
        String name = this.name;
        if(name == null || name.isEmpty()) {
            return true;
        }
        name = name.strip();
        while(name.contains("  ")) {
            name = name.replace("  ", " ");
        }
        name = name.replace(' ', '-');
        boolean allowDot = true;
        char c;
        for(int i = 0; i < name.length(); i++) {
            c = name.charAt(i);
            if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == '-')) {
                continue;
            }
            if(i > 0 && c == '.' && allowDot) {
                allowDot = false;
                continue;
            }
            return true;
        }
        this.name = name;
        return false;
    }

    public String getContentType() {
        return getFile().getContentType();
    }

    public String getMimeType() {
        return getFile().getMimeType();
    }

    public String getFileName() {
        return timeKey + "/" + getName() + "." + getFile().getFileExtension();
    }

    @Override
    public boolean isImage() {
        return getFile().isImage();
    }

    @Override
    public boolean isAudio() {
        return getFile().isAudio();
    }

    @Override
    public boolean isVideo() {
        return getFile().isVideo();
    }

    @Override
    public boolean isPDF() {
        return getFile().isPDF();
    }

    public static MediaFile get(String name) {
        if(name == null) {
            return null;
        }
        name = name.strip();
        if("noimage".equalsIgnoreCase(name)) {
            return noImage();
        }
        MediaFile mf = get(MediaFile.class, "lower(Name)='" + name.toLowerCase() + "'");
        if(mf != null) {
            return mf;
        }
        return list(MediaFile.class, "lower(Name) LIKE '" + name.toLowerCase() + "%'").single(false);
    }

    public static ObjectIterator<MediaFile> list(String name) {
        return list(MediaFile.class, "lower(Name) LIKE '" + name.trim().toLowerCase() + "%'");
    }

    public static MediaFile noImage() {
        if(noImage != null) {
            return noImage;
        }
        noImage = new MediaFile();
        noImage.makeVirtual();
        noImage.name = "noimage";
        noImage.timeKey = 100;
        noImage.file = new StreamData() {
            @Override
            public InputStream getContent() {
                return new ByteArrayInputStream("""
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1 1" width="0" height="0">
                            <rect width="1" height="1" fill="none" />
                        </svg>
                        """.getBytes(StandardCharsets.UTF_8));
            }
        };
        noImage.file.setContentType("image/svg+xml");
        noImage.file.makeVirtual();
        return noImage;
    }
}
