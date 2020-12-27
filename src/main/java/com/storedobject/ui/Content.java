package com.storedobject.ui;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.TransactionManager;

import java.io.InputStream;

/**
 * Content coming from an {@link InputStream} that can be viewed or downloaded
 * (via {@link Application#view(String, ContentProducer)} or {@link Application#download(ContentProducer)}).
 *
 * @author Syam
 */
public class Content implements ContentProducer {

    private final InputStream content;
    private final String mimeType, fileExtension;

    /**
     * Constructor.
     *
     * @param content Content.
     * @param mimeType Content type.
     * @param fileExtension File extension.
     */
    public Content(InputStream content, String mimeType, String fileExtension) {
        this.content = content;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    @Override
    public final void produce() {
    }

    @Override
    public final InputStream getContent() {
        return content;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public final String getContentType() {
        return mimeType;
    }

    @Override
    public final String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
    }
}
