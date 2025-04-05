package com.storedobject.ui.common;

import com.storedobject.core.StringUtility;
import com.storedobject.ui.TemplateComponent;
import com.storedobject.ui.TemplateView;
import com.storedobject.ui.UploadProcessorView;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;

import java.io.InputStream;
import java.util.function.Consumer;

public class TemplateEditor extends TextContentEditor {

    private final Button loadHTML = new Button("Paste HTML File", "", e -> loadHTML()),
            loadCSS = new Button("Paste CSS File", "", e -> loadHCSS()),
            runAsView = new Button("Test as View", "", e -> runAsView()),
            runAsComponent = new Button("Test as Component", "", e -> runAsComponent());
    private TextArea content;

    @Override
    protected void addExtraButtons() {
        if(getObject() != null) {
            buttonPanel.add(runAsView, runAsComponent);
        }
    }

    @Override
    protected void addExtraEditingButtons() {
        if(content != null) {
            buttonPanel.add(loadHTML, loadCSS, runAsView, runAsComponent);
        }
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("Content".equals(fieldName) && field instanceof TextArea ta) {
            this.content = ta;
        }
        super.customizeField(fieldName, field);
    }

    private void runAsView() {
        new RunAsView(content.getValue()).execute(this);
    }

    private void runAsComponent() {
        new RunAsComponent(content.getValue()).execute(this);
    }

    private void loadHTML() {
        new Upload("HTML", this::processHTML).execute(this);
    }

    private void loadHCSS() {
        new Upload("CSS", this::processCSS).execute(this);
    }

    private void processHTML(String html) {
        int headStart = html.indexOf("<head>");
        int headEnd = html.indexOf("</head>");
        if (headStart >= 0 && headEnd > headStart) {
            html = html.substring(0, headStart) + html.substring(headEnd + 7);
        }
        html = html.replaceAll("(?i)<!DOCTYPE html>|<html(\\s[^>]*)?>|</html>", "").strip();
        html = html.replaceAll("(?i)<html(\\s[^>]*)?>|</html>", "").strip();
        int bodyStart = html.indexOf("<body>"), bodyEnd = html.indexOf("</body>");
        if (bodyStart >= 0 && bodyEnd > bodyStart) {
            String bodyContent = html.substring(bodyStart + 6, bodyEnd).strip();
            if (bodyContent.matches("^<[^>]+>.*</[^>]+>$")) {
                html = bodyContent;
            }
        }
        String s = content.getValue().strip();
        int p1 = s.indexOf("<style>"), p2 = s.lastIndexOf("</style>");
        String style = (p1 >= 0 && p2 >= 0) ? s.substring(p1, p2 + 8) : "";
        content.setValue(style + "\n" + html);
    }

    private void processCSS(String css) {
        String s = content.getValue().strip();
        int p1 = s.indexOf("<style>"), p2 = s.lastIndexOf("</style>");
        if(p1 < 0 || p2 < 0) {
            content.setValue("<style>\n" + css + "\n</style>\n" + s);
        } else {
            content.setValue(s.substring(0, p1) + "<style>\n" + css + "\n</style>\n" + s.substring(p2 + 8));
        }
    }

    private static class Upload extends UploadProcessorView {

        private final Consumer<String> consumer;

        public Upload(String caption, Consumer<String> consumer) {
            super("Upload " + caption + " content");
            getUploadComponent().setAcceptedFileTypes("text/" + caption.toLowerCase());
            this.consumer = consumer;
        }

        @Override
        protected void process(InputStream stream, String mimeType) {
            try {
                String content = StringUtility.toString(stream);
                close();
                getApplication().access(() -> consumer.accept(content));
            } catch (Exception e) {
                error(e);
            }
        }
    }

    private static class RunAsView extends TemplateView implements CloseableView {

        RunAsView(String content) {
            super(() -> content);
            setCaption("Test as View");
        }
    }

    private static class RunAsComponent extends View implements CloseableView {

        public RunAsComponent(String content) {
            super("Test as Component");
            setComponent(new TemplateComponent(content));
        }
    }
}
