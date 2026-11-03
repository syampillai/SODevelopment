package com.storedobject.ui.common;

import com.storedobject.core.P5;
import com.storedobject.ui.P5Canvas;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;

public class P5Editor extends AbstractTextContentEditor<P5> {

    private Button run;
    private TextArea content;

    public P5Editor() {
        super(P5.class);
    }

    public P5Editor(int actions) {
        super(P5.class, actions);
    }

    public P5Editor(int actions, String caption) {
        super(P5.class, actions, caption);
    }

    public P5Editor(String className) throws Exception {
        super(className);
    }

    @Override
    protected void createExtraButtons() {
        run = new Button("Play", e -> play());
    }

    private void play() {
        P5 pl = getObject();
        if(content != null) {
            P5Canvas p5Canvas = new P5Canvas(content.getText());
            p5Canvas.setFeedbackConsumer(this::message);
            String caption = pl == null ? null : pl.getName();
            if(caption == null || caption.isBlank()) {
                caption = "P5 Test";
            }
            View.createCloseableView(p5Canvas, caption).execute();
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
