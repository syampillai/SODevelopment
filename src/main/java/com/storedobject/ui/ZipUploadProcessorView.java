package com.storedobject.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        super.setProcessor(new ZipProcessor());
        zipEntryConsumer = processor;
        super.setMaxFiles(1);
    }

    /**
     * Do not override this method unless you want to process the file yourself without unzipping.
     *
     * @param content File stream.
     * @param mimeType Mime type.
     */
    @Override
    protected void process(InputStream content, String mimeType) {
        super.process(content, mimeType);
    }

    @Override
    public void setProcessor(BiConsumer<InputStream, String> processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFiles(int fileCount) {
        throw new UnsupportedOperationException();
    }

    public void setProcessor(Consumer<ZipEntry> zipEntryConsumer) {
        this.zipEntryConsumer = zipEntryConsumer;
    }

    /**
     * Override this method for processing the "Zip Entries". Each "Zip Entry" will be passed here unless a "Processor"
     * ({@link Consumer}) is set and in that case, Processor's accept() method
     * ({@link Consumer#accept(Object)}) will be called with each zip entry.
     *
     * @param zipEntry Zip entry to consume.
     * @throws IOException Exception for I/O error.
     */
    @SuppressWarnings("RedundantThrows")
    protected void process(ZipEntry zipEntry) throws IOException {
        if(zipEntryConsumer != null) {
            zipEntryConsumer.accept(zipEntry);
        } else {
            redMessage("Ignoring zip entry: " + zipEntry.getName());
        }
    }

    /**
     * This will be invoked when the processing is completed.
     */
    protected void processCompleted() {
    }

    private class ZipProcessor implements BiConsumer<InputStream, String> {

        private ZipInputStream zin;

        @Override
        public void accept(InputStream inputStream, String mimeType) {
            if(!"application/zip".equals(mimeType)) {
                error("Not a zip file!");
                return;
            }
            if(zin != null) {
                error("File already processed!");
                return;
            }
            zin = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while(true) {
                try {
                    zipEntry = zin.getNextEntry();
                    if(zipEntry == null) {
                        break;
                    }
                    process(zipEntry);
                } catch (IOException e) {
                    error(e);
                }
            }
            processCompleted();
            blueMessage("Done");
        }
    }
}
