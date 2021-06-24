package com.storedobject.ui.tools;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.IO;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectBlockEditor extends View implements Transactional {

    private final Div layout = new Div();
    private final List<TextArea> blocks = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();
    private final List<StoredObject> methodOwners = new ArrayList<>();
    private final List<String> originalValues = new ArrayList<>();
    private final Button save;
    private final Button expand;
    private final Button collapse;
    private final Button cancel;
    protected ButtonLayout buttonPanel;

    public ObjectBlockEditor(StoredObject master, Map<String, StoredObject> details, String block, String caption) throws Exception {
        setCaption(caption);
        buttonPanel = new ButtonLayout();
        buttonPanel.sticky();
        buttonPanel.add(save = new Button("Save", this));
        buttonPanel.add(expand = new Button("Expand", this));
        expand.setVisible(false);
        buttonPanel.add(collapse = new Button("Collapse", this));
        addExtraButtons();
        buttonPanel.add(cancel = new Button("Cancel", this));
        layout.add(buttonPanel);
        BufferedReader buffer = IO.get(new StringReader(block));
        String lineO, line, blockName, colName = null;
        int onPos;
        StoredObject owner;
        StringBuilder sb = new StringBuilder();
        while((lineO = buffer.readLine()) != null) {
            line = lineO.trim();
            if(!line.startsWith("// Start of ")) {
                sb.append(lineO).append('\n');
                continue;
            }
            line = line.substring(12);
            onPos = line.indexOf(" on ");
            if(onPos > 0) {
                sb.append("/*\n");
            }
            createBlock(sb.toString(), false, null);
            if(onPos > 0) {
                colName = line.substring(onPos + 4);
                line = line.substring(0, onPos);
                owner = "*".equals(colName) ? master : details.get(colName);
            } else {
                owner = master;
            }
            methodOwners.add(owner);
            methods.add(owner.getClass().getMethod("set" + line, String.class));
            blockName = line;
            sb = new StringBuilder();
            while((lineO = buffer.readLine()) != null) {
                line = lineO.trim();
                if (!line.startsWith("// End of " + blockName)) {
                    if(onPos > 0 && ("/*".equals(line) || "*/".equals(line))) {
                        continue;
                    }
                    sb.append(lineO).append('\n');
                    continue;
                }
                lineO = sb.toString();
                createBlock(lineO, true, blockName + (owner == master ? "" : (" on " + colName)));
                originalValues.add(lineO);
                break;
            }
            sb = new StringBuilder();
            if(onPos > 0) {
                sb.append("*/\n");
            }
        }
        createBlock(sb.toString(), false, null);
        IO.close(buffer);
        setComponent(layout);
    }

    private void createBlock(String b, boolean editable, String placeholder) {
        TextArea ta = new TextArea();
        ta.setSpellCheck(false);
        if(!StringUtility.isWhite(b)) {
            ta.setValue(b);
        }
        if(!editable) {
            ta.setEnabled(false);
        }
        ta.setWidthFull();
        if(placeholder != null) {
            ta.setPlaceholder(StringUtility.makeLabel(placeholder));
        }
        layout.add(ta);
        blocks.add(ta);
    }

    public void addExtraButtons() {
    }

    public boolean canSave() {
        return true;
    }

    public boolean saveBlockText() {
        return saveBlockTextInt(null);
    }

    private boolean saveBlockTextInt(List<StoredObject> changed) {
        int m = 0;
        String v;
        for (TextArea block : blocks) {
            if (block.isEnabled()) {
                try {
                    v = block.getValue().trim();
                    if(changed != null) {
                        if(!v.equals(originalValues.get(m))) {
                            changed.add(methodOwners.get(m));
                        }
                    }
                    methods.get(m).invoke(methodOwners.get(m), v);
                    m++;
                } catch (Exception e) {
                    error(e);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void clicked(Component c) {
        if(c == cancel) {
            close();
            return;
        }
        if(c == save) {
            ArrayListSet<StoredObject> changed = new ArrayListSet<>();
            if(!canSave() || !saveBlockTextInt(changed)) {
                return;
            }
            if(changed.isEmpty()) {
                message("No changed made!");
                return;
            }
            if(transact(t -> {
                for (StoredObject storedObject : changed) {
                    storedObject.save(t);
                }
            })) {
                close();
            }
            return;
        }
        if(c == expand) {
            expand.setVisible(false);
            collapse.setVisible(true);
            for(TextArea ta: blocks) {
                if(!ta.isEnabled()) {
                    ta.setVisible(true);
                }
            }
            return;
        }
        if(c == collapse) {
            expand.setVisible(true);
            collapse.setVisible(false);
            for(TextArea ta: blocks) {
                if(!ta.isEnabled()) {
                    ta.setVisible(false);
                }
            }
        }
    }

    public String getBlockText() {
        StringBuilder sb = new StringBuilder();
        for(TextArea ta: blocks) {
            sb.append(ta.getValue().trim());
        }
        return sb.toString();
    }
}
