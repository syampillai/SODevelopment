package com.storedobject.ui;

import com.storedobject.core.ContentProducer;
import com.storedobject.ui.util.AbstractContentGenerator;

import java.io.InputStream;

/**
 * PDF content coming from an {@link InputStream} that can be viewed or downloaded
 * (via {@link Application#view(String, ContentProducer)} or {@link Application#download(ContentProducer)}).
 *
 * @author Syam
 */
public final class PDFContent extends Content {

    /**
     * Constructor.
     *
     * @param content PDF content.
     */
    public PDFContent(InputStream content) {
        super(content, AbstractContentGenerator.PDF_CONTENT, "pdf");
    }
}
