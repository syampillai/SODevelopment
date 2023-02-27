package com.storedobject.ui.common;

import com.storedobject.core.ApplicationModule;
import com.storedobject.core.ModuleLogic;
import com.storedobject.core.StoredObject;
import com.storedobject.core.Transaction;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class ApplicationModuleEditor extends ObjectEditor<ApplicationModule> {

    private final ConfirmButton replicate = new ConfirmButton("Replicate", VaadinIcon.COPY_O,e -> replicate());

    public ApplicationModuleEditor() {
        super(ApplicationModule.class);
    }

    public ApplicationModuleEditor(int actions) {
        super(ApplicationModule.class, actions);
    }

    public ApplicationModuleEditor(int actions, String caption) {
        super(ApplicationModule.class, actions, caption);
    }

    public ApplicationModuleEditor(String className) throws Exception {
        this();
    }

    @Override
    protected void addExtraButtons() {
        if(getObject() != null) {
            buttonPanel.add(replicate);
        }
    }

    private void replicate() {
        ApplicationModule am = getObject();
        List<ModuleLogic> logicList = am.listLinks(ModuleLogic.class).toList();
        am.makeNew();
        am.setName(am.getName() + " NEW");
        Transaction t = null;
        try {
            t = getTransactionManager().createTransaction();
            am.save(t);
            addLinks(t, am, logicList);
            t.commit();
            setObject(am);
        } catch(Exception e) {
            if(t != null) {
                t.rollback();
            }
            error(e);
        }
    }

    private void addLinks(Transaction t, StoredObject parent, List<ModuleLogic> logicList) throws Exception {
        if(logicList.isEmpty()) {
            return;
        }
        List<ModuleLogic> list;
        for(ModuleLogic logic: logicList) {
            list = logic.listLinks(ModuleLogic.class).toList();
            logic.makeNew();
            logic.save(t);
            parent.addLink(t, logic);
            addLinks(t, logic, list);
        }
    }
}
