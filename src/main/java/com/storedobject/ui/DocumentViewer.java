package com.storedobject.ui;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;
import com.storedobject.vaadin.PDFViewer;

public class DocumentViewer extends PDFViewer {

    public DocumentViewer() {
    }

    public DocumentViewer(Id streamDataId) {
        this();
    }

    public DocumentViewer(StreamData streamData) {
        this();
    }

    public DocumentViewer(ContentProducer contentProducer) {
    }

    public void setDocument(Id streamDataId) {
    }

    public void view(String caption) {
    }

    public void setDocument(ContentProducer contentProducer) {
    }
}
