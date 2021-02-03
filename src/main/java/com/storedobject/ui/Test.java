package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.textfield.NumberField;

public class Test extends DataForm {

    public Test() {
        super("Signature");
        addField(new TextField("Hello"));
        ELabel e = new ELabel("Test");
        e.append("One pieces and one aksk ak ksl klklk klak lklkdaaas lk;lk;l", "red").
                newLine().
                append("Another sd jhjhk hkjhjhjhj ahjasdas jhhdah asdhdah hjhj kkhkjhj jhkhada", "blue").
                newLine().
                append("Third line of testing asjdkjka adalk adaad!").update();
        add(e);
        addField(new NumberField("Number"));
    }

    @Override
    protected boolean process() {
        close();
        return true;
    }
}
