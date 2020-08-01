package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;

import java.util.function.Consumer;

public class FlowDiagram extends Component implements HasSize {

    public FlowDiagram() {
        this(null, null);
    }

    public FlowDiagram(int width, int height) {
        this(width + "px", height + "px");
    }

    public FlowDiagram(String width, String height) {
        this(null, width, height);
    }

    public FlowDiagram(TextContent definition) {
        this(definition, null, null);
    }

    private FlowDiagram(TextContent source, String width, String height) {
    }

    public void draw() {
    }

    public void draw(TextContent definition) {
    }

    public void draw(String... comands) {
    }

    public void clear() {
    }

    public void command(String... commands) {
    }

    public void debug(String command, Consumer<String> failedCommandConsumer) {
    }

    public void clearCommands() {
    }
}