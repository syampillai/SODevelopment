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

    public DBResource(HasStreamData hasStreamData) {
        this(hasStreamData == null ? null : hasStreamData.getStreamData(), fileName(hasStreamData));
    }

    private DBResource(StreamData streamData, String fileName) {
        super(fileName, new DBStream(streamData == null ? null : streamData.getStreamData(), fileName));
        setContentType(streamData == null ? "text/plain" : streamData.getMimeType());
    }

    private static String fileNameGen(StreamData streamData) {
        return PaintedImageResource.createBaseFileName() + "." + (streamData == null ? "txt" : streamData.getFileExtension());
    }

    private static String fileName(HasStreamData hasStreamData) {
        if (hasStreamData == null) {
            return fileNameGen(null);
        }
        String name = hasStreamData.getName();
        return name == null ? fileNameGen(hasStreamData.getStreamData()) : hasStreamData.getFileName();
    }

    private record DBStream(StreamData streamData, String fileName) implements InputStreamFactory {

        @Override
        public InputStream createInputStream() {
            if (streamData == null) {
                return new ByteArrayInputStream(("No content" + (fileName == null ? "!" : ("for file - " + fileName))).getBytes(StandardCharsets.UTF_8));
            }
            return streamData.getContent();
        }
    }
}
