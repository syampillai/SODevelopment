package com.storedobject.ui;

import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.AbstractStreamResource;

public class ImageView extends com.storedobject.vaadin.ImageView {

    public ImageView() {
    }

    public ImageView(String url) {
        super(url);
    }

    public ImageView(StreamData streamData) {
        this(new DBResource(streamData));
    }

    public ImageView(Id streamDataId) {
        this(new DBResource(streamDataId));
    }

    public ImageView(SVG svg) {
        this(new SVGResource(svg));
    }

    public ImageView(AbstractStreamResource resource) {
        super(resource);
    }

    public ImageView(MediaFile mediaFile) {
        this(mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : "");
    }
}
