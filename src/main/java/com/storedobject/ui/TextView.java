package com.storedobject.ui;

import com.storedobject.common.Processor;
import com.storedobject.core.ApplicationServer;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;

public class TextView extends View implements CloseableView, Transactional, StyledBuilder {

    private final ELabels label = new ELabels();
    private Processor processor;
    private Application.BusyIndicator progress;
    private boolean indeterminate = true;
    private final Div content = new Div();
    private Component topComponent;
    private Application application;
    private boolean componentSet;
    private int lineCount = 0;

    public TextView(String caption) {
        setCaption(caption);
    }

    @Override
    protected void initUI() {
        super.initUI();
        topComponent= getTopComponent();
        if(topComponent != null) {
            content.add(topComponent);
        }
        content.add(label);
        Component bottomComponent = getBottomComponent();
        if(bottomComponent != null) {
            content.add(bottomComponent);
        }
        if(!componentSet) {
            componentSet = true;
            setComponent(content);
        }
    }

    protected Component getTopComponent() {
        return null;
    }

    protected Component getBottomComponent() {
        return null;
    }

    public void add(Component... components) {
        if(topComponent instanceof HasComponents) {
            ((HasComponents) topComponent).add(components);
        } else {
            content.add(components);
        }
    }

    public void remove(Component... components) {
        if(topComponent instanceof HasComponents) {
            ((HasComponents) topComponent).remove(components);
        } else {
            content.remove(components);
        }
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setValue(Object object) {
        setText(object == null ? "" : object.toString());
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return label;
    }

    @Override
    protected void execute(com.storedobject.vaadin.View parent, boolean doNotLock) {
        getApplication().startPolling(this);
        getApplication().getUI().access(() -> {
            label.update();
            super.execute(parent, doNotLock);
        });
        startProcess();
    }

    protected void startProcess() {
        Thread.startVirtualThread(() -> {
            getProgress();
            readyForProcessing();
            try {
                process();
            } catch(Throwable error) {
                ApplicationServer.log(getApplication(), error);
                getApplication().getUI().access(() -> error(error));
            }
            getApplication().access(() -> {
                getApplication().stopPolling(this);
                closeProgress();
            });
        });
    }

    protected void readyForProcessing() {
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    private synchronized void closeProgress() {
        if(progress != null) {
            progress.close();
            progress = null;
        }
        getApplication().stopPolling(this);
    }

    @Override
    public void clean() {
        closeProgress();
        super.clean();
    }

    public void process() throws Throwable {
        if(processor != null) {
            getApplication().getUI().access(() -> getApplication().startPolling(this));
            processor.process();
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public void setProgress(int progress) {
        if(getApplication() == null) {
            getProgress().setValue(progress / 100.0);
        } else {
            getApplication().access(() -> getProgress().setValue(progress / 100.0));
        }
    }

    public void setProgressCaption(String caption) {
        if(getApplication() == null) {
            getProgress().setCaption(caption);
        } else {
            getApplication().access(() -> getProgress().setCaption(caption));
        }
    }

    public String getProgressCaption() {
        return null;
    }

    private Application.BusyIndicator getProgress() {
        if(progress == null) {
            progress = getApplication().getProgressBar(indeterminate);
            String m = getProgressCaption();
            if(m != null) {
                progress.setCaption(m);
            }
        }
        return progress;
    }

    @Override
    public void setCaption(String caption) {
        if(getApplication() == null) {
            super.setCaption(caption);
        } else {
            getApplication().access(() -> super.setCaption(caption));
        }
    }

    @Override
    public void message(Object message) {
        blackMessage(getApplication().getEnvironment().toDisplay(message));
    }

    public void message(Object... messages) {
        message(m(messages));
    }

    @Override
    public void warning(Object message) {
        blackMessage(getApplication().getEnvironment().toDisplay(message));
    }

    public void warning(Object... messages) {
        warning(m(messages));
    }

    @Override
    public void error(Object message) {
        redMessage(getApplication().getEnvironment().toDisplay(message));
    }

    public void error(Object... messages) {
        error(m(messages));
    }

    private StringBuilder m(Object... messages) {
        StringBuilder s = new StringBuilder();
        for(Object m: messages) {
            s.append(getApplication().getEnvironment().toDisplay(m));
        }
        return s;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        if(application == null) {
            application = super.getApplication();
        }
        return application;
    }

    public void setApplication(Application application) {
        if(this.application == null) {
            this.application = application;
        }
    }

    @Override
    public void close() {
        getApplication().getUI().access(super::close);
    }

    @Override
    public void abort() {
        getApplication().getUI().access(super::abort);
    }

    @Override
    public boolean isNewLine() {
        return label.isNewLine();
    }

    @Override
    public void decorateComponent() {
        super.decorateComponent();
    }

    public void popup() {
        Application a = getApplication();
        if(a == null) {
            pop();
        } else {
            a.access(this::pop);
        }
    }

    private void pop() {
        if(!componentSet) {
            componentSet = true;
            initUI();
            setComponent(new Window(new WindowDecorator(this), new Box(content)));
        }
        execute();
    }

    @Override
    public StyledBuilder clearContent() {
        lineCount = 0;
        return StyledBuilder.super.clearContent().update();
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        if(++lineCount > 500) {
            clearContent().update();
        }
        return StyledBuilder.super.newLine(force);
    }
}