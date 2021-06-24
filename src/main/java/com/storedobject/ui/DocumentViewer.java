package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.ContentGenerator;
import com.storedobject.vaadin.PDFViewer;
import com.storedobject.vaadin.View;

public class DocumentViewer extends PDFViewer {

    public DocumentViewer() {
    }

    public DocumentViewer(Id streamDataId) {
        this();
        setDocument(streamDataId);
    }

    public DocumentViewer(StreamData streamData) {
        this();
        setDocument(streamData);
    }

    public DocumentViewer(ContentProducer contentProducer) {
        if(contentProducer == null) {
            return;
        }
        setDocument(contentProducer);
    }

    public void setDocument(Id streamDataId) {
        setDocument(StoredObject.get(StreamData.class, streamDataId));
    }

    public void setDocument(StreamData streamData) {
        if (streamData == null) {
            setSource((String) null);
            return;
        }
        setDocument(new StreamDataContent(streamData));
    }

    public void view(String caption) {
        View.createCloseableView(this, caption).execute();
    }

    public void setDocument(ContentProducer contentProducer) {
        if (contentProducer == null) {
            setSource((String) null);
            return;
        }
        new Content(contentProducer).kick();
    }

    private class Content extends ContentGenerator {

        protected Content(ContentProducer producer) {
            super(Application.get(), producer, null, null);
            setViewer(DocumentViewer.this);
        }

        @Override
        protected void started() {
        }
    }
}
