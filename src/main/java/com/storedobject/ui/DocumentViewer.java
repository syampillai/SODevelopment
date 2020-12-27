package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.office.PDFProperties;
import com.storedobject.office.od.Office;
import com.storedobject.ui.util.ContentGenerator;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.PDFViewer;
import com.storedobject.vaadin.View;

import java.io.File;
import java.io.FileOutputStream;

public class DocumentViewer extends PDFViewer {

    private File file = null;

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

    private class V extends View implements CloseableView {

        private V(String caption) {
            super(DocumentViewer.this, caption);
            execute();
        }

        @Override
        public void clean() {
            if(file != null) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                file = null;
            }
        }
    }

    public void view(String caption) {
        new V(caption);
    }

    public void setDocument(ContentProducer contentProducer) {
        if (file != null) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        if (contentProducer == null) {
            setSource((String) null);
            return;
        }
        new Content(contentProducer).kick();
    }

    private class Content extends ContentGenerator {

        protected Content(ContentProducer producer) {
            super(Application.get(), producer, null);
        }

        @Override
        protected void started() {
        }

        @Override
        public boolean kick() {
            if (super.kick()) {
                return true;
            }
            if (getExt().equals(".pdf")) {
                file = createFile();
            } else {
                try {
                    String fileName = file.getAbsolutePath();
                    fileName = fileName.substring(0, fileName.lastIndexOf('.') + 1) + "pdf";
                    Office.convertToPDF(getContentStream(), new FileOutputStream(fileName), new PDFProperties(getExt().substring(1)));
                    file = new File(fileName);
                } catch (Throwable e) {
                    Application.error(e);
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    file = null;
                    setSource((String) null);
                    return false;
                }
            }
            setSource(new FileResource(file, PDF_CONTENT));
            return true;
        }
    }
}
