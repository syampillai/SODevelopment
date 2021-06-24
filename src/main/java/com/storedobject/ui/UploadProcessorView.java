package com.storedobject.ui;

import com.storedobject.common.Processor;
import com.storedobject.vaadin.FormLayout;
import com.storedobject.vaadin.UploadField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Upload;

import java.io.InputStream;
import java.util.function.BiConsumer;

public class UploadProcessorView extends TextView {

    private final UploadField uploadField;
    private final String message;
    BiConsumer<InputStream, String> processor;
    private String fileName;

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
        this.message = message;
        this.processor = processor;
        uploadField = new UploadField(caption, this::process);
        uploadField.setMaxFiles(1);
        uploadField.getUploadComponent().addFailedListener(e -> {
            error(e.getReason());
            uploadAborted();
        });
    }

    public Upload getUploadComponent() {
        return uploadField.getUploadComponent();
    }

    @Override
    protected Component getTopComponent() {
        FormLayout form = new FormLayout();
        form.setColumns(1);
        form.add(uploadField);
        return form;
    }

    @Override
    public String getProgressCaption() {
        return message;
    }

    public void setProcessor(BiConsumer<InputStream, String> processor) {
        this.processor = processor;
    }

    @Override
    public void setProcessor(Processor processor) {
        throw new UnsupportedOperationException();
    }

    protected void process(InputStream content, String mimeType) {
        try {
            fileName = uploadField.getFileName();
            if (processor == null) {
                //noinspection StatementWithEmptyBody
                while (content.read() != -1) ;
            } else {
                processor.accept(content, mimeType);
            }
        } catch (Throwable error) {
            error(error);
        }
    }

    protected void uploadAborted() {
        error("Process aborted");
    }

    public String getFileName() {
        return fileName;
    }

    public void setMaxFiles(int fileCount) {
        uploadField.setMaxFiles(fileCount);
    }

    public int getFileCount() {
        return uploadField.getValue();
    }
}
