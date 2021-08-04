package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.InformationView;
import com.storedobject.vaadin.*;
import com.storedobject.ui.Application;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

public class DocumentViewer extends PDFViewer {

    private final Application application;
    ContentType contentType;
    private Content generator;
    private View view;
    private String caption;

    protected DocumentViewer() {
        application = Application.get();
    }

    protected DocumentViewer(Id streamDataId) {
        this();
        setDocument(streamDataId);
    }

    protected DocumentViewer(StreamData streamData) {
        this();
        setDocument(streamData);
    }

    protected DocumentViewer(ContentProducer contentProducer) {
        this();
        if(contentProducer == null) {
            return;
        }
        setDocument(contentProducer);
    }

    protected static void view(String uri, String caption, ContentType contentType, String mime) {
        DocumentViewer dv = new DocumentViewer();
        dv.contentType = contentType;
        dv.view(uri, caption, mime);
    }

    protected void setDocument(Id streamDataId) {
        setDocument(StoredObject.get(StreamData.class, streamDataId));
    }

    protected void setDocument(StreamData streamData) {
        if (streamData == null) {
            setSource((String) null);
            return;
        }
        setDocument(new StreamDataContent(streamData));
    }

    protected void view(String caption) {
        this.caption = caption;
        if(view != null) {
            view.setCaption(caption);
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

    private void view(String resource, String mimeType, String caption) {
        if(contentType == null) {
            super.setSource(resource);
            return;
        }
        caption(caption);
        if(view == null) {
            if(contentType.isPDF()) {
                view = new ContentView(this);
                super.setSource(resource);
            } else if(contentType.isVideo()) {
                view = new ContentView(new Video(resource, mimeType));
            } else if(contentType.isAudio()) {
                view = new ContentView(new Audio(resource, mimeType));
            } else if(contentType.isImage()) {
                view = new ContentView(new Image(resource));
            } else {
                return;
            }
        }
        view.execute();
    }

    void view(StreamResource resource, String caption) {
        if(contentType == null) {
            super.setSource(resource);
            return;
        }
        caption(caption);
        if(view == null) {
            if(contentType.isPDF()) {
                view = new ContentView(this);
                super.setSource(resource);
            } else if(contentType.isVideo()) {
                view = new ContentView(new Video(resource));
            } else if(contentType.isAudio()) {
                view = new ContentView(new Audio(resource));
            } else if(contentType.isImage()) {
                view = new ContentView(new Image(resource));
            } else {
                return;
            }
        }
        view.execute();
    }

    protected void setDocument(ContentProducer contentProducer) {
        this.contentType = contentProducer;
        if (contentProducer == null) {
            setSource((String) null);
            return;
        }
        generator = new Content(contentProducer);
        generator.kick();
    }

    private class Content extends ContentGenerator {

        protected Content(ContentProducer producer) {
            super(Application.get(), producer, "_", null, null);
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
        return !application.supportsCloseableView() && !contentType.isPDF();
    }

    private Component createContentLayout(Component component) {
        if(application.supportsCloseableView() || !contentType.isPDF()) {
            return contentType.isPDF() ? component : new CenteredLayout(component);
        }
        if(isWindow()) {
            return contentType.isPDF() ? component : new CenteredLayout(component);
        }
        return new VerticalLayout();
    }
}
