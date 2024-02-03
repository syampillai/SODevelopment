package com.storedobject.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Div;

public class ELabels extends Div implements StyledBuilder, HasText {

    private Application application;
    private ELabel last;

    public ELabels() {
        this(null);
    }

    public ELabels(String text) {
        this(text, new String[0]);
    }

    public ELabels(Object object, String... style) {
        setValue(object, style);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        application = Application.get();
    }

    @Override
    public Application getApplication() {
        if(application == null) {
            application = Application.get();
        }
        return application;
    }

    @Override
    public void setText(String text) {
        setValue(text);
    }

    @Override
    public String getText() {
        return getValue();
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        if(force || !isNewLine()) {
            add(new ELabel(""));
        }
        return this;
    }

    @Override
    public boolean isNewLine() {
        return last == null;
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    public void setValue(Object object, String... style) {
        clear();
        if(object == null) {
            return;
        }
        add(new ELabel(object, style));
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public StyledBuilder update() {
        if(last != null) {
            if (getApplication() == null) {
                last.update();
            } else {
                application.access(() -> last.update());
            }
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getChildren().findAny().isPresent();
    }

    @Override
    public StyledBuilder append(Object anything) {
        if(last == null) {
            setValue(anything);
        } else {
            last.append(anything);
        }
        return this;
    }

    @Override
    public StyledBuilder append(Object anything, String color) {
        if(last == null) {
            setValue(anything, color);
        } else {
            last.append(anything, color);
        }
        return this;
    }

    @Override
    public StyledBuilder append(Object anything, String... style) {
        if(last == null) {
            setValue(anything, style);
        } else {
            last.append(anything, style);
        }
        return this;
    }

    @Override
    public StyledBuilder appendHTML(String html) {
        if(last == null) {
            setValue("");
        }
        last.appendHTML(html);
        return this;
    }

    @Override
    public void clear() {
        removeAll();
    }

    @Override
    public StyledBuilder clearContent() {
        clear();
        return this;
    }

    @Override
    public StyledBuilder space(int count) {
        return this;
    }

    @Override
    public StyledBuilder drawLine() {
        if(last == null) {
            setValue("");
        }
        last.drawLine();
        return this;
    }

    private void add(ELabel label) {
        if(getApplication() == null) {
            if(last != null) {
                last.update();
            }
            label.update();
        } else {
            application.access(() -> {
                if(last != null) {
                    last.update();
                }
                label.update();
            });
        }
        add(new Div(label));
        last = label;
    }

    @Override
    public void add(Component... components) {
        if(getApplication() == null) {
            super.add(components);
        } else {
            application.access(() -> super.add(components));
        }
    }

    @Override
    public void removeAll() {
        if(getApplication() == null) {
            super.removeAll();
        } else {
            application.access(super::removeAll);
        }
        last = null;
    }

    @Override
    public void remove(Component... components) {
        if(getApplication() == null) {
            super.remove(components);
        } else {
            application.access(() -> super.remove(components));
        }
        for(Component c: components) {
            if(c == last) {
                last = null;
                return;
            }
        }
    }
}
