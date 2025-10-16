package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.common.Sequencer;
import com.storedobject.core.ContentProducer;
import com.storedobject.core.StreamData;
import com.storedobject.office.PDFProperties;
import com.storedobject.office.od.Office;
import com.storedobject.ui.Application;
import com.storedobject.ui.FileResource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.WebBrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class ContentGenerator extends AbstractContentGenerator {

    private static final Sequencer sequencerFileId = new Sequencer();
    private boolean kicked = false, internal = false, download;
    private final CountDownLatch started = new CountDownLatch(1);
    private final String caption;
    private DocumentViewer viewer;
    private final boolean windowMode;
    private final Component[] extraHeaderButtons;
    private DownloadStream downloadStream;

    public ContentGenerator(Application application, ContentProducer producer, String caption,
                            Consumer<AbstractContentGenerator> inform, Consumer<Long> timeTracker,
                            Runnable preRun, boolean windowMode, Component... extraHeaderButtons) {
        this(application, producer, false, caption, inform, timeTracker, preRun, windowMode,
                extraHeaderButtons);
    }

    public ContentGenerator(Application application, ContentProducer producer, boolean download, String caption,
                             Consumer<AbstractContentGenerator>  inform, Consumer<Long> timeTracker,
                            Runnable preRun, boolean windowMode, Component... extraHeaderButtons) {
        super(application, producer, inform, timeTracker, preRun);
        this.windowMode = windowMode;
        this.extraHeaderButtons = extraHeaderButtons;
        this.download = download;
        this.caption = caption;
        fileId = sequencerFileId.next();
        start();
    }

    public void setViewer(DocumentViewer viewer) {
        this.viewer = viewer;
    }

    private boolean canView() {
        return producer.isMedia() || producer.isPDF() || producer.isHTML();
    }

    @Override
    public void run() {
        producer.setTransactionManager(application.getTransactionManager());
        if(!download) {
            WebBrowser wb = application.getWebBrowser();
            if(producer.isPDF()) {
                internal = !wb.isAndroid() && !wb.isIPhone();
            } else {
                internal = canView();
            }
        }
        if(!internal) {
            started();
        }
        started.countDown();
        producer.produce();
        Throwable error = producer.getError();
        if(error != null) {
            application.access(() -> {
                Application.error(error);
                Application.error("The output may be incomplete!");
            });
        }
    }

    public void kick() {
        synchronized (this) {
            try {
                started.await();
            } catch (InterruptedException ignored) {
            }
            if(kicked) {
                return;
            }
            kicked = true;
        }
        kickInt();
    }

    private void kickInt() {
        if (!internal) {
            return;
        }
        if(viewer == null) {
            viewer = new DocumentViewer(null);
            viewer.contentType = producer;
        }
        viewer.setWindowMode(windowMode);
        viewer.setExtraButtons(extraHeaderButtons);
        if(canView()) {
            InputStream contentStream = getContentStream();
            viewer.view(resource(getContentType(), contentStream), contentStream, caption);
            if(producer.isMedia()) {
                application.access(application::closeWaitMessage);
            }
            return;
        }
        File file = createFile();
        try {
            String fileName = file.getAbsolutePath();
            fileName = fileName.substring(0, fileName.lastIndexOf('.') + 1) + "pdf";
            Office.convertToPDF(getContentStream(), new FileOutputStream(fileName), new PDFProperties(getExt().substring(1)));
            file = new File(fileName);
            FileResource fr = new FileResource(file, producer.getMimeType());
            viewer.view(fr, ((com.storedobject.ui.util.FileResource)fr).getInputStream(), caption);
        } catch (Throwable e) {
            application.access(application::closeWaitMessage);
            Application.error(e);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    private StreamResource resource(String contentType, InputStream contentStream) {
        StreamResource sr = new StreamResource(getFile() + getExt(), () -> contentStream);
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
        String finalLink = link;
        Thread.startVirtualThread(() -> openPage(finalLink));
    }

    private void openPage(String link) {
        try {
            createDownload();
            producer.ready();
        } catch (Exception e) {
            application.access(() -> {
                application.log(e);
                application.showNotification(e);
            });
            generated();
            return;
        }
        application.access(() -> {
            application.getPage().open(link);
            generated();
        });
    }

    /**
     * Create the download stream (Used when the content is downloaded).
     *
     * @return Stream to download.
     * @throws Exception If any error occurs.
     */
    @Override
    public DownloadStream getContent() throws Exception {
        return downloadStream;
    }

    private void createDownload() {
        kick();
        String fileName = getFile(), ext = getExt();
        if(!fileName.endsWith(ext)) {
            fileName += ext;
        }
        String ct = getContentType();
        downloadStream = new DownloadStream(producer, ct, fileName);
        if(!download) {
            if (!producer.isPDF()) {
                application.log("File: " + fileName + ", Type: " + ct);
                download = true;
            }
        }
        if(download) {
            downloadStream.setParameter("Content-Disposition", "attachment;filename=\"" + fileName + "\";");
        }
        downloadStream.setCacheTime(0L);
    }

    private InputStream content() {
        try {
            return producer.getContent();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void abort(Throwable error) {
        super.abort(error);
        IO.close(content());
    }
}