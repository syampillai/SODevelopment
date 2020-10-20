package com.storedobject.ui;

import com.storedobject.common.Processor;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

public class TextView extends View implements CloseableView, Transactional, StyledBuilder {

    public TextView(String caption) {
        setCaption(caption);
    }

    protected Component getTopComponent() {
        return null;
    }

    protected Component getBottomComponent() {
        return null;
    }

    public void add(Component... components) {
    }

    public void remove(Component... components) {
    }

    public void setText(String text) {
    }

    public void setValue(Object object) {
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    protected void execute(com.storedobject.vaadin.View parent, boolean doNotLock) {
    }

    protected void startProcess() {
    }

    protected void readyForProcessing() {
    }

    public void setProcessor(Processor processor) {
    }

    public void process() throws Throwable {
    }

    public void setIndeterminate(boolean indeterminate) {
    }

    public void setProgress(int progress) {
    }

    public void setProgressCaption(String caption) {
    }

    public String getProgressCaption() {
        return null;
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
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return super.getApplication();
    }

    public void setApplication(Application application) {
    }

    @Override
    public void close() {
        getApplication().access(super::close);
    }

    @Override
    public void abort() {
        getApplication().access(super::abort);
    }

    @Override
    public boolean isNewLine() {
        return false;
    }
}