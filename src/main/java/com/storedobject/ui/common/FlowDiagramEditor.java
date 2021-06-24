package com.storedobject.ui.common;

import com.storedobject.core.TextContent;
import com.storedobject.ui.FlowDiagram;
import com.storedobject.ui.TextView;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Consumer;

public class FlowDiagramEditor extends TextContentEditor {

    private Button run1, run2, debug;
    private HasValue<?, String> nameField, contentField;
    private FlowDiagram flowDiagram;
    private View view;

    public FlowDiagramEditor() {
        super();
    }

    public FlowDiagramEditor(int actions) {
        super(actions);
    }

    public FlowDiagramEditor(int actions, String caption) {
        super(actions, caption);
    }

    public FlowDiagramEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected void formConstructed() {
        super.formConstructed();
        setCaption("Flow Diagram Editor");
    }

    @Override
    protected void createExtraButtons() {
        run1 = new Button("Draw", VaadinIcon.FILE_TREE, e -> draw1());
        run2 = new Button("Draw", VaadinIcon.FILE_TREE, e -> draw2());
        debug = new Button("Debug", VaadinIcon.BUG, e -> debug());
    }

    private void view(String caption) {
        caption = caption.trim();
        if(caption.isEmpty()) {
            caption = "Flow Diagram";
        }
        if(view == null) {
            flowDiagram = new FlowDiagram();
            view = createCloseableView(flowDiagram, caption);
        } else {
            view.setCaption(caption);
        }
        flowDiagram.clearCommands();
        view.execute();
    }

    private void draw1() {
        TextContent tc = getObject();
        view(tc.getName());
        flowDiagram.draw(tc.getContent());
    }

    private void draw2() {
        view(nameField.getValue());
        flowDiagram.draw(contentField.getValue());
    }

    private void debug() {
        view(nameField.getValue());
        flowDiagram.debug(contentField.getValue(), new Debugger());
    }

    @Override
    protected void addExtraButtons() {
        if(getObject() == null) {
            return;
        }
        buttonPanel.add(run1);
    }

    @Override
    protected void addExtraEditingButtons() {
        super.addExtraEditingButtons();
        buttonPanel.add(run2, debug);
    }

    @Override
    protected String getLabel(String fieldName) {
        if("Content".equals(fieldName)) {
            return "Flow Diagram Source";
        }
        return super.getLabel(fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("Content".equals(fieldName)) {
            contentField = (HasValue<?, String>) field;
        } else if("Name".equals(fieldName)) {
            nameField = (HasValue<?, String>) field;
        }
        super.customizeField(fieldName, field);
    }

    private class Debugger implements Consumer<String> {

        @Override
        public void accept(String error) {
            if(error == null) {
                message("No errors found!");
            } else {
                TextView textView = new TextView("Error");
                textView.append(error);
                textView.execute();
            }
        }
    }
}