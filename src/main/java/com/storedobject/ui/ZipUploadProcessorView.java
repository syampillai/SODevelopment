package com.storedobject.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;

public class ZipUploadProcessorView extends UploadProcessorView {

    private Consumer<ZipEntry> zipEntryConsumer;

    public ZipUploadProcessorView(String caption) {
        this(caption, null, null);
    }

    public ZipUploadProcessorView(String caption, Consumer<ZipEntry> processor) {
        this(caption, null, processor);
    }

    public ZipUploadProcessorView(String caption, String message) {
        this(caption, message, null);
    }

    public ZipUploadProcessorView(String caption, String message, Consumer<ZipEntry> processor) {
        super(caption, message, null);
    }

    @Override
    protected void process(InputStream content, String mimeType) {
    }

    @Override
    public void setProcessor(BiConsumer<InputStream, String> processor) {
    }

    @Override
    public void setMaxFiles(int fileCount) {
    }

    public void setProcessor(Consumer<ZipEntry> zipEntryConsumer) {
    }

    /**
     * Override this method for processing the "Zip Entries". Each "Zip Entry" will be passed here unless a "Processor"
     * ({@link Consumer<ZipEntry>}) is set and in that case, Processor's accept() method
     * ({@link Consumer#accept(Object)}) will be called with each zip entry.
     *
     * @param zipEntry Zip entry to consume.
     * @throws IOException Exception for I/O error.
     */
    protected void process(ZipEntry zipEntry) throws IOException {
    }
}
