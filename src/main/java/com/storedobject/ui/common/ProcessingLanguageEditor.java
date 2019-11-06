package com.storedobject.ui.common;

import com.storedobject.core.ProcessingLanguage;

public class ProcessingLanguageEditor extends AbstractTextContentEditor<ProcessingLanguage> {

    public ProcessingLanguageEditor() {
        super(ProcessingLanguage.class);
    }

    public ProcessingLanguageEditor(int actions) {
        super(ProcessingLanguage.class, actions);
    }

    public ProcessingLanguageEditor(int actions, String caption) {
        super(ProcessingLanguage.class, actions, caption);
    }

    public ProcessingLanguageEditor(String className) throws Exception {
        super(className);
    }
}
