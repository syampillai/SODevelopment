package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.StreamResource;

public class Video extends com.storedobject.vaadin.Video {

    /**
     * Constructor.
     */
    public Video() {
    }

    /**
     * Constructor.
     * @param streamData Stream data from which media streamAll to be pulled
     */
    public Video(StreamData streamData) {
        super(new DBResource(streamData));
    }

    /**
     * Constructor.
     * @param streamDataId Id of the streamAll data from which media streamAll to be obtained
     */
    public Video(Id streamDataId) {
        super(new DBResource(streamDataId));
    }

    /**
     * Consructor.
     * @param resources Sources to be set
     */
    public Video(StreamResource... resources) {
        super(resources);
    }

    /**
     * Constructor.
     * @param uri URI source
     * @param type Content type of the media
     */
    public Video(String uri, String type) {
        super(uri, type);
    }

}
