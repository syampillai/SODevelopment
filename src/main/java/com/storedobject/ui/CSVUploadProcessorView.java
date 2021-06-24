package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.StringUtility;

import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CSVUploadProcessorView extends UploadProcessorView {

    private Consumer<String[]> csvConsumer;

    public CSVUploadProcessorView(String caption) {
        this(caption, null, null);
    }

    public CSVUploadProcessorView(String caption, Consumer<String[]> processor) {
        this(caption, null, processor);
    }

    public CSVUploadProcessorView(String caption, String message) {
        this(caption, message, null);
    }

    public CSVUploadProcessorView(String caption, String message, Consumer<String[]> processor) {
        super(caption, message, null);
        super.setProcessor(new CSVProcessor());
        csvConsumer = processor;
        super.setMaxFiles(1);
    }

    /**
     * Do not override this method unless you want to process the file yourself.
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

    public void setProcessor(Consumer<String[]> csvConsumer) {
        this.csvConsumer = csvConsumer;
    }

    /**
     * Override this method for processing the "CSV Entries". Each "CSV Entry" - 1 per line - will be passed here as
     * a string array unless a "Processor"
     * ({@link Consumer}) is set and in that case, Processor's accept() method
     * ({@link Consumer#accept(Object)}) will be called with each CSV entry.
     *
     * @param csvEntry CSV entry to consume.
     */
    @SuppressWarnings("RedundantThrows")
    protected void process(String[] csvEntry) {
        if(csvConsumer != null) {
            csvConsumer.accept(csvEntry);
        }
    }

    /**
     * Get the line number of the line being processed currently.
     *
     * @return Line number if available, otherwise, -1.
     */
    public int getLineNumber() {
        try {
            return (((CSVProcessor)processor).br).getLineNumber();
        } catch(Throwable e) {
            return -1;
        }
    }

    /**
     * This will be invoked when the processing is completed.
     */
    protected void processCompleted() {
    }

    private class CSVProcessor implements BiConsumer<InputStream, String> {

        private LineNumberReader br;

        @Override
        public void accept(InputStream inputStream, String mimeType) {
            if(!"text/csv".equals(mimeType)) {
                error("Not a CSV file!");
                return;
            }
            if(br != null) {
                error("File already processed!");
                return;
            }
            br = new LineNumberReader(IO.getReader(inputStream));
            String[] csvEntry;
            try {
                while((csvEntry = StringUtility.getCSV(br)) != null) {
                    process(csvEntry);
                }
            } catch (Exception e) {
                error(e);
            }
            IO.close(br);
            processCompleted();
            blueMessage("Done");
        }
    }
}
