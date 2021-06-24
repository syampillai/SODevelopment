package com.storedobject.ui.util;

import com.storedobject.common.Sequencer;
import com.storedobject.core.ContentProducer;
import com.storedobject.ui.Application;

import java.io.InputStream;

public class MultiContentGenerator extends AbstractContentGenerator {

    private static final Sequencer fileIdSequencer = new Sequencer();

    public MultiContentGenerator(Application application, ContentProducer producer) {
        super(application, producer, null);
        fileId = -fileIdSequencer.next();
        producer.setTransactionManager(application.getTransactionManager());
        application.addMultiContent(fileId, this);
    }

    @Override
    public DownloadStream getContent() throws Exception {
        producer.produce();
        String fileName = getFile(), ext = getExt();
        if(!fileName.endsWith(ext)) {
            fileName += ext;
        }
        String ct = getContentType();
        InputStream content;
        content = producer.getContent();
        DownloadStream ds = new DownloadStream(content, ct, fileName);
        ds.setCacheTime(0L);
        return ds;
    }
}
