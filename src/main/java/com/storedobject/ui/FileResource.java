package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileResource extends StreamResource {

    public FileResource(String fileName, String contentType) {
        this(fileName == null ? null : new File(fileName), contentType);
    }

    public FileResource(File file, String contentType) {
        this(new FileStream(file, contentType));
    }

    private FileResource(FileStream fileStream) {
        super(fileName(fileStream), fileStream);
        setContentType(fileStream.contentType);
    }

    private static String fileName(FileStream fileStream) {
        return PaintedImageResource.createBaseFileName() + fileExt(fileStream);
    }

    private static String fileExt(FileStream fileStream) {
        if(fileStream.fileExt != null) {
            return fileStream.fileExt;
        }
        String name = fileStream.file.getName();
        int i = name.lastIndexOf('.');
        return i < 0 ? "" : name.substring(i);
    }

    private static class FileStream implements InputStreamFactory {

        private final File file;
        private String contentType;
        private String fileExt;

        private FileStream(File file, String contentType) {
            this.file = file;
            this.contentType = contentType;
        }

        @Override
        public InputStream createInputStream() {
            String error = null;
            try {
                if(file == null) {
                    error = "File not found";
                } else if(!file.exists()) {
                    error = "File doesn't exists";
                } else if(file.isDirectory()) {
                    error = "File is a directory";
                } else if(!file.canRead()) {
                    error = "No read permission on file";
                }
                if(error == null) {
                    return IO.getInput(file);
                }
            } catch (IOException e) {
                error = e.getMessage();
            }
            if(error == null) {
                error = "Read error";
            }
            if(file != null) {
                error += " - " + file.getName();
            }
            contentType = "text/plain";
            fileExt = "txt";
            return new ByteArrayInputStream(error.getBytes(StandardCharsets.UTF_8));
        }
    }
}