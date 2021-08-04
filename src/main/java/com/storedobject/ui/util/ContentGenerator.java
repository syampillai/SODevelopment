package com.storedobject.ui.util;

import com.storedobject.common.Sequencer;
import com.storedobject.core.ContentProducer;
import com.storedobject.core.StreamData;
import com.storedobject.office.PDFProperties;
import com.storedobject.office.od.Office;
import com.storedobject.ui.Application;
import com.storedobject.ui.FileResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.WebBrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.function.Consumer;

public class ContentGenerator extends AbstractContentGenerator {

    private static final Sequencer sequencerFileId = new Sequencer();
    private boolean started = false, internal = false, download;
    private final String caption;
    private DocumentViewer viewer;

    public ContentGenerator(Application application, ContentProducer producer, String caption,
                            Consumer<AbstractContentGenerator> inform, Consumer<Long> timeTracker) {
        this(application, producer, false, caption, inform, timeTracker);
    }

    private ContentGenerator(Application application, ContentProducer producer, boolean download, String caption,
                             Consumer<AbstractContentGenerator>  inform, Consumer<Long> timeTracker) {
        super(application, producer, inform, timeTracker);
        this.download = download;
        this.caption = caption;
        fileId = sequencerFileId.next();
        start();
    }

    public void setViewer(DocumentViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void run() {
        producer.setTransactionManager(application.getTransactionManager());
        if(!download) {
            WebBrowser wb = application.getWebBrowser();
            if(producer.isPDF()) {
                internal = !wb.isAndroid() && !wb.isIPhone();
            } else {
                internal = producer.isMedia();
            }
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
        if(viewer == null) {
            viewer = new DocumentViewer();
            viewer.contentType = producer;
        }
        if(producer.isPDF() || producer.isMedia()) {
            viewer.view(resource(ct), caption);
            return true;
        }
        File file = createFile();
        try {
            String fileName = file.getAbsolutePath();
            fileName = fileName.substring(0, fileName.lastIndexOf('.') + 1) + "pdf";
            Office.convertToPDF(getContentStream(), new FileOutputStream(fileName), new PDFProperties(getExt().substring(1)));
            file = new File(fileName);
            viewer.view(new FileResource(file, producer.getMimeType()), caption);
        } catch (Throwable e) {
            Application.error(e);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        return true;
    }

    private StreamResource resource(String contentType) {
        StreamResource sr = new StreamResource(getFile() + getExt(), this::getContentStream);
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
        application.access(() -> {
            application.getPage().open(toOpen);
            generated();
        });
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
            if (!producer.isPDF()) {
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