package com.storedobject.ui;

import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.AbstractStreamResource;

public class Image extends com.vaadin.flow.component.html.Image {

    public Image(String url) {
        super();
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
        super();
    }

    public void setSource(String source) {
    }

    @Override
    public void setSrc(String src) {
        setSource(src);
    }

    public void setSource(AbstractStreamResource source) {
    }

    @Override
    public void setSrc(AbstractStreamResource source) {
        setSource(source);
    }
}