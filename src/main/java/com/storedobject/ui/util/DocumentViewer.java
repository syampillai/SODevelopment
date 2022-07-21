package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.storedobject.vaadin.Audio;
import com.storedobject.vaadin.Image;
import com.storedobject.vaadin.Video;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.InputStream;

public class DocumentViewer extends PDFViewer {

    private final Application application;
    ContentType contentType;
    private Content generator;
    private View view;
    private String caption;
    private Component viewerComponent;
    private final Runnable listener;
    private boolean windowMode = false;

    public DocumentViewer(Runnable listener) {
        this.listener = listener;
        application = Application.get();
        viewerComponent = this;
    }

    public static void view(String caption, MediaFile mediaFile, boolean windowMode) {
        if(mediaFile == null) {
            return;
        }
        if(caption == null) {
            caption = mediaFile.getFileName();
        }
        DocumentViewer dv = new DocumentViewer(null);
        dv.setWindowMode(windowMode);
        dv.contentType = mediaFile;
        dv.view("media/" + mediaFile.getFileName(), mediaFile.getFile(), caption);
    }

    @Override
    public void setSource(String fileURL) {
        super.setSource(fileURL);
        if(listener != null) {
            listener.run();
        }
    }

    public void setWindowMode(boolean windowMode) {
        this.windowMode = windowMode;
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
        this.caption = caption;
        if(view != null) {
            view.setCaption(caption);
            view.execute();
        }
    }

    private void caption(String caption) {
        if(this.caption == null || this.caption.isBlank()) {
            this.caption = caption;
        }
        if(this.caption == null || this.caption.isBlank() || this.caption.equals("_")) {
            if(contentType.isPDF()) {
                this.caption = "Report";
            } else if(contentType.isVideo()) {
                this.caption = "Video";
            } else if(contentType.isAudio()) {
                this.caption = "Audio";
            } else if(contentType.isImage()) {
                this.caption = "Image";
            }
        }
    }

    private void view(String resource, StreamData streamData, String caption) {
        if(contentType == null) {
            super.setSource(resource);
            return;
        }
        caption(caption);
        if(view == null) {
            if(contentType.isPDF()) {
                view = new ContentView(viewerComponent = this);
                super.setSource(resource);
            } else if(contentType.isVideo()) {
                view = new ContentView(viewerComponent = new Video(resource, contentType.getMimeType()));
            } else if(contentType.isAudio()) {
                view = new ContentView(viewerComponent = new Audio(resource, contentType.getMimeType()));
            } else if(contentType.isImage()) {
                view = new ContentView(viewerComponent = new Image(resource));
            } else if(contentType.isHTML()) {
                if(streamData != null) {
                    view = new HTMLView(IO.getReader(streamData.getContent()), isWindow());
                    viewerComponent = ((HTMLView) view).getViewerComponent();
                }
            } else {
                viewerComponent = null;
                return;
            }
            view.setCaption(caption);
        } else {
            if(contentType.isPDF()) {
                super.setSource(resource);
            } else if(viewerComponent instanceof Video v) {
                v.setSource(resource, contentType.getMimeType());
            } else if(viewerComponent instanceof Audio a) {
                a.setSource(resource, contentType.getMimeType());
            } else if(viewerComponent instanceof Image i) {
                i.setSource(resource);
            } else if(viewerComponent instanceof IFrame html) {
                html.setSourceDocument(IO.getReader(streamData.getContent()).toString());
            } else {
                viewerComponent = null;
                return;
            }
            view.setCaption(caption);
        }
        if(listener == null) {
            view.execute();
        } else {
            listener.run();
        }
    }

    void view(StreamResource resource, InputStream input, String caption) {
        if(contentType == null) {
            super.setSource(resource);
            return;
        }
        caption(caption);
        if(view == null) {
            if(contentType.isPDF()) {
                view = new ContentView(viewerComponent = this);
                super.setSource(resource);
            } else if(contentType.isVideo()) {
                view = new ContentView(viewerComponent = new Video(resource));
            } else if(contentType.isAudio()) {
                view = new ContentView(viewerComponent = new Audio(resource));
            } else if(contentType.isImage()) {
                view = new ContentView(viewerComponent = new Image(resource));
            } else if(contentType.isHTML()) {
                view = new HTMLView(IO.getReader(input), isWindow());
                viewerComponent = ((HTMLView)view).getViewerComponent();
            } else {
                viewerComponent = null;
                return;
            }
        } else {
            if(contentType.isPDF()) {
                super.setSource(resource);
            } else if(viewerComponent instanceof Video v) {
                v.setSource(resource);
            } else if(viewerComponent instanceof Audio a) {
                a.setSource(resource);
            } else if(viewerComponent instanceof Image i) {
                i.setSource(resource);
            } else if(viewerComponent instanceof IFrame html) {
                html.setSourceDocument(IO.getReader(input).toString());
            } else {
                viewerComponent = null;
                return;
            }
        }
        view.setCaption(this.caption);
        if(listener == null) {
            view.execute();
        } else {
            listener.run();
        }
    }

    public void setDocument(ContentProducer contentProducer) {
        this.contentType = contentProducer;
        if (contentProducer == null) {
            setSource((String) null);
            return;
        }
        generator = new Content(contentProducer);
        generator.kick();
    }

    public Component getViewerComponent() {
        return viewerComponent == null ? this : viewerComponent;
    }

    private class Content extends ContentGenerator {

        protected Content(ContentProducer producer) {
            super(Application.get(), producer, "_", null, null, null);
            setViewer(DocumentViewer.this);
        }

        @Override
        protected void started() {
        }
    }

    private class ContentView extends Viewer implements InformationView {

        ContentView(Component component) {
            this(component, createContentLayout(component));
        }

        ContentView(Component component, Component layout) {
            super(layout == null ? component : layout, caption == null || caption.isBlank() ? "View" : caption,
                    isWindow());
            if(application.supportsCloseableView() && component instanceof HasSize hs) {
                hs.setHeight("95%");
            }
            if(layout instanceof VerticalLayout v) {
                WindowDecorator wd = new WindowDecorator(this);
                wd.getElement().getStyle().set("border-radius", "0px");
                v.add(wd, component);
                v.setMargin(false);
                v.setPadding(false);
                component.getElement().getStyle().set("margin", "0px");
            }
        }

        @Override
        protected int getViewWidth() {
            return 95;
        }

        @Override
        protected int getViewHeight() {
            return 95;
        }

        @Override
        public void clean() {
            if(generator != null) {
                generator.generated();
            }
            super.clean();
        }
    }

    private boolean isWindow() {
        return windowMode || (!application.supportsCloseableView() && !isViewer());
    }

    private Component createContentLayout(Component component) {
        if(application.supportsCloseableView() || !isViewer()) {
            return isViewer() ? component : new CenteredLayout(component);
        }
        if(isWindow()) {
            return isViewer() ? component : new CenteredLayout(component);
        }
        return new VerticalLayout();
    }

    private boolean isViewer() {
        return contentType.isPDF() || contentType.isHTML();
    }
}
