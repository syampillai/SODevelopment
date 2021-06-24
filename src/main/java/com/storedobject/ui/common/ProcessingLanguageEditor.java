package com.storedobject.ui.common;

import com.storedobject.core.ProcessingLanguage;
import com.storedobject.ui.ProcessingLanguageVisualizer;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;

public class ProcessingLanguageEditor extends AbstractTextContentEditor<ProcessingLanguage> {

    private Button run;

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

    @Override
    protected void createExtraButtons() {
        run = new Button("Play", e -> play());
    }

    private void play() {
        if(SOServlet.getMedia("ProcessingLanguage") == null) {
            warning("Processing Language engine not installed!");
            return;
        }
        ProcessingLanguage pl = getObject();
        View.createCloseableView(new ProcessingLanguageVisualizer(pl), pl.getName()).execute();
    }

    @Override
    protected void addExtraButtons() {
        if(getObject() == null) {
            return;
        }
        buttonPanel.add(run);
    }

    @Override
    protected String getLabel(String fieldName) {
        if("Content".equals(fieldName)) {
            return "Processing Language Source";
        }
        return super.getLabel(fieldName);
    }
}
