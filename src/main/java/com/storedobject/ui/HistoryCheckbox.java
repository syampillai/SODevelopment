package com.storedobject.ui;

import com.storedobject.core.Database;
import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.vaadin.flow.component.checkbox.Checkbox;

import java.util.function.Consumer;

public class HistoryCheckbox extends Checkbox {

    private static final String LABEL = "Include History";
    private final DateField dateField = new DateField(LABEL + " from");
    private final Consumer<HistoryCheckbox> consumer;
    private HistoryLoader loader;

    public HistoryCheckbox(Consumer<HistoryCheckbox> consumer) {
        super(LABEL);
        dateField.setValue(DateUtility.startOfYear());
        this.consumer = consumer;
        addValueChangeListener(e -> {
            if(e.isFromClient()) {
                if(e.getValue()) {
                    if(loader == null) loader = new HistoryLoader();
                    loader.execute();
                } else {
                    setLabel(LABEL);
                    consumer.accept(this);
                }
            }
        });
    }

    public String getFilter(String attribute) {
        return attribute + ">='" + Database.format(dateField.getValue()) + "' ";
    }

    private class HistoryLoader extends DataForm {

        public HistoryLoader() {
            super(LABEL);
            addField(dateField);
        }

        @Override
        protected boolean process() {
            close();
            HistoryCheckbox.this.setLabel("History from " + DateUtility.format(dateField.getValue()));
            Application.get().access(() -> consumer.accept(HistoryCheckbox.this));
            return true;
        }

        @Override
        protected void cancel() {
            super.cancel();
            setValue(false);
        }
    }
}
