package com.storedobject.core;

import com.storedobject.common.ContentGenerator;
import com.storedobject.common.HTTP;
import com.storedobject.common.IO;
import com.storedobject.common.SOException;

import java.io.*;
import java.lang.reflect.Method;

public class StreamData extends StoredObject implements ContentType, HasStreamData {

    private String contentType;
    private StreamDataProvider streamProvider;

    public StreamData(String contentType) {
        this.contentType = contentType;
    }

    /**
     * For internal use only.
     */
    public StreamData() {
    }

    public static void columns(Columns columns) {
        columns.add("ContentType", "text");
    }

    public void setStreamDataProvider(StreamDataProvider provider) {
        this.streamProvider = provider;
    }

    public StreamDataProvider getStreamDataProvider() {
        return streamProvider;
    }

    public final String getMimeType() {
        return contentType.startsWith("l:") ? contentType.substring(2) : contentType;
    }

    public final String getContentType() {
        return contentType;
    }

    public final void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @SuppressWarnings("ConstantConditions")
    public InputStream getContent() {
        if(contentType.startsWith("l:")) {
            InputStream in = null;
            try {
                String link = getLink();
                if(link.startsWith("db:")) {
                    StreamData sd = getViaLink(link);
                    in = sd.getContent();
                    contentType = sd.getMimeType();
                } else {
                    HTTP http = new HTTP(link);
                    in = http.getInputStream();
                    contentType = http.getConnection().getContentType();
                    if(contentType == null) {
                        contentType = "application/octet-stream";
                    }
                }
                contentType = "l:" + contentType;
            } catch(Throwable ignored) {
            }
            return in;
        }
        return new DatabaseInputStream(this);
    }

    public static StreamData getViaLink(String link) {
        try {
            link = link.substring(3);
            int p = link.indexOf('/');
            String className = link.substring(0, p);
            link = link.substring(p + 1);
            p = link.indexOf('/');
            String attribute = link.substring(0, p);
            link = link.substring(p + 1);
            @SuppressWarnings("unchecked") Class<? extends StoredObject> soClass = (Class<? extends StoredObject>) JavaClassLoader.getLogic(className);
            Method method = soClass.getMethod("get" + attribute);
            return (StreamData) method.invoke(StoredObject.get(soClass, new Id(link)));
        } catch (Throwable error) {
            return null;
        }
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        if(updated() || inserted()) {
            if(streamProvider == null) {
                throw new SOException("No Stream Data Provider");
            }
            try (InputStream in = streamProvider.getStream(this);
                 DatabaseOutputStream out = new DatabaseOutputStream(getId(), ((AbstractTransaction)getTransaction()).getSQL())) {
                if (in == null) {
                    streamProvider.writeStream(this, out);
                } else {
                    IO.copy(in, out);
                }
            } catch (Data_Not_Changed dnc) {
                if (inserted()) {
                    throw dnc;
                }
            }
        }
    }

    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(contentType)) {
            contentType = "application/octet-stream";
        }
        super.validateData(tm);
    }

    public String toString() {
        return contentType;
    }

    @Override
    public String toDisplay() {
        return (isLink() ? "Link to " : "") + toDisplay(getMimeType());
    }

    private String toDisplay(String contentType) {
        if(isImage()) {
            return "Image";
        }
        if(isAudio()) {
            return "Audio";
        }
        if(isVideo()) {
            return "Video";
        }
        if(contentType.endsWith("/pdf")) {
            return "PDF File";
        }
        if(contentType.startsWith("text/")) {
            return "Text File (" + contentType.substring(contentType.indexOf('/') + 1).toUpperCase() + ")";
        }
        contentType = getFileExtension();
        if("bin".equals(contentType)) {
            return "Binary File";
        }
        return contentType.toUpperCase() + " File";
    }

    public String getFileExtension() {
        String e = ContentGenerator.getFileExtension(getMimeType());
        if(e == null) {
            StoredObject.logger.info("Unknown content type: '" + contentType + "'");
            return "bin";
        }
        return e;
    }

    public String getLink() {
        if(!isLink()) {
            return null;
        }
        try (BufferedReader br = IO.getReader(new DatabaseInputStream(this))) {
            return br.readLine();
        } catch (Exception ignore) {
        }
        return null;
    }

    public void view(Device device) {
        device.view(this);
    }

    /**
     * Get the {@link StreamData} instance for the given name. The name could be the {@link Id} of the instance or the
     * {@link Id} or name of a {@link FileData} instance as a string.
     *
     * @param name Name.
     * @return {@link StreamData} instance if available.
     */
    public static StreamData get(String name) {
        if(name == null || name.isBlank() || name.equals("0")) {
            return null;
        }
        StreamData sd = null;
        if(StringUtility.isDigit(name)) {
            sd = StreamData.get(StreamData.class, "Id=" + name);
            if(sd == null) {
                FileData file = FileData.get(FileData.class, "Id=" + name, true);
                if(file != null) {
                    sd = file.getFile();
                }
            }
        } else {
            FileData file = FileData.get(name);
            if(file != null) {
                sd = file.getFile();
            }
        }
        return sd;
    }

    @Override
    public StreamData getStreamData() {
        return this;
    }
}
