package com.storedobject.ui;

import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.AbstractStreamResource;

public class Image extends com.storedobject.vaadin.Image {

    public Image(String url) {
        super(url);
    }

    public Image(StreamData streamData) {
        this(new DBResource(streamData));
    }

    public Image(Id streamDataId) {
        this(new DBResource(streamDataId));
    }

    public Image(SVG svg) {
        this(new SVGResource(svg));
    }

    public Image(AbstractStreamResource resource) {
        super(resource);
    }

    public Image(MediaFile mediaFile) {
        this("");
    }

    /**
     * Create an image from the media file.
     *
     * @param name Name of the media file
     * @return Image if exists.
     */
    public static Image createFromMedia(String name) {
        return null;
    }
}