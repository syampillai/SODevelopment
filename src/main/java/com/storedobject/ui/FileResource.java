package com.storedobject.ui;

import java.io.File;

public class FileResource extends com.storedobject.ui.util.FileResource {

    public FileResource(String fileName, String contentType) {
        super(fileName, contentType);
    }

    public FileResource(File file, String contentType) {
        super(file, contentType);
    }
}