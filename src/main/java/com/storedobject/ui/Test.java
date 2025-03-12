package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.template.Id;

public class Test extends View implements CloseableView {

    public Test() {
        super("Test");
        TemplateComponent tc = new TemplateComponent("""
                <vaadin-text-field id = "streetAddress" label="Street Address" value="Ruukinkatu 2" clear-button-visible>
                  <vaadin-icon slot="prefix" icon="vaadin:map-marker"></vaadin-icon>
                </vaadin-text-field>
                """) {
            @Id
            private TextField streetAddress;

            @Override
            protected Component createComponentForId(String id, String tag) {
                if("streetAddress".equals(id)) {
                    TextField tf = new TextField();
                    tf.addValueChangeListener(e -> message("Value: " + streetAddress.getValue()));
                    return tf;
                }
                return super.createComponentForId(id, tag);
            }
        };
        setComponent(tc);
    }
}