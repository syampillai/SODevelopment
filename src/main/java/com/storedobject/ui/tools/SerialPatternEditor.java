package com.storedobject.ui.tools;

import com.storedobject.core.SerialPattern;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.*;

public class SerialPatternEditor extends ObjectEditor<SerialPattern> {

    private final Button test = new Button("Test", e -> test());
    private TestPatten testPatten;

    public SerialPatternEditor() {
        super(SerialPattern.class);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(test);
    }

    private void test() {
        if(testPatten == null) {
            testPatten = new TestPatten();
        }
        testPatten.execute();
    }

    private class TestPatten extends DataForm {

        private final TextField patternField = new TextField("Pattern");
        private final DateField dateField = new DateField("Date");
        private final LongField numberField = new LongField("Serial");
        private final TextField resultField = new TextField("Result");

        public TestPatten() {
            super("Test Pattern");
            patternField.addValueChangeListener(e -> result());
            dateField.addValueChangeListener(e -> result());
            numberField.addValueChangeListener(e -> result());
            addField(patternField, dateField, numberField, resultField);
            setFieldReadOnly(resultField);
            numberField.setValue(50L);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.remove(ok);
            cancel.setText("Close");
        }

        private void result() {
            resultField.setValue(SerialPattern.getNumber(getTransactionManager(), numberField.getValue(),
                    dateField.getValue(), patternField.getValue()));
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            SerialPattern sp = getObject();
            patternField.setValue(sp == null ? "" : sp.getPattern());
            super.execute(parent, doNotLock);
            result();
        }

        @Override
        protected boolean process() {
            return true;
        }
    }
}
