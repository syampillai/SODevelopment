package com.storedobject.ui.util;

import com.storedobject.core.ContentProducer;
import com.storedobject.ui.Application;

public class MultiContentGenerator extends AbstractContentGenerator {

    public MultiContentGenerator(Application application, ContentProducer producer) {
        super(application, producer);
    }

    @Override
    public DownloadStream getContent() throws Exception {
        return null;
    }
}
