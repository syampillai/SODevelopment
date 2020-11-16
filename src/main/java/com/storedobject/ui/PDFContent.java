package com.storedobject.ui;

import com.storedobject.ui.util.AbstractContentGenerator;

import java.io.InputStream;

public final class PDFContent extends Content {

    public PDFContent(InputStream stream) {
        super(stream);
    }

    @Override
    public String getContentType() {
        return AbstractContentGenerator.PDF_CONTENT;
    }

    @Override
    public String getFileExtension() {
        return "pdf";
    }
}
