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
import com.vaadin.flow.shared.Registration;

import java.io.InputStream;
import java.io.Reader;

public class DocumentViewer extends PDFViewer {

    private final Application application;
    ContentType contentType;
    private Content generator;
    private View view;
    private String caption;
    private Component viewerComponent;
    private final Runnable listener;
    private boolean windowMode = false;
    private Component[] extraButtons;
    private Runnable onViewClose;
    private Registration viewClosedRegister;
    private final Runnable onClose = () -> {
        if(onViewClose != null) {
            onViewClose.run();
            onViewClose = null; // Make sure that the onViewClose is not executed again
        }
        if(viewClosedRegister != null) {
            viewClosedRegister.remove();
            viewClosedRegister = null;
        }
    };

    /**
     * Constructor.
     * @param listener Listener to be notified when the source is set.
     */
    public DocumentViewer(Runnable listener) {
        this.listener = listener;
        application = Application.get();
        viewerComponent = this;
    }

    /**
     * View a media file.
     * @param caption Caption.
     * @param mediaFile Media file.
     * @param windowMode Whether to open in a window or not.
     */
    public static void view(String caption, MediaFile mediaFile, boolean windowMode) {
        view(caption, mediaFile, windowMode, null);
    }

    /**
     * View a media file.
     * @param caption Caption.
     * @param mediaFile Media file.
     * @param windowMode Whether to open in a window or not.
     * @param extraButtons Extra buttons to be added to the view.
     */
    public static void view(String caption, MediaFile mediaFile, boolean windowMode, Component[] extraButtons) {
        view(caption, mediaFile, windowMode, null, extraButtons);
    }

    /**
     * View a media file.
     * @param caption Caption.
     * @param mediaFile Media file.
     * @param windowMode Whether to open in a window or not.
     * @param onViewClose Runnable to be executed when the view is closed.
     * @param extraButtons Extra buttons to be added to the view.
     */
    public static void view(String caption, MediaFile mediaFile, boolean windowMode, Runnable onViewClose,
                            Component[] extraButtons) {
        if(mediaFile == null) {
            return;
        }
        if(caption == null) {
            caption = mediaFile.getFileName();
        }
        DocumentViewer dv = new DocumentViewer(null);
        dv.setWindowMode(windowMode);
        dv.setExtraButtons(extraButtons);
        dv.contentType = mediaFile;
        dv.onViewClose = onViewClose;
        dv.view("media/" + mediaFile.getFileName(), mediaFile.getFile(), caption);
    }

    @Override
    public void setSource(String fileURL) {
        super.setSource(fileURL);
        if(listener != null) {
            listener.run();
        }
    }

    /**
     * Set the window mode.
     * @param windowMode True for window mode.
     */
    public void setWindowMode(boolean windowMode) {
        this.windowMode = windowMode;
    }

    /**
     * Set extra buttons to be added to the view.
     * @param extraButtons Extra buttons.
     */
    public void setExtraButtons(Component... extraButtons) {
        this.extraButtons = extraButtons;
    }

    /**
     * Set a runnable to be executed when the view is closed.
     * @param onViewClose Runnable.
     */
    public void setOnViewClose(Runnable onViewClose) {
        this.onViewClose = onViewClose;
    }

    /**
     * Set the document to be viewed.
     * @param streamDataId ID of the stream data.
     */
    public void setDocument(Id streamDataId) {
        setDocument(StoredObject.get(StreamData.class, streamDataId));
    }

    /**
     * Set the document to be viewed.
     * @param streamData Stream data.
     */
    public void setDocument(StreamData streamData) {
        if (streamData == null) {
            setSource((String) null);
            return;
        }
        setDocument(new StreamDataContent(streamData));
    }

    /**
     * View the document.
     * @param caption Caption.
     */
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
        run();
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
                view = new HTMLViewer(IO.getReader(input), isWindow());
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
        run();
    }

    private void run() {
        if(viewClosedRegister != null) {
            viewClosedRegister.remove(); // Old registration, if any, removed
            viewClosedRegister = null;
        }
        if(onViewClose != null) {
            // Add close listener
            viewClosedRegister = view.addClosedListener(e -> onClose.run());
        }
        if(listener == null) {
            view.execute();
        } else {
            listener.run();
        }
    }

    /**
     * Set the document to be viewed.
     * @param contentProducer Content producer.
     */
    public void setDocument(ContentProducer contentProducer) {
        this.contentType = contentProducer;
        if (contentProducer == null) {
            setSource((String) null);
            return;
        }
        generator = new Content(contentProducer);
        generator.kick();
    }

    /**
     * Get the viewer component.
     * @return Viewer component.
     */
    public Component getViewerComponent() {
        return viewerComponent == null ? this : viewerComponent;
    }

    private class Content extends ContentGenerator {

        protected Content(ContentProducer producer) {
            super(Application.get(), producer, "_", null, null, null,
                    view != null && view.isWindowMode(), onViewClose, extraButtons);
            setViewer(DocumentViewer.this);
        }

        @Override
        protected void started() {
        }
    }

    private class HTMLViewer extends HTMLView implements InformationView {

        HTMLViewer(Reader htmlContent, boolean windowMode) {
            super(htmlContent, windowMode);
        }

        @Override
        protected WindowDecorator createWindowDecorator() {
            return new WindowDecorator(this, extraButtons);
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
                WindowDecorator wd = new WindowDecorator(this, extraButtons);
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

        @Override
        protected WindowDecorator createWindowDecorator() {
            return new WindowDecorator(this, extraButtons);
        }
    }

    private boolean isWindow() {
        return (extraButtons != null && extraButtons.length > 0) || windowMode
                || (!application.supportsCloseableView() && !isViewer());
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