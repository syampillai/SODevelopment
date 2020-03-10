package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Upload;

import java.io.InputStream;
import java.util.function.BiConsumer;

public class UploadProcessorView extends TextView {

    public UploadProcessorView(String caption) {
        this(caption, null, null);
    }

    public UploadProcessorView(String caption, BiConsumer<InputStream, String> processor) {
        this(caption, null, processor);
    }

    public UploadProcessorView(String caption, String message) {
        this(caption, message, null);
    }

    public UploadProcessorView(String caption, String message, BiConsumer<InputStream, String> processor) {
        super(caption);
    }

    public Upload getUploadComponent() {
        return null;
    }

    @Override
    protected Component getTopComponent() {
        return null;
    }

    @Override
    public String getProgressCaption() {
        return null;
    }

    public void setProcessor(BiConsumer<InputStream, String> processor) {
    }

    protected void process(InputStream content, String mimeType) {
    }

    protected void uploadAborted() {
    }

    public String getFileName() {
        return null;
    }

    public void setMaxFiles(int fileCount) {
    }

    public int getFileCount() {
        return 0;
    }
}
