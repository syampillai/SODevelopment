package com.storedobject.ui.util;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.StreamData;
import com.storedobject.ui.Application;
import com.storedobject.ui.Audio;
import com.storedobject.ui.Image;
import com.storedobject.ui.Video;
import com.storedobject.vaadin.PDFViewer;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.StreamResource;

import java.io.InputStream;

public class ContentGenerator extends AbstractContentGenerator {

    private static long fileIdCount = 0;
    private boolean started = false, internal = false, download;
    private String caption;

    public ContentGenerator(Application application, ContentProducer producer) {
        this(application, producer, true, null);
    }

    public ContentGenerator(Application application, ContentProducer producer, String caption) {
        this(application, producer, false, caption);
    }

    private ContentGenerator(Application application, ContentProducer producer, boolean download, String caption) {
        super(application, producer);
        this.download = download;
        this.caption = caption;
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized(application) {
            if(++fileIdCount == Long.MAX_VALUE) {
                fileIdCount = 1;
            }
            fileId = fileIdCount;
        }
        start();
    }

    @Override
    public void run() {
        producer.setTransactionManager(application.getTransactionManager());
        String ct = producer.getContentType();
        if(!download) {
            internal = ct.equals(PDF_CONTENT) || ct.startsWith("video/") || ct.startsWith("image/") || ct.startsWith("audio/");
        }
        if(!internal) {
            started();
        }
        synchronized(this) {
            started = true;
            notifyAll();
        }
        producer.produce();
    }

    public boolean kick() {
        synchronized (this) {
            while (!started) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (!internal) {
            return false;
        }
        String ct = getContentType();
        if (ct.equals(PDF_CONTENT)) {
            if(caption == null) {
                caption = "Report";
            }
            show(new PDFViewer(resource(ct)));
        } else if(ct.startsWith("video/")) {
            if(caption == null) {
                caption = "Video";
            }
            show(new Video(resource(ct)));
        } else if(ct.startsWith("audio/")) {
            if(caption == null) {
                caption = "Audio";
            }
            show(new Audio(resource(ct)));
        } else if(ct.startsWith("image/")) {
            if(caption == null) {
                caption = "Image";
            }
            show(new Image(resource(ct)));
        }
        return true;
    }

    private StreamResource resource(String contentType) {
        StreamResource sr = new StreamResource("so" + fileId + getExt(), this::getContentStream);
        sr.setContentType(contentType);
        return sr;
    }

    protected void started() {
        String link = producer.isLink() ? producer.getLink() : null;
        while(link != null) {
            if(!link.startsWith("db:")) {
                break;
            }
            StreamData streamData = StreamData.getViaLink(link);
            if(streamData == null) {
                return;
            }
            link = streamData.isLink() ? streamData.getLink() : null;
        }
        if(link == null) {
            application.addContent(fileId, this);
            link = "so" + fileId;
        }
        String toOpen = link;
//        String script = "window.open('" + (link != null ? link : ("so" + fileId)) + "', '_blank')"; // TODO remove after testing
        application.access(() -> application.getPage().open(toOpen));
    }

    private void show(Component viewer) {
        View.createCloseableView(viewer, caption).execute();
    }

    @Override
    public DownloadStream getContent() throws Exception {
        kick();
        String fileName = getFile(), ext = getExt();
        if(!fileName.endsWith(ext)) {
            fileName += ext;
        }
        String ct = getContentType();
        InputStream content;
        content = producer.getContent();
        DownloadStream ds = new DownloadStream(content, ct, fileName);
        if(!download) {
            if (!PDF_CONTENT.equals(ct)) {
                application.log("File: " + fileName + ", Type: " + ct);
                download = true;
            }
        }
        if(download) {
            ds.setParameter("Content-Disposition", "attachment;filename=\"" + fileName + "\";");
        }
        ds.setCacheTime(0L);
        return ds;
    }
}