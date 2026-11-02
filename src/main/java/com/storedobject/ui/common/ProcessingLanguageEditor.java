package com.storedobject.ui.common;

import com.storedobject.core.ProcessingLanguage;
import com.storedobject.ui.P5;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;

public class ProcessingLanguageEditor extends AbstractTextContentEditor<ProcessingLanguage> {

    private Button run;
    private TextArea content;

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
        ProcessingLanguage pl = getObject();
        if(content != null) {
            P5 p5 = new P5(content.getText());
            p5.setFeedbackConsumer(this::message);
            String caption = pl == null ? null : pl.getName();
            if(caption == null || caption.isBlank()) {
                caption = "P5 Test";
            }
            View.createCloseableView(p5, caption).execute();
        }
    }

    @Override
    protected void addExtraButtons() {
        if(getObject() == null) {
            return;
        }
        buttonPanel.add(run);
    }

    @Override
    protected void addExtraEditingButtons() {
        if(content != null) {
            buttonPanel.add(run);
        }
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("Content".equals(fieldName) && field instanceof TextArea ta) {
            this.content = ta;
        }
        super.customizeField(fieldName, field);
    }

    @Override
    protected String getLabel(String fieldName) {
        if("Content".equals(fieldName)) {
            return "Processing Language Source";
        }
        return super.getLabel(fieldName);
    }
}
