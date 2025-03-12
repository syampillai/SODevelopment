package com.storedobject.ui;

import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

/**
 * Template component can be used to define generic HTML-template based elements via HTML text-content. Use the
 * "so-template" tag and specify your text-content name as the "template" attribute in it!
 *
 * @author Syam
 */
@Tag("so-template")
@NpmPackage(value = "grapesjs", version = "^0.22.5")
@JsModule("./so/template/so-template.js")
public final class Template extends LitComponent {

    private boolean created = false;

    public Template() {
        getElement().setProperty("idTemplate", "soTemp" + ID.newID());
    }
    
    public Template(String templateName) {
        this();
        template(templateName);
    }

    private void template(String name) {
        if(name == null) {
            return;
        }
        created = true;
        getElement().appendChild(new TemplateLayout(name).getElement());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(!created) {
            template(getElement().getAttribute("template"));
        }
    }
}
