package com.storedobject.ui;

import com.storedobject.core.*;

public class DocumentViewer extends com.storedobject.ui.util.DocumentViewer {

    public DocumentViewer() {
        super();
    }

    public DocumentViewer(Id streamDataId) {
        super(streamDataId);
    }

    public DocumentViewer(StreamData streamData) {
        super(streamData);
    }

    public DocumentViewer(ContentProducer contentProducer) {
        super(contentProducer);
    }

    public void setDocument(Id streamDataId) {
        super.setDocument(streamDataId);
    }

    public void setDocument(StreamData streamData) {
        super.setDocument(streamData);
    }

    public void setDocument(ContentProducer contentProducer) {
        super.setDocument(contentProducer);
    }

    public void view(String caption) {
        super.view(caption);
    }

    static void view(String caption, MediaFile mediaFile) {
        if(mediaFile == null) {
            return;
        }
        view("media/" + mediaFile.getFileName(), caption, mediaFile, mediaFile.getMimeType());
    }
}
