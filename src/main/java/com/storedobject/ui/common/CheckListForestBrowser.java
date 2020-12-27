package com.storedobject.ui.common;

import com.storedobject.core.CheckList;

public final class CheckListForestBrowser extends AbstractCheckListForestBrowser<CheckList> {

    public CheckListForestBrowser() {
        super(CheckList.class);
    }

    public CheckListForestBrowser(int actions) {
        super(CheckList.class, actions);
    }

    public CheckListForestBrowser(int actions, String title) {
        super(CheckList.class, actions, title);
    }

    public CheckListForestBrowser(String className) throws Exception {
        super(className);
    }

    @Override
    public void setExtraInformation(boolean required) {
        super.setExtraInformation(false);
    }
}