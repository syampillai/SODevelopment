package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.PasswordField;

public class Test extends TemplateView implements CloseableView {

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

    public Test() {
        Span span = new Span("Hello World");
        span.getElement().setAttribute("slot", "stest");
        getElement().appendChild(span.getElement());
    }

    @Override
    protected Component createComponentForId(String id) {
        if("submit".equals(id)) {
            return new Button("Submit", VaadinIcon.FUNNEL,
                    e -> message.setText("Hello " + name.getValue() + " with password " + password.getValue() + "\n" + fn.getValue()));
        }
        return super.createComponentForId(id);
    }
}
