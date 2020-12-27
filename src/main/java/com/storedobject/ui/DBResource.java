package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DBResource extends StreamResource {

    public DBResource(Id streamDataId) {
        this(StoredObject.get(StreamData.class, streamDataId));
    }

    public DBResource(StreamData streamData) {
        this(streamData, null);
    }

    public DBResource(FileData fileData) {
        this(fileData == null ? null : fileData.getFile(), fileData == null ? null : fileData.getName());
    }

    public DBResource(MediaFile mediaFile) {
        this(mediaFile == null ? null : mediaFile.getFile(), mediaFile == null ? null : mediaFile.getName());
    }

    private DBResource(StreamData streamData, String fileName) {
        super(fileName(streamData), new DBStream(streamData, fileName));
        setContentType(streamData == null ? "text/plain" : streamData.getMimeType());
    }

    private static String fileName(StreamData streamData) {
        return PaintedImageResource.createBaseFileName() + "." + (streamData == null ? "txt" : streamData.getFileExtension());
    }

    private static class DBStream implements InputStreamFactory {

        private final StreamData streamData;
        private final String fileName;

        private DBStream(StreamData streamData, String fileName) {
            this.streamData = streamData;
            this.fileName = fileName;
        }

        @Override
        public InputStream createInputStream() {
            if(streamData == null) {
                return new ByteArrayInputStream(("No content" + (fileName == null ? "!" : ("for file - " + fileName))).getBytes(StandardCharsets.UTF_8));
            }
            return streamData.getContent();
        }
    }
}
