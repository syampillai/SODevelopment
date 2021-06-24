package com.storedobject.ui;

import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.Signature;
import com.storedobject.core.StreamData;
import com.storedobject.ui.util.SOServlet;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResourceRegistry;

/**
 * Use this class instead of {@link com.storedobject.chart.Image} because it supports images from
 * {@link com.storedobject.core.MediaFile} and other resources.
 *
 * @author Syam
 */
public class ChartImage extends com.storedobject.chart.Image {

    public ChartImage(String imageURL) {
        super(null);
        setImageURL(imageURL);
    }

    public ChartImage(StreamData streamData) {
        this(new DBResource(streamData));
    }

    public ChartImage(Id streamDataId) {
        this(new DBResource(streamDataId));
    }

    public ChartImage(SVG svg) {
        this(new SVGResource(svg));
    }

    public ChartImage(AbstractStreamResource resource) {
        super(null);
        setImageURL(resource);
    }

    public ChartImage(MediaFile mediaFile) {
        this(resource(mediaFile));
    }

    public ChartImage(Signature signature) {
        this(signature.getSignature());
    }

    private static String resource(MediaFile mediaFile) {
        return mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : null;
    }

    /**
     * Create a chart image from the media file.
     *
     * @param name Name of the media file
     * @return Image if exists.
     */
    public static ChartImage createFromMedia(String name) {
        MediaFile mf = SOServlet.getImage(name);
        return mf == null ? null : new ChartImage(mf);
    }

    @Override
    public void setImageURL(String imageURL) {
        super.setImageURL(imageURL == null ? null : MediaCSS.parse(imageURL));
    }

    public void setImageURL(AbstractStreamResource resource) {
        super.setImageURL(resource == null ? null : StreamResourceRegistry.getURI(resource).toASCIIString());
    }

    public void setImageURL(MediaFile mediaFile) {
        super.setImageURL(resource(mediaFile));
    }

    public void setImageURL(Signature signature) {
        super.setImageURL(signature.getSignature());
    }
}
