package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

public class AbstractCheckListForestBrowser<T extends AbstractCheckList> extends ObjectForestBrowser<T> {

    private CheckListTemplate template;
    private Button checkButton;
    private boolean extraInformation = false;

    public AbstractCheckListForestBrowser(Class<T> objectClass) {
        super(objectClass);
    }

    public AbstractCheckListForestBrowser(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractCheckListForestBrowser(Class<T> objectClass, String title) {
        super(objectClass, title);
    }

    public AbstractCheckListForestBrowser(Class<T> objectClass, int actions, String title) {
        super(objectClass, actions, title);
    }

    public AbstractCheckListForestBrowser(String className) throws Exception {
        super(className);
    }

    @Override
    public void constructed() {
        getObjectEditor(getObjectClass()).setNewObjectGenerator(new CreateCheckList());
        setExtraInformation(true);
        super.constructed();
    }

    @Override
    protected void createExtraButtons() {
        checkButton = new Button("Check", this);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(checkButton);
    }

    @Override
    public void returnedFrom(View parent) {
        if(ProcessCheckList.class.isAssignableFrom(parent.getClass())) {
            refresh();
        }
    }

    @Override
    public void clicked(Component c) {
        if(c == checkButton) {
            check();
            return;
        }
        super.clicked(c);
    }

    public void check() {
        T checkList = getRoot();
        if(checkList == null) {
            message("No data!");
            return;
        }
        new ProcessCheckList(checkList).execute(getView());
    }

    private class CreateCheckList implements NewObject<T> {

        @Override
        public T newObject() throws Exception {
            CheckListTemplate template = getTemplate();
            if(template != null) {
                if(extraInformation) {
                    collectExtraInformation();
                    return null;
                }
                T object = getObjectClass().getDeclaredConstructor().newInstance();
                AbstractCheckList.populate(object, template, getTransactionManager(), AbstractCheckListForestBrowser.this::populate, AbstractCheckListForestBrowser.this::populate);
                return object;
            } else {
                new TemplateSelector().execute();
            }
            return null;
        }
    }

    public void setExtraInformation(boolean required) {
        this.extraInformation = required;
    }

    public void setTemplate(CheckListTemplate template) {
        this.template = template;
    }

    public CheckListTemplate getTemplate() {
        return template;
    }

    private class TemplateSelector extends DataForm {

        private ObjectField<CheckListTemplate> templateField;

        public TemplateSelector() {
            super("Select Template");
        }

        @Override
        protected void buildFields() {
            templateField = new ObjectField<>("Check List Template", CheckListTemplate.class);
            addField(templateField);
        }

        @Override
        protected boolean process() {
            CheckListTemplate template = templateField.getObject();
            if(template == null) {
                return true;
            }
            setTemplate(template);
            AbstractCheckListForestBrowser.this.setCaption("Check List (" + template.getName() + ")");
            AbstractCheckListForestBrowser.this.clicked(add);
            return true;
        }
    }

    protected void collectExtraInformation() {
        setExtraInformation(false);
    }

    protected void populate(@SuppressWarnings("unused") T checkList) {
    }

    protected void populate(@SuppressWarnings("unused") AbstractCheckListItem checkListItem, @SuppressWarnings("unused") AbstractCheckList parent) {
    }
}