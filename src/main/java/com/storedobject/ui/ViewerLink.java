package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.MediaFile;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Clickable;
import com.vaadin.flow.component.html.Span;

/**
 * A link that can be clicked to view some sort of content.
 *
 * @author Syam
 */
public class ViewerLink extends Span {

    private TextContent tc;
    private MediaFile mf;
    private FileData fd;
    private String caption;

    /**
     * Constructor.
     */
    public ViewerLink() {
        this("Content");
    }


    /**
     * Constructor.
     *
     * @param text Text to be shown as the link.
     */
    public ViewerLink(String text) {
        super(text);
        init();
    }


    /**
     * Constructor.
     *
     * @param text Text to be shown as the link.
     * @param tc Text content.
     */
    public ViewerLink(String text, TextContent tc) {
        super(text);
        this.tc = tc;
        init();
    }


    /**
     * Constructor.
     *
     * @param text Text to be shown as the link.
     * @param mf Media file.
     */
    public ViewerLink(String text, MediaFile mf) {
        super(text);
        this.mf = mf;
        init();
    }

    /**
     * Constructor.
     *
     * @param text Text to be shown as the link.
     * @param fd File data.
     */
    public ViewerLink(String text, FileData fd) {
        super(text);
        this.fd = fd;
        init();
    }

    private void init() {
        new Clickable<>(this, e -> view());
        setClassName("link");
    }

    /**
     * Set the caption to be shown when the content is viewed.
     *
     * @param caption Caption.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get the caption.
     *
     * @return Caption.
     */
    public final String getCaption() {
        return caption;
    }

    /**
     * Set the text content.
     *
     * @param tc Text content.
     */
    public void setTextContent(TextContent tc) {
        if(tc == null) {
            this.tc = null;
        } else {
            this.tc = tc;
            fd = null;
            mf = null;
        }
    }

    /**
     * Set the text content.
     *
     * @param textContentName Text content name.
     */
    public void setTextContent(String textContentName) {
        if(textContentName == null) {
            this.tc = null;
        } else {
            this.tc = SOServlet.getTextContent(textContentName);
            fd = null;
            mf = null;
        }
    }

    /**
     * Set the media content.
     *
     * @param mf Media content.
     */
    public void setMediaFile(MediaFile mf) {
        if(mf == null) {
            this.mf = null;
        } else {
            this.mf = mf;
            tc = null;
            fd = null;
        }
    }

    /**
     * Set the media content.
     *
     * @param mediaFileName Media file name.
     */
    public void setMediaFile(String mediaFileName) {
        if(mediaFileName == null) {
            this.mf = null;
        } else {
            this.mf = SOServlet.getMedia(mediaFileName);
            tc = null;
            fd = null;
        }
    }

    /**
     * Set the content from file data.
     *
     * @param fd File data.
     */
    public void setFileData(FileData fd) {
        if(fd == null) {
            this.fd = null;
        } else {
            this.fd = fd;
            tc = null;
            mf = null;
        }
    }

    /**
     * Set the content from file data.
     *
     * @param path Path of the file data.
     */
    public void setFileData(String path) {
        if(path == null) {
            this.fd = null;
        } else {
            this.fd = FileData.get(path);
            tc = null;
            mf = null;
        }
    }

    private String c(String c, boolean strip) {
        if(caption != null && !caption.isBlank()) {
            return caption;
        }
        if(strip && c.contains("/")) {
            c = c.substring(c.lastIndexOf('/') + 1);
        }
        return c;
    }

    /**
     * View the current content. This will be invoked automatically when the user clicks on the text of the link.
     */
    public void view() {
        Application a = Application.get();
        if(a == null) {
            return;
        }
        if(mf != null) {
            a.view(c(mf.getName(), false), mf);
            return;
        }
        if(fd != null) {
            a.view(c(fd.getName(), true), fd);
            return;
        }
        if(tc != null) {
            new HTMLView(tc, true).execute();
        }
    }

    public void setContrast() {
        getClassNames().add("contrast");
    }
}
