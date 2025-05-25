package com.storedobject.ui;

import com.storedobject.core.HasStreamData;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.ui.util.SOServlet;
import com.vaadin.flow.server.StreamResource;

public class Video extends com.storedobject.vaadin.Video {

    /**
     * Constructor.
     */
    public Video() {
    }

    /**
     * Constructor.
     *
     * @param streamData Stream data from which media streamAll to be pulled
     */
    public Video(HasStreamData streamData) {
        super(new DBResource(streamData));
    }

    /**
     * Constructor.
     *
     * @param streamDataId Id of the streamAll data from which media streamAll to be obtained
     */
    public Video(Id streamDataId) {
        super(new DBResource(streamDataId));
    }

    /**
     * Constructor.
     *
     * @param resources Sources to be set
     */
    public Video(StreamResource... resources) {
        super(resources);
    }

    /**
     * Constructor.
     *
     * @param uri URI source
     * @param type Content type of the media
     */
    public Video(String uri, String type) {
        super(uri, type);
    }

    /**
     * Constructor.
     *
     * @param mediaFile Media file containing the video
     */
    public Video(MediaFile mediaFile) {
        this(resource(mediaFile), mediaFile != null && mediaFile.isVideo() ? mediaFile.getMimeType() : "");
    }

    /**
     * Create a video from the media file.
     *
     * @param names Names of the media file. The first one found is returned.
     * @return Video if exists.
     */
    public static Video createFromMedia(String... names) {
        MediaFile mf = SOServlet.getVideo(names);
        return mf == null ? null : new Video(mf);
    }


    private static String resource(MediaFile mediaFile) {
        return mediaFile != null && mediaFile.isVideo() ? ("media/" + mediaFile.getFileName()) : "";
    }

    public void setSource(MediaFile mediaFile) {
        if(mediaFile == null || !mediaFile.isVideo()) {
            return;
        }
        super.setSource(resource(mediaFile), mediaFile.getMimeType());
    }

}
