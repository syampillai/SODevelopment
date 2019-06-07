package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.StreamResource;

public class Audio extends com.storedobject.vaadin.Audio {

    /**
     * Constructor.
     */
    public Audio() {
    }

    /**
     * Constructor.
     * @param streamData Stream data from which media streamAll to be pulled
     */
    public Audio(StreamData streamData) {
        super(new DBResource(streamData));
    }

    /**
     * Constructor.
     * @param streamDataId Id of the streamAll data from which media streamAll to be obtained
     */
    public Audio(Id streamDataId) {
        super(new DBResource(streamDataId));
    }

    /**
     * Consructor.
     * @param resources Sources to be set
     */
    public Audio(StreamResource... resources) {
        super(resources);
    }

    /**
     * Constructor.
     * @param uri URI source
     * @param type Content type of the media
     */
    public Audio(String uri, String type) {
        super(uri, type);
    }
}
