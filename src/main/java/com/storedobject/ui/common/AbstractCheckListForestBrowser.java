package com.storedobject.ui.common;

import com.storedobject.core.AbstractCheckList;
import com.storedobject.core.AbstractCheckListItem;
import com.storedobject.core.CheckListTemplate;
import com.storedobject.ui.ObjectForestBrowser;

public class AbstractCheckListForestBrowser<T extends AbstractCheckList> extends ObjectForestBrowser<T> {

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

    public void setExtraInformation(boolean required) {
    }

    public void setTemplate(CheckListTemplate template) {
    }

    public CheckListTemplate getTemplate() {
        return null;
    }

    protected void collectExtraInformation() {
    }

    protected void populate(@SuppressWarnings("unused") T checkList) {
    }

    protected void populate(@SuppressWarnings("unused") AbstractCheckListItem checkListItem, @SuppressWarnings("unused") AbstractCheckList parent) {
    }
}