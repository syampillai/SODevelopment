package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.PasswordField;

public class Test extends DataForm {

    public Test() {
        super("Test", false);
        addField(new TemperatureField("Temp."));
        addField(new MeasurementUnitField("Unit"));
        add(new TestLayout());
    }

    @Override
    protected boolean process() {
        return false;
    }

    private static class TestLayout extends TemplateLayout {

        @Id
        private TextField name;

        @Id
        private PasswordField password;

        @Id
        private Button submit;

        @Id
        private Div message;

        @Id
        private TextField fn;

        public TestLayout() {
            super("LayoutTest");
            submit.addClickListener(event -> message.setText("Hello " + name.getValue() + " with password " + password.getValue() + "\n" + fn.getValue()));
            Span span = new Span("Hello World");
            span.getElement().setAttribute("slot", "stest");
            getElement().appendChild(span.getElement());
        }
    }
}
