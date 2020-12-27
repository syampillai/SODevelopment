package com.storedobject.ui.common;

import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;

public class TextContentEditor extends AbstractTextContentEditor<TextContent> {
    
    public TextContentEditor() {
        super(TextContent.class);
    }

    public TextContentEditor(int actions) {
        super(TextContent.class, actions);
    }

    public TextContentEditor(int actions, String caption) {
        super(TextContent.class, actions, caption);
    }

    public TextContentEditor(String className) throws Exception {
        super(className);
    }

    @Override
    public void saved(TextContent object) {
        SOServlet.removeCache(object);
    }
}