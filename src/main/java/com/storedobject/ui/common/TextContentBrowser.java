package com.storedobject.ui.common;

import com.storedobject.core.TextContent;
import com.storedobject.ui.ObjectBrowser;

public class TextContentBrowser extends ObjectBrowser<TextContent> {

    public TextContentBrowser() {
        super(TextContent.class);
    }

    public TextContentBrowser(int actions) {
        super(TextContent.class, actions);
    }

    public TextContentBrowser(String className) {
        this();
    }

    public String getContent(TextContent tc) {
        String content = tc.getContent();
        if(content.length() <= 80) {
            return content;
        }
        return content.substring(0, 77) + "...";
    }
}
