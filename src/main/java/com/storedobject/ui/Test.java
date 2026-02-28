package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.*;

public class Test extends DataForm {

    TextField icon, category;
    FontIcon fi;

    public Test() {
        super("Test");
        fi = new FontIcon("frog");
        icon = new TextField("Icon");
        category = new TextField("Category");
        addField(icon, category);
        setRequired(icon, category);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.add(fi);
    }

    @Override
    protected boolean process() {
        fi.set(icon.getValue(), category.getValue());
        return false;
    }
}
