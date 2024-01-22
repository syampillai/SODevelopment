package com.storedobject.ui.tools;

import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextArea;
import jdk.jshell.JShell;

public class CodeShell extends DataForm {

    private final TextArea code = new TextArea("Code Shell");
    private final JShell shell;

    public CodeShell() {
        super("Code Shell", false);
        addField(code);
        shell = JShell.create();
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Evaluate");
    }

    @Override
    protected boolean process() {
        String code = this.code.getValue();
        if(!code.isBlank()) {
            //code = "double eval() { double r = 0;" + code + "return r;}";
            shell.eval(code);
            //shell.eval("double r = eval();");
            System.err.println(shell.snippets().count());
            shell.variables().forEach(v -> message(v.name() + " = " + shell.varValue(v)));
        }
        return false;
    }
}
