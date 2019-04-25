package com.storedobject.core;

import com.storedobject.common.Executable;
import com.storedobject.common.SORuntimeException;

import java.util.List;
import java.util.Map;

public interface AdditionalDocumentPrinter extends Executable {

    void setAdditionalDocument(AdditionalDocument document, List<StoredObject> documents, Map<String, Object> values, StoredObject entity);
    void setTemplate(StreamData format);

    default void setAdditionalDocument(AdditionalDocument additionalDocument) {
    }
}
