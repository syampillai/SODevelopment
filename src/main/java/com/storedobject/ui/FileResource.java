package com.storedobject.ui;

import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.InputStream;

public class FileResource extends StreamResource {

    public FileResource(String fileName, String contentType) {
        this(fileName == null ? null : new File(fileName), contentType);
    }

    public FileResource(File file, String contentType) {
        this(file, contentType, false);
    }

    FileResource(File file, String contentType, boolean db) {
        this(new FileStream(file, contentType, db));
    }

    private FileResource(FileStream fileStream) {
        super(fileName(fileStream), fileStream);
    }

    private static String fileName(FileStream fileStream) {
        return PaintedImageResource.createBaseFileName();
    }

    private static class FileStream implements InputStreamFactory {

        private FileStream(File file, String contentType, boolean db) {
        }

        @Override
        public InputStream createInputStream() {
            return null;
        }
    }
}